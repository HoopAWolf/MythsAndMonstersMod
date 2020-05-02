package com.hoopawolf.mwaw.entities;

import com.hoopawolf.mwaw.network.MWAWPacketHandler;
import com.hoopawolf.mwaw.network.packets.client.SpawnParticleMessage;
import com.hoopawolf.mwaw.util.RegistryHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.IFlyingAnimal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.UUID;

public class FairyEntity extends AnimalEntity implements IFlyingAnimal
{
    LinkedList<BlockPos> prevAttraction = new LinkedList<BlockPos>();
    private static final DataParameter<Boolean> ANGRY = EntityDataManager.createKey(FairyEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> RESTING = EntityDataManager.createKey(FairyEntity.class, DataSerializers.BOOLEAN);
    private UUID revengeTargetUUID;
    @Nullable
    private BlockPos flowerPos = null;
    private int inWaterTick;
    private float staminaRemaining;
    private final float maxStamina = 1000;

    public FairyEntity(EntityType<? extends FairyEntity> p_i225714_1_, World p_i225714_2_)
    {
        super(p_i225714_1_, p_i225714_2_);
        this.moveController = new FlyingMovementController(this, 20, true);
        this.setPathPriority(PathNodeType.WATER, -1.0F);
        this.setPathPriority(PathNodeType.COCOA, -1.0F);
        this.setPathPriority(PathNodeType.FENCE, -1.0F);
        this.staminaRemaining = this.maxStamina;
    }

    protected void registerData()
    {
        super.registerData();
        this.dataManager.register(ANGRY, false);
        this.dataManager.register(RESTING, false);
    }

    public float getBlockPathWeight(BlockPos pos, IWorldReader worldIn)
    {
        return worldIn.isAirBlock(pos) ? 10.0F : 0.0F;
    }

    protected void registerGoals()
    {
        this.goalSelector.addGoal(0, new FairyEntity.AttackGoal(this, (double) 1.4F, true));
        this.goalSelector.addGoal(1, new FairyEntity.RestingGoal());
        this.goalSelector.addGoal(6, new FairyEntity.GoToFlowerGoal());
        this.goalSelector.addGoal(8, new FairyEntity.WanderGoal());
        this.goalSelector.addGoal(9, new SwimGoal(this));
        this.goalSelector.addGoal(10, new LookAtGoal(this, PlayerEntity.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtGoal(this, MobEntity.class, 8.0F));

        this.targetSelector.addGoal(1, new FairyEntity.AttackDendroidGoal(this));
        this.targetSelector.addGoal(2, (new FairyEntity.AngerGoal(this)).setCallsForHelp(new Class[0]));
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isAngry()
    {
        return this.dataManager.get(ANGRY);
    }

    public void setAngry(boolean angry)
    {
        this.dataManager.set(ANGRY, angry);
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isResting()
    {
        return this.dataManager.get(RESTING);
    }

    public void setResting(boolean resting)
    {
        this.dataManager.set(RESTING, resting);
    }

    public void writeAdditional(CompoundNBT compound)
    {
        super.writeAdditional(compound);
        if (this.HasFlowerPos())
        {
            compound.put("FlowerPos", NBTUtil.writeBlockPos(this.GetFlowerPos()));
        }

        compound.putFloat("StaminaLeft", this.staminaRemaining);

        if (this.revengeTargetUUID != null)
        {
            compound.putString("HurtBy", this.revengeTargetUUID.toString());
        } else
        {
            compound.putString("HurtBy", "");
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readAdditional(CompoundNBT compound)
    {
        super.readAdditional(compound);
        this.flowerPos = null;
        if (compound.contains("FlowerPos"))
        {
            this.flowerPos = NBTUtil.readBlockPos(compound.getCompound("FlowerPos"));
        }

        if (compound.contains("StaminaLeft"))
        {
            this.staminaRemaining = compound.getFloat("StaminaLeft");
        } else
        {
            this.ResetStamina();
        }

        String s = compound.getString("HurtBy");
        if (!s.isEmpty())
        {
            this.revengeTargetUUID = UUID.fromString(s);
            PlayerEntity playerentity = this.world.getPlayerByUuid(this.revengeTargetUUID);
            this.setRevengeTarget(playerentity);
            if (playerentity != null)
            {
                this.attackingPlayer = playerentity;
                this.recentlyHit = this.getRevengeTimer();
            }
        }

    }

    public boolean attackEntityAsMob(Entity entityIn)
    {
        return entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), (float) this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue());
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void tick()
    {
        super.tick();
        if (!this.isResting())
            this.setNoGravity(true);
        else
        {
            this.setNoGravity(false);

            Vec3d vec3d = this.getMotion();
            if (!this.onGround && vec3d.y < 0.0D)
            {
                this.setMotion(vec3d.mul(1.0D, 0.6D, 1.0D));
            }
        }

        if (ticksExisted % 2 == 0 && !world.isRemote)
        {
            int _iteration = this.rand.nextInt(2);
            Vec3d _vec = new Vec3d(this.getPosX() - (double) 0.3F, this.getPosYHeight(0.5D), this.getPosZ() + (double) 0.3F);
            SpawnParticleMessage spawnParticleMessage = new SpawnParticleMessage(_vec, new Vec3d(0, -0.1f, 0), _iteration, 0, 0.5F);
            MWAWPacketHandler.INSTANCE.send(PacketDistributor.DIMENSION.with(() -> this.dimension), spawnParticleMessage);
        }
    }

    public float getBrightness()
    {
        return 1.0F;
    }

    private void MoveToPos(BlockPos p_226433_1_)
    {
        if (p_226433_1_ != null)
        {
            Vec3d vec3d = new Vec3d(p_226433_1_);
            int i = 0;
            BlockPos blockpos = new BlockPos(this);
            int j = (int) vec3d.y - blockpos.getY();
            if (j > 2)
            {
                i = 4;
            } else if (j < -2)
            {
                i = -4;
            }

            int k = 6;
            int l = 8;
            int i1 = blockpos.manhattanDistance(p_226433_1_);
            if (i1 < 15)
            {
                k = i1 / 2;
                l = i1 / 2;
            }

            Vec3d vec3d1 = RandomPositionGenerator.func_226344_b_(this, k, l, i, vec3d, (double) ((float) Math.PI / 15F));
            if (vec3d1 != null)
            {
                this.navigator.setRangeMultiplier(0.5F);
                this.navigator.tryMoveToXYZ(vec3d1.x, vec3d1.y, vec3d1.z, FairyEntity.this.getAttribute(SharedMonsterAttributes.FLYING_SPEED).getBaseValue());
                this.getLookController().setLookPosition(new Vec3d(GetFlowerPos()));

                if (ticksExisted % 5 == 0 && !this.world.isRemote)
                {
                    Vec3d _vec = new Vec3d(this.getPosX() - (double) 0.3F, this.getPosYHeight(0.5D), this.getPosZ() + (double) 0.3F);
                    SpawnParticleMessage spawnParticleMessage = new SpawnParticleMessage(_vec, new Vec3d(0, 0, 0), 1, 1, 0.5F);
                    MWAWPacketHandler.INSTANCE.send(PacketDistributor.DIMENSION.with(() -> this.dimension), spawnParticleMessage);
                }
            }
        }
    }

    @Nullable
    public BlockPos GetFlowerPos()
    {
        return this.flowerPos;
    }

    public boolean HasFlowerPos()
    {
        return this.GetFlowerPos() != null;
    }

    /**
     * Hint to AI tasks that we were attacked by the passed EntityLivingBase and should retaliate. Is not guaranteed to
     * change our actual active target (for example if we are currently busy attacking someone else)
     */
    public void setRevengeTarget(@Nullable LivingEntity livingBase)
    {
        super.setRevengeTarget(livingBase);
        if (livingBase != null)
        {
            this.revengeTargetUUID = livingBase.getUniqueID();
        }

    }

    protected void updateAITasks()
    {
        if (this.isInWaterOrBubbleColumn())
        {
            ++this.inWaterTick;
        } else
        {
            this.inWaterTick = 0;
        }

        if (this.inWaterTick > 20)
        {
            this.attackEntityFrom(DamageSource.DROWN, 1.0F);
        }

        if (!this.isResting())
        {
            if (this.staminaRemaining <= 0.0F)
            {
                this.staminaRemaining = 0.0F;
                this.setResting(true);
            }
        } else
        {
            if (this.staminaRemaining >= this.maxStamina)
            {
                this.ResetStamina();
                this.setResting(false);
            }
        }
    }

    private boolean IsBlockFar(BlockPos p_226437_1_)
    {
        return !this.IsWithinDistance(p_226437_1_, 15);
    }

    protected void registerAttributes()
    {
        super.registerAttributes();
        this.getAttributes().registerAttribute(SharedMonsterAttributes.FLYING_SPEED);
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(3.0D);
        this.getAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(1.7D);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
        this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0D);
        this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(12.0D);
    }

    /**
     * Returns new PathNavigateGround instance
     */
    protected PathNavigator createNavigator(World worldIn)
    {
        FlyingPathNavigator flyingpathnavigator = new FlyingPathNavigator(this, worldIn)
        {
            public boolean canEntityStandOnPos(BlockPos pos)
            {
                return !this.world.isAirBlock(pos.down());
            }

            public void tick()
            {
                if (!FairyEntity.this.isResting())
                {
                    super.tick();
                }
            }
        };
        flyingpathnavigator.setCanOpenDoors(false);
        flyingpathnavigator.setCanSwim(false);
        flyingpathnavigator.setCanEnterDoors(true);
        return flyingpathnavigator;
    }

    private boolean IsFlowerBlock(BlockPos p_226439_1_)
    {
        return this.world.isBlockPresent(p_226439_1_) && (this.world.getBlockState(p_226439_1_).getBlock().isIn(BlockTags.FLOWERS) || this.world.getBlockState(p_226439_1_).getBlock() == RegistryHandler.FAIRY_MUSHROOM_BLOCK.get());
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn)
    {
    }

    protected SoundEvent getAmbientSound()
    {
        return null;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP;
    }

    protected SoundEvent getDeathSound()
    {
        return SoundEvents.BLOCK_FIRE_EXTINGUISH;
    }

    /**
     * Returns the volume for the sounds this mob makes.
     */
    protected float getSoundVolume()
    {
        return 0.4F;
    }

    @Nullable
    public BeeEntity createChild(AgeableEntity ageable)
    {
        return null;
    }

    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn)
    {
        return sizeIn.height * 0.8F;
    }

    public boolean onLivingFall(float distance, float damageMultiplier)
    {
        return false;
    }

    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos)
    {
    }

    protected boolean makeFlySound()
    {
        return true;
    }

    public boolean SetRevengeTarget(Entity _entity)
    {
        if (_entity instanceof LivingEntity)
        {
            this.setRevengeTarget((LivingEntity) _entity);
        }

        return true;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (this.isInvulnerableTo(source))
        {
            return false;
        } else
        {
            Entity entity = source.getTrueSource();
            this.ResetStamina();
            if (!this.world.isRemote && entity instanceof PlayerEntity && !((PlayerEntity) entity).isCreative() && this.canEntityBeSeen(entity) && !this.isAIDisabled())
            {
                this.SetRevengeTarget(entity);
            }

            return super.attackEntityFrom(source, amount);
        }
    }

    public CreatureAttribute getCreatureAttribute()
    {
        return CreatureAttribute.UNDEFINED;
    }

    protected void handleFluidJump(Tag<Fluid> fluidTag)
    {
        this.setMotion(this.getMotion().add(0.0D, 0.01D, 0.0D));
    }

    private boolean IsWithinDistance(BlockPos p_226401_1_, int p_226401_2_)
    {
        return p_226401_1_.withinDistance(new BlockPos(this), (double) p_226401_2_);
    }

    class AngerGoal extends HurtByTargetGoal
    {
        AngerGoal(FairyEntity p_i225726_2_)
        {
            super(p_i225726_2_);
        }

        protected void setAttackTarget(MobEntity mobIn, LivingEntity targetIn)
        {
            if (mobIn instanceof FairyEntity && this.goalOwner.canEntityBeSeen(targetIn) && ((FairyEntity) mobIn).SetRevengeTarget(targetIn))
            {
                mobIn.setAttackTarget(targetIn);
            }
        }
    }

    public class AttackDendroidGoal extends NearestAttackableTargetGoal<DendroidEntity>
    {
        AttackDendroidGoal(FairyEntity p_i225719_1_)
        {
            super(p_i225719_1_, DendroidEntity.class, true);
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean shouldExecute()
        {
            return super.shouldExecute();
        }

        public void startExecuting()
        {
            FairyEntity.this.ResetStamina();
            super.startExecuting();
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean shouldContinueExecuting()
        {
            if (this.goalOwner.getAttackTarget() != null && this.goalOwner.getAttackTarget() instanceof DendroidEntity)
            {
                return super.shouldContinueExecuting();
            } else
            {
                this.target = null;
                return false;
            }
        }
    }

    public class GoToFlowerGoal extends FairyEntity.PassiveGoal
    {
        private int goToFlowerTimer = FairyEntity.this.world.rand.nextInt(10);

        GoToFlowerGoal()
        {
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean func_225506_g_()
        {
            return FairyEntity.this.flowerPos != null && FairyEntity.this.IsFlowerBlock(FairyEntity.this.flowerPos) && !FairyEntity.this.IsWithinDistance(FairyEntity.this.flowerPos, 2);
        }

        public boolean func_225507_h_()
        {
            return this.func_225506_g_();
        }

        public boolean shouldExecute()
        {
            return super.shouldExecute();
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void startExecuting()
        {
            this.goToFlowerTimer = 0;
            super.startExecuting();
        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        public void resetTask()
        {
            this.goToFlowerTimer = 0;
            FairyEntity.this.navigator.clearPath();
            FairyEntity.this.navigator.resetRangeMultiplier();

            if (HasFlowerPos())
            {
                FairyEntity.this.prevAttraction.add(new BlockPos(GetFlowerPos()));
                FairyEntity.this.flowerPos = null;
            }

            if (FairyEntity.this.prevAttraction.size() > 10)
            {
                FairyEntity.this.prevAttraction.removeFirst();
            }
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void tick()
        {
            if (FairyEntity.this.flowerPos != null)
            {
                FairyEntity.this.DecreaseStamina();
                ++this.goToFlowerTimer;

                if (this.goToFlowerTimer > 300)
                {
                    FairyEntity.this.prevAttraction.add(new BlockPos(GetFlowerPos()));
                    FairyEntity.this.flowerPos = null;

                    if (FairyEntity.this.prevAttraction.size() > 10)
                    {
                        FairyEntity.this.prevAttraction.removeFirst();
                    }

                } else if (!FairyEntity.this.navigator.func_226337_n_())
                {
                    if (FairyEntity.this.IsBlockFar(FairyEntity.this.flowerPos))
                    {
                        FairyEntity.this.flowerPos = null;
                    } else
                    {
                        FairyEntity.this.MoveToPos(FairyEntity.this.flowerPos);
                    }
                }
            }
        }
    }

    abstract class PassiveGoal extends Goal
    {
        private PassiveGoal()
        {
        }

        public abstract boolean func_225506_g_();

        public abstract boolean func_225507_h_();

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean shouldExecute()
        {
            return this.func_225506_g_();
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean shouldContinueExecuting()
        {
            return this.func_225507_h_();
        }
    }

    class AttackGoal extends MeleeAttackGoal
    {
        private final FairyEntity parentEntity;

        AttackGoal(FairyEntity p_i225718_2_, double p_i225718_3_, boolean p_i225718_5_)
        {
            super(p_i225718_2_, p_i225718_3_, p_i225718_5_);
            parentEntity = p_i225718_2_;
        }

        public void resetTask()
        {
            this.parentEntity.setAngry(false);
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean shouldExecute()
        {
            return super.shouldExecute();
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean shouldContinueExecuting()
        {
            return super.shouldContinueExecuting();
        }

        public void tick()
        {
            LivingEntity livingentity = FairyEntity.this.getAttackTarget();
            if (FairyEntity.this.getBoundingBox().intersects(livingentity.getBoundingBox()))
            {
                FairyEntity.this.attackEntityAsMob(livingentity);
            } else
            {
                Vec3d vec3d = livingentity.getEyePosition(1.0F);
                FairyEntity.this.moveController.setMoveTo(vec3d.x, vec3d.y, vec3d.z, FairyEntity.this.getAttribute(SharedMonsterAttributes.FLYING_SPEED).getBaseValue() * 2);
            }

            this.parentEntity.setAngry(true);
        }
    }

    class WanderGoal extends Goal
    {
        WanderGoal()
        {
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean shouldExecute()
        {
            return FairyEntity.this.navigator.noPath() && FairyEntity.this.rand.nextInt(10) == 0 && !FairyEntity.this.isResting();
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean shouldContinueExecuting()
        {
            return FairyEntity.this.navigator.func_226337_n_() && !FairyEntity.this.isResting();
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void startExecuting()
        {
            Vec3d vec3d = this.GetNextPosViaForward();
            if (vec3d != null)
            {
                FairyEntity.this.navigator.setPath(FairyEntity.this.navigator.getPathToPos(new BlockPos(vec3d), 1), FairyEntity.this.getAttribute(SharedMonsterAttributes.FLYING_SPEED).getBaseValue());
            }

        }

        public void tick()
        {
            FairyEntity.this.DecreaseStamina();

            if (!FairyEntity.this.HasFlowerPos())
            {
                int bound = 7;
                int yBound = 3;
                for (int y = -yBound; y < 0; ++y)
                {
                    for (int x = -bound; x < bound; ++x)
                    {
                        for (int z = -bound; z < bound; ++z)
                        {
                            boolean getOutFlag = false;
                            BlockPos pos = new BlockPos(FairyEntity.this.getPosX() + x, FairyEntity.this.getPosY() + y, FairyEntity.this.getPosZ() + z);

                            for (BlockPos _blockPos : FairyEntity.this.prevAttraction)
                            {
                                if (_blockPos != null && _blockPos.equals(pos))
                                {
                                    getOutFlag = true;
                                    break;
                                }
                            }

                            if (getOutFlag)
                            {
                                break;
                            }

                            if (IsFlowerBlock(pos))
                            {
                                FairyEntity.this.flowerPos = pos;
                                break;
                            }
                        }

                        if (FairyEntity.this.GetFlowerPos() != null)
                        {
                            break;
                        }
                    }

                    if (FairyEntity.this.GetFlowerPos() != null)
                    {
                        break;
                    }
                }
            }
        }

        @Nullable
        private Vec3d GetNextPosViaForward()
        {
            Vec3d vec3d = FairyEntity.this.getLook(0.0F);

            Vec3d vec3d2 = RandomPositionGenerator.findAirTarget(FairyEntity.this, 8, 7, FairyEntity.this.getLook(0.0F), ((float) Math.PI / 2F), 2, 1);
            return vec3d2 != null ? vec3d2 : RandomPositionGenerator.findGroundTarget(FairyEntity.this, 8, 4, -2, vec3d, (double) ((float) Math.PI / 2F));
        }
    }

    class RestingGoal extends Goal
    {

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean shouldExecute()
        {
            return FairyEntity.this.isResting();
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean shouldContinueExecuting()
        {
            return FairyEntity.this.isResting();
        }

        public void startExecuting()
        {
            FairyEntity.this.navigator.clearPath();
            super.startExecuting();
        }

        public void tick()
        {
            FairyEntity.this.IncreaseStamina();
        }
    }

    protected void DecreaseStamina()
    {
        if (rand.nextInt(50) < 20)
            --staminaRemaining;
    }

    protected void IncreaseStamina()
    {
        ++staminaRemaining;
    }

    protected void ResetStamina()
    {
        staminaRemaining = maxStamina;
    }
}
//TODO Ride fairy jar entity. Have a hunger state if is riding jar. if hungry, becoe ANGERY. try to break jar. get riding on and damage. right click jar to get item. right click jar with sugar to calm the fairy hunger. Fairy pet maybe