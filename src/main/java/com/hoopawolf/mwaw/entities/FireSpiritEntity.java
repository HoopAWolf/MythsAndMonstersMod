package com.hoopawolf.mwaw.entities;

import com.hoopawolf.mwaw.network.MWAWPacketHandler;
import com.hoopawolf.mwaw.network.packets.client.SpawnParticleMessage;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.monster.BlazeEntity;
import net.minecraft.entity.monster.PillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class FireSpiritEntity extends CreatureEntity
{
    protected static final DataParameter<Byte> CHARGE_FLAG = EntityDataManager.createKey(FireSpiritEntity.class, DataSerializers.BYTE);
    private LivingEntity owner;
    @Nullable
    private BlockPos boundOrigin;

    public FireSpiritEntity(EntityType<? extends FireSpiritEntity> p_i50190_1_, World p_i50190_2_)
    {
        super(p_i50190_1_, p_i50190_2_);
        this.moveController = new FireSpiritEntity.MoveHelperController(this);
    }

    @Override
    protected void registerAttributes()
    {
        super.registerAttributes();
        this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);

        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(2.0D);
    }

    @Override
    public void move(MoverType typeIn, Vec3d pos)
    {
        super.move(typeIn, pos);
        this.doBlockCollisions();
    }

    @Override
    public void tick()
    {
        this.noClip = true;
        super.tick();
        this.noClip = false;
        this.setNoGravity(true);

        if (!world.isRemote)
        {
            SpawnParticleMessage spawnParticleMessage = new SpawnParticleMessage(new Vec3d(this.getPosX(), this.getPosY(), this.getPosZ()), new Vec3d(0.0F, 0.0D, 0.0F), 2, 9, 0);
            MWAWPacketHandler.packetHandler.sendToDimension(this.world.dimension.getType(), spawnParticleMessage);

            if (owner == null)
            {
                this.remove();
            }

            if (getAttackTarget() != null)
            {
                if (!getAttackTarget().isAlive())
                {
                    remove();
                }
            }
        }
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new FireSpiritEntity.ChargeAttackGoal());
        this.goalSelector.addGoal(8, new FireSpiritEntity.MoveRandomGoal());
        this.goalSelector.addGoal(9, new LookAtGoal(this, PlayerEntity.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtGoal(this, MobEntity.class, 8.0F));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, CreatureEntity.class, 10, true, false, (p_213621_0_) ->
        {
            return !(p_213621_0_ instanceof PillagerEntity) && !(p_213621_0_ instanceof PyromancerEntity) && !(p_213621_0_ instanceof BlazeEntity) && !(p_213621_0_ instanceof FireSpiritEntity);  //TODO future cult member
        }));
    }

    @Override
    protected void registerData()
    {
        super.registerData();
        this.dataManager.register(CHARGE_FLAG, (byte) 0);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    public void readAdditional(CompoundNBT compound)
    {
        super.readAdditional(compound);
        if (compound.contains("BoundX"))
        {
            this.boundOrigin = new BlockPos(compound.getInt("BoundX"), compound.getInt("BoundY"), compound.getInt("BoundZ"));
        }
    }

    @Override
    public void writeAdditional(CompoundNBT compound)
    {
        super.writeAdditional(compound);
        if (this.boundOrigin != null)
        {
            compound.putInt("BoundX", this.boundOrigin.getX());
            compound.putInt("BoundY", this.boundOrigin.getY());
            compound.putInt("BoundZ", this.boundOrigin.getZ());
        }
    }

    public LivingEntity getOwner()
    {
        return this.owner;
    }

    public void setOwner(LivingEntity ownerIn)
    {
        this.owner = ownerIn;
    }

    @Nullable
    public BlockPos getBoundOrigin()
    {
        return (this.owner != null ? this.owner.getPosition() : null);
    }

    private boolean getChargeFlag(int mask)
    {
        int i = this.dataManager.get(CHARGE_FLAG);
        return (i & mask) != 0;
    }

    private void setChargeFlag(int mask, boolean value)
    {
        int i = this.dataManager.get(CHARGE_FLAG);
        if (value)
        {
            i = i | mask;
        } else
        {
            i = i & ~mask;
        }

        this.dataManager.set(CHARGE_FLAG, (byte) (i & 255));
    }

    public boolean isCharging()
    {
        return this.getChargeFlag(1);
    }

    public void setCharging(boolean charging)
    {
        this.setChargeFlag(1, charging);
    }

    @Override
    public float getBrightness()
    {
        return 1.0F;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (!world.isRemote)
        {
            if (source.getImmediateSource() != null)
            {
                owner.attackEntityFrom(source, amount * 0.2F);
                return super.attackEntityFrom(source, amount);
            }
        }

        return false;
    }

    class ChargeAttackGoal extends Goal
    {
        public ChargeAttackGoal()
        {
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean shouldExecute()
        {
            return FireSpiritEntity.this.getAttackTarget() != null && FireSpiritEntity.this.rand.nextInt(3) == 0;
        }

        @Override
        public boolean shouldContinueExecuting()
        {
            return FireSpiritEntity.this.getMoveHelper().isUpdating() && FireSpiritEntity.this.isCharging() && FireSpiritEntity.this.getAttackTarget() != null && FireSpiritEntity.this.getAttackTarget().isAlive();
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        @Override
        public void startExecuting()
        {
            LivingEntity livingentity = FireSpiritEntity.this.getAttackTarget();
            Vec3d Vec3d = livingentity.getEyePosition(1.0F);
            FireSpiritEntity.this.moveController.setMoveTo(Vec3d.x, Vec3d.y, Vec3d.z, 1.0D);
            FireSpiritEntity.this.setCharging(true);
            FireSpiritEntity.this.playSound(SoundEvents.ENTITY_PUFFER_FISH_BLOW_OUT, 1.0F, 0.5F);
        }

        @Override
        public void resetTask()
        {
            FireSpiritEntity.this.setCharging(false);
        }

        @Override
        public void tick()
        {
            LivingEntity livingentity = FireSpiritEntity.this.getAttackTarget();
            if (FireSpiritEntity.this.getBoundingBox().intersects(livingentity.getBoundingBox()))
            {
                FireSpiritEntity.this.attackEntityAsMob(livingentity);
                livingentity.setFire(100);
                FireSpiritEntity.this.setCharging(false);
            } else
            {
                Vec3d Vec3d = livingentity.getEyePosition(1.0F);
                FireSpiritEntity.this.moveController.setMoveTo(Vec3d.x, Vec3d.y, Vec3d.z, 1.0D);
            }

        }
    }

    class MoveHelperController extends MovementController
    {
        public MoveHelperController(FireSpiritEntity vex)
        {
            super(vex);
        }

        @Override
        public void tick()
        {
            if (this.action == MovementController.Action.MOVE_TO)
            {
                Vec3d Vec3d = new Vec3d(this.posX - FireSpiritEntity.this.getPosX(), this.posY - FireSpiritEntity.this.getPosY(), this.posZ - FireSpiritEntity.this.getPosZ());
                double d0 = Vec3d.length();
                if (d0 < FireSpiritEntity.this.getBoundingBox().getAverageEdgeLength())
                {
                    this.action = MovementController.Action.WAIT;
                    FireSpiritEntity.this.setMotion(FireSpiritEntity.this.getMotion().scale(0.5D));
                } else
                {
                    FireSpiritEntity.this.setMotion(FireSpiritEntity.this.getMotion().add(Vec3d.scale(this.speed * 0.05D / d0)));
                    if (FireSpiritEntity.this.getAttackTarget() == null)
                    {
                        Vec3d Vec3d1 = FireSpiritEntity.this.getMotion();
                        FireSpiritEntity.this.rotationYaw = -((float) MathHelper.atan2(Vec3d1.x, Vec3d1.z)) * (180F / (float) Math.PI);
                    } else
                    {
                        double d2 = FireSpiritEntity.this.getAttackTarget().getPosX() - FireSpiritEntity.this.getPosX();
                        double d1 = FireSpiritEntity.this.getAttackTarget().getPosZ() - FireSpiritEntity.this.getPosZ();
                        FireSpiritEntity.this.rotationYaw = -((float) MathHelper.atan2(d2, d1)) * (180F / (float) Math.PI);
                    }
                    FireSpiritEntity.this.renderYawOffset = FireSpiritEntity.this.rotationYaw;
                }

            }
        }
    }

    class MoveRandomGoal extends Goal
    {
        public MoveRandomGoal()
        {
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        @Override
        public boolean shouldExecute()
        {
            return !FireSpiritEntity.this.getMoveHelper().isUpdating() && FireSpiritEntity.this.rand.nextInt(7) == 0;
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        @Override
        public boolean shouldContinueExecuting()
        {
            return false;
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        @Override
        public void tick()
        {
            BlockPos blockpos = FireSpiritEntity.this.getBoundOrigin();
            if (blockpos == null)
            {
                blockpos = FireSpiritEntity.this.getPosition();
            }

            for (int i = 0; i < 3; ++i)
            {
                BlockPos blockpos1 = blockpos.add(FireSpiritEntity.this.rand.nextInt(10) - 4, FireSpiritEntity.this.rand.nextInt(10) - 4, FireSpiritEntity.this.rand.nextInt(10) - 4);
                if (FireSpiritEntity.this.world.isAirBlock(blockpos1))
                {
                    FireSpiritEntity.this.moveController.setMoveTo((double) blockpos1.getX() + 0.5D, (double) blockpos1.getY() + 0.5D, (double) blockpos1.getZ() + 0.5D, 0.25D);
                    if (FireSpiritEntity.this.getAttackTarget() == null)
                    {
                        FireSpiritEntity.this.getLookController().setLookPosition((double) blockpos1.getX() + 0.5D, (double) blockpos1.getY() + 0.5D, (double) blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
                    }
                    break;
                }
            }

        }
    }
}
