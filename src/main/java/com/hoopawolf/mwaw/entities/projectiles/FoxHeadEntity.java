package com.hoopawolf.mwaw.entities.projectiles;

import com.hoopawolf.mwaw.network.MWAWPacketHandler;
import com.hoopawolf.mwaw.network.packets.client.SpawnSuckingParticleMessage;
import com.hoopawolf.mwaw.util.EntityRegistryHandler;
import com.hoopawolf.mwaw.util.ParticleRegistryHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.*;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;

public class FoxHeadEntity extends DamagingProjectileEntity
{
    private LivingEntity owner;
    private Entity target;
    private Direction direction;
    private double targetDeltaX;
    private double targetDeltaY;
    private double targetDeltaZ;
    private float startTimer;

    public FoxHeadEntity(EntityType<? extends FoxHeadEntity> p_i50161_1_, World p_i50161_2_)
    {
        super(p_i50161_1_, p_i50161_2_);
        this.noClip = true;
        this.setNoGravity(true);
        startTimer = 20.0F;
    }

    public FoxHeadEntity(World worldIn, double x, double y, double z, double motionXIn, double motionYIn, double motionZIn)
    {
        this(EntityRegistryHandler.FOX_HEAD_ENTITY.get(), worldIn);
        this.setLocationAndAngles(x, y, z, this.rotationYaw, this.rotationPitch);
        this.setMotion(motionXIn, motionYIn, motionZIn);
    }

    public FoxHeadEntity(World worldIn, LivingEntity ownerIn, Entity targetIn)
    {
        this(EntityRegistryHandler.FOX_HEAD_ENTITY.get(), worldIn);
        this.owner = ownerIn;
        BlockPos blockpos = new BlockPos(ownerIn);
        double d0 = (double) blockpos.getX() + 0.5D;
        double d1 = (double) blockpos.getY() + 0.5D;
        double d2 = (double) blockpos.getZ() + 0.5D;
        this.setLocationAndAngles(d0, d1, d2, this.rotationYaw, this.rotationPitch);
        this.target = targetIn;
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
        if (startTimer <= 0)
        {
            startTimer = 0;

            if (!world.isRemote)
            {
                if (target != null && target.isAlive())
                {
                    Vec3d _dir = new Vec3d(target.getPositionVec().getX(), target.getPositionVec().getY() + 0.25F, target.getPositionVec().getZ()).subtract(this.getPositionVec());
                    this.setMotion(MathHelper.signum(_dir.getX()) * 0.25F, MathHelper.signum(_dir.getY()) * 0.25F, MathHelper.signum(_dir.getZ()) * 0.25F);
                } else
                {
                    setDead();
                }
            }
        } else
        {
            if (ticksExisted % 2 == 0)
                --startTimer;

            if (!world.isRemote)
            {
                SpawnSuckingParticleMessage spawnParticleMessage = new SpawnSuckingParticleMessage(new Vec3d(getPosX(), getPosY() + 0.5F, getPosZ()), new Vec3d(0.1D, 0.1D, 0.1D), 10, 0, 0.5F);
                MWAWPacketHandler.packetHandler.sendToDimension(this.dimension, spawnParticleMessage);
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
        return ParticleRegistryHandler.GREEN_FLAME_PARTICLE.get();
    }

    @Override
    protected void onImpact(RayTraceResult result)
    {
        super.onImpact(result);
        if (!this.world.isRemote && startTimer <= 0)
        {
            if (result.getType() == RayTraceResult.Type.ENTITY)
            {
                Entity entity = ((EntityRayTraceResult) result).getEntity();
                if (entity != this.owner)
                {
                    if (entity instanceof PlayerEntity && !((PlayerEntity) entity).isCreative())
                    {
                        ((PlayerEntity) entity).dropItem(((PlayerEntity) entity).inventory.getStackInSlot(((PlayerEntity) entity).inventory.currentItem), true, false);
                        ((PlayerEntity) entity).inventory.removeStackFromSlot(((PlayerEntity) entity).inventory.currentItem);
                    }

                    this.playSound(SoundEvents.ENTITY_FOX_SCREECH, 1.0F, 1.0F);

                    entity.attackEntityFrom(DamageSource.causeMobDamage(this.owner), 8.0F);

                    int i = 0;
                    if (this.world.getDifficulty() == Difficulty.NORMAL)
                    {
                        i = 10;
                    } else if (this.world.getDifficulty() == Difficulty.HARD)
                    {
                        i = 40;
                    }

                    if (i > 0)
                    {
                        ((LivingEntity) entity).addPotionEffect(new EffectInstance(Effects.BLINDNESS, 20 * i, 1));
                    }

                    this.setDead();
                }
            }
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (!this.world.isRemote)
        {
            this.playSound(SoundEvents.ENTITY_SHULKER_BULLET_HURT, 1.0F, 1.0F);
            ((ServerWorld) this.world).spawnParticle(ParticleTypes.CRIT, this.getPosX(), this.getPosY(), this.getPosZ(), 15, 0.2D, 0.2D, 0.2D, 0.0D);
            this.remove();
        }

        return true;
    }

    private void setDead()
    {
        if (!world.isRemote)
        {
            this.playSound(SoundEvents.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, 1.0F, 1.0F);
            SpawnSuckingParticleMessage spawnParticleMessage = new SpawnSuckingParticleMessage(this.getPositionVec(), new Vec3d(0.1D, 0.1D, 0.1D), 10, 0, 0.5F);
            MWAWPacketHandler.packetHandler.sendToDimension(this.dimension, spawnParticleMessage);
        }

        this.remove();
    }

    public float getSpawnPercentage()
    {
        return (100.0F - ((startTimer / 20.0F) * 100.0F)) * 0.01F;
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
