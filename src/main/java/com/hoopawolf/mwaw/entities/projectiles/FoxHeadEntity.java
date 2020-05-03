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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.UUID;

public class FoxHeadEntity extends DamagingProjectileEntity //TODO BLINDNESS AND DROP PLAYER MAIN HAND ITEM, MAYBE HAVE SOME HEALTH
{
    private LivingEntity owner;
    private Entity target;
    @Nullable
    private Direction direction;
    private double targetDeltaX;
    private double targetDeltaY;
    private double targetDeltaZ;
    @Nullable
    private UUID ownerUniqueId;
    @Nullable
    private UUID targetUniqueId;

    public FoxHeadEntity(EntityType<? extends FoxHeadEntity> p_i50161_1_, World p_i50161_2_)
    {
        super(p_i50161_1_, p_i50161_2_);
        this.noClip = true;
        this.setNoGravity(true);
    }

    @OnlyIn(Dist.CLIENT)
    public FoxHeadEntity(World worldIn, double x, double y, double z, double motionXIn, double motionYIn, double motionZIn)
    {
        this(EntityRegistryHandler.FOX_HEAD_ENTITY.get(), worldIn);
        this.setLocationAndAngles(x, y, z, this.rotationYaw, this.rotationPitch);
        this.setMotion(motionXIn, motionYIn, motionZIn);
    }

    public FoxHeadEntity(World worldIn, LivingEntity ownerIn, Entity targetIn, Direction.Axis p_i46772_4_)
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

    public SoundCategory getSoundCategory()
    {
        return SoundCategory.HOSTILE;
    }

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

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readAdditional(CompoundNBT compound)
    {
        this.targetDeltaX = compound.getDouble("TXD");
        this.targetDeltaY = compound.getDouble("TYD");
        this.targetDeltaZ = compound.getDouble("TZD");
        if (compound.contains("Dir", 99))
        {
            this.direction = Direction.byIndex(compound.getInt("Dir"));
        }

        if (compound.contains("Owner", 10))
        {
            CompoundNBT compoundnbt = compound.getCompound("Owner");
            this.ownerUniqueId = NBTUtil.readUniqueId(compoundnbt);
        }

        if (compound.contains("Target", 10))
        {
            CompoundNBT compoundnbt1 = compound.getCompound("Target");
            this.targetUniqueId = NBTUtil.readUniqueId(compoundnbt1);
        }

    }

    /**
     * Makes the entity despawn if requirements are reached
     */
    public void checkDespawn()
    {
        if (this.world.getDifficulty() == Difficulty.PEACEFUL)
        {
            this.remove();
        }
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void tick()
    {
        super.tick();

        if (target != null)
        {
            Vec3d _dir = target.getPositionVec().subtract(this.getPositionVec()).normalize();
            this.setMotion(_dir.getX(), _dir.getY(), _dir.getZ());
        } else
        {
            setDead();
        }
    }

    protected float getMotionFactor()
    {
        return super.getMotionFactor();
    }

    /**
     * Returns true if the entity is on fire. Used by render to add the fire effect on rendering.
     */
    public boolean isBurning()
    {
        return false;
    }

    /**
     * Checks if the entity is in range to render.
     */
    @OnlyIn(Dist.CLIENT)
    public boolean isInRangeToRenderDist(double distance)
    {
        return distance < 16384.0D;
    }

    /**
     * Gets how bright this entity is.
     */
    public float getBrightness()
    {
        return 1.0F;
    }

    protected IParticleData getParticle()
    {
        return ParticleRegistryHandler.GREEN_FLAME_PARTICLE.get();
    }

    protected void onImpact(RayTraceResult result)
    {
        super.onImpact(result);
        if (!this.world.isRemote)
        {
            if (result.getType() == RayTraceResult.Type.ENTITY)
            {
                Entity entity = ((EntityRayTraceResult) result).getEntity();
                if (!(entity instanceof PlayerEntity && ((PlayerEntity) entity).isCreative()))
                {
                    this.playSound(SoundEvents.ENTITY_FOX_SCREECH, 1.0F, 1.0F);


                    if (this.shootingEntity != null)
                    {
                        if (entity.attackEntityFrom(DamageSource.causeMobDamage(this.shootingEntity), 8.0F))
                        {
                            if (entity.isAlive())
                            {
                                this.applyEnchantments(this.shootingEntity, entity);
                            }
                        }
                    }

                    if (entity instanceof LivingEntity)
                    {
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
                    }

                    this.setDead();
                }
            }
        }
    }

    /**
     * Called when the entity is attacked.
     */
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
            MWAWPacketHandler.INSTANCE.send(PacketDistributor.DIMENSION.with(() -> this.dimension), spawnParticleMessage);
        }

        this.remove();
    }

    protected boolean isFireballFiery()
    {
        return false;
    }

    public IPacket<?> createSpawnPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
