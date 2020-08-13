package com.hoopawolf.mwaw.entities.projectiles;

import com.hoopawolf.mwaw.entities.PyromancerEntity;
import com.hoopawolf.mwaw.network.MWAWPacketHandler;
import com.hoopawolf.mwaw.network.packets.client.SpawnParticleMessage;
import com.hoopawolf.mwaw.network.packets.client.SpawnSuckingParticleMessage;
import com.hoopawolf.mwaw.util.EntityRegistryHandler;
import com.hoopawolf.mwaw.util.ParticleRegistryHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.*;
import net.minecraft.world.Difficulty;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public class SpiritBombEntity extends DamagingProjectileEntity
{
    private static final DataParameter<Integer> CHARGE_TIMER = EntityDataManager.createKey(SpiritBombEntity.class, DataSerializers.VARINT);
    public int innerRotation;
    private LivingEntity owner;
    private Entity target;
    private Direction direction;
    private double targetDeltaX;
    private double targetDeltaY;
    private double targetDeltaZ;
    private boolean hasShot;

    public SpiritBombEntity(EntityType<? extends SpiritBombEntity> p_i50161_1_, World p_i50161_2_)
    {
        super(p_i50161_1_, p_i50161_2_);
        this.setNoGravity(true);
        innerRotation = 0;
        hasShot = false;
    }

    @OnlyIn(Dist.CLIENT)
    public SpiritBombEntity(World worldIn, double x, double y, double z, double motionXIn, double motionYIn, double motionZIn)
    {
        this(EntityRegistryHandler.SPIRIT_BOMB_ENTITY.get(), worldIn);
        this.setLocationAndAngles(x, y, z, this.rotationYaw, this.rotationPitch);
        this.setMotion(motionXIn, motionYIn, motionZIn);
    }

    public SpiritBombEntity(World worldIn, LivingEntity ownerIn, Entity targetIn)
    {
        this(EntityRegistryHandler.SPIRIT_BOMB_ENTITY.get(), worldIn);
        this.owner = ownerIn;
        BlockPos blockpos = new BlockPos(ownerIn);
        double d0 = (double) blockpos.getX() + 0.5D;
        double d1 = (double) blockpos.getY() + 0.5D;
        double d2 = (double) blockpos.getZ() + 0.5D;
        this.setLocationAndAngles(d0, d1, d2, this.rotationYaw, this.rotationPitch);
        this.target = targetIn;
    }

    @Override
    protected void registerData()
    {
        super.registerData();
        this.dataManager.register(CHARGE_TIMER, 0);
    }

    public int getChargeTimer()
    {
        return this.dataManager.get(CHARGE_TIMER);
    }

    public void setChargeTimer(int timerIn)
    {
        this.dataManager.set(CHARGE_TIMER, timerIn);
    }

    @Override
    public SoundCategory getSoundCategory()
    {
        return SoundCategory.HOSTILE;
    }

    @Override
    public void writeAdditional(CompoundNBT compound)
    {
        if (this.owner != null)
        {
            BlockPos blockpos = new BlockPos(this.owner);
            CompoundNBT compoundnbt = NBTUtil.writeUniqueId(this.owner.getUniqueID());
            compoundnbt.putInt("X", blockpos.getX());
            compoundnbt.putInt("Y", blockpos.getY());
            compoundnbt.putInt("Z", blockpos.getZ());
            compound.put("Owner", compoundnbt);
        }

        if (this.target != null)
        {
            BlockPos blockpos1 = new BlockPos(this.target);
            CompoundNBT compoundnbt1 = NBTUtil.writeUniqueId(this.target.getUniqueID());
            compoundnbt1.putInt("X", blockpos1.getX());
            compoundnbt1.putInt("Y", blockpos1.getY());
            compoundnbt1.putInt("Z", blockpos1.getZ());
            compound.put("Target", compoundnbt1);
        }

        if (this.direction != null)
        {
            compound.putInt("Dir", this.direction.getIndex());
        }

        compound.putDouble("TXD", this.targetDeltaX);
        compound.putDouble("TYD", this.targetDeltaY);
        compound.putDouble("TZD", this.targetDeltaZ);
    }

    @Override
    public void readAdditional(CompoundNBT compound)
    {
        this.targetDeltaX = compound.getDouble("TXD");
        this.targetDeltaY = compound.getDouble("TYD");
        this.targetDeltaZ = compound.getDouble("TZD");
        if (compound.contains("Dir", 99))
        {
            this.direction = Direction.byIndex(compound.getInt("Dir"));
        }
    }

    @Override
    public void checkDespawn()
    {
        if (this.world.getDifficulty() == Difficulty.PEACEFUL)
        {
            this.remove();
        }
    }

    @Override
    public void tick()
    {
        super.tick();
        ++innerRotation;

        if (!world.isRemote)
        {
            if (getChargeTimer() < 100)
            {
                if (!(owner instanceof PyromancerEntity && owner.isAlive()))
                {
                    setDead();
                }
            }

            Vec3d _vec = new Vec3d(this.getPosX(), this.getPosYHeight(0.5D), this.getPosZ());
            SpawnParticleMessage spawnParticleMessage = new SpawnParticleMessage(_vec, new Vec3d(0, 0, 0), 1, 10, getWidth() * getCharge());
            MWAWPacketHandler.packetHandler.sendToDimension(this.dimension, spawnParticleMessage);

            if (hasShot)
            {
                if (target != null)
                {
                    if (target instanceof LivingEntity)
                    {
                        float f = 0.01F;
                        double d1 = target.getPosX() - this.getPosX();
                        double d2 = target.getPosYHeight(0.5D) - this.getPosYHeight(0.5D);
                        double d3 = target.getPosZ() - this.getPosZ();
                        this.accelerationX = MathHelper.signum(d1) * (double) f;
                        this.accelerationY = MathHelper.signum(d2) * (double) f;
                        this.accelerationZ = MathHelper.signum(d3) * (double) f;
                    } else
                    {
                        setDead();
                    }
                } else
                {
                    setDead();
                }
            }
        }
    }

    @Override
    protected float getMotionFactor()
    {
        return 1.0F;
    }

    @Override
    public boolean isBurning()
    {
        return false;
    }

    @Override
    public boolean isInRangeToRenderDist(double distance)
    {
        return distance < 16384.0D;
    }

    @Override
    public float getBrightness()
    {
        return 1.0F;
    }

    @Override
    protected IParticleData getParticle()
    {
        return ParticleRegistryHandler.FIRE_PARTICLE.get();
    }

    private void setDead()
    {
        if (!world.isRemote)
        {
            boolean flag = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, this.shootingEntity);
            this.world.createExplosion(null, this.getPosX(), this.getPosY(), this.getPosZ(), 10.0F * getCharge(), flag, flag ? Explosion.Mode.DESTROY : Explosion.Mode.NONE);
            this.remove();
        }
    }

    @Override
    protected void onImpact(RayTraceResult result)
    {
        super.onImpact(result);
        if (!this.world.isRemote)
        {
            if (result.getType() == RayTraceResult.Type.ENTITY)
            {
                Entity entity = ((EntityRayTraceResult) result).getEntity();
                if (!(entity instanceof PyromancerEntity))
                {
                    entity.attackEntityFrom(DamageSource.causeFireballDamage(this, this.shootingEntity), 20.0F * getCharge());
                    this.applyEnchantments(this.shootingEntity, entity);
                    this.setDead();
                }
            } else
            {
                this.setDead();
            }
        }
    }

    public void increaseCharge()
    {
        SpawnSuckingParticleMessage spawnParticleMessage = new SpawnSuckingParticleMessage(new Vec3d(getPosX(), getPosY() + 1.0F, getPosZ()), new Vec3d(0.1D, 0.1D, 0.1D), 10, 2, 0.5F);
        MWAWPacketHandler.packetHandler.sendToDimension(this.dimension, spawnParticleMessage);
        setChargeTimer(MathHelper.clamp(getChargeTimer() + 1, 0, 100));
    }

    public void setHaveShot(boolean hasShotIn)
    {
        hasShot = hasShotIn;
    }

    public void setTarget(Entity targetIn)
    {
        target = targetIn;
    }

    public float getCharge()
    {
        return (float) getChargeTimer() / 100.0F;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        return false;
    }

    @Override
    protected boolean isFireballFiery()
    {
        return false;
    }

    @Override
    public IPacket<?> createSpawnPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
