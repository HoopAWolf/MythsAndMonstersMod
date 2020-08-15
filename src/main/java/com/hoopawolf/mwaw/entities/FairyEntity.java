package com.hoopawolf.mwaw.entities;

import com.hoopawolf.mwaw.network.MWAWPacketHandler;
import com.hoopawolf.mwaw.network.packets.client.SpawnParticleMessage;
import com.hoopawolf.mwaw.util.ItemBlockRegistryHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
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
import net.minecraft.potion.Effects;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.UUID;

public class FairyEntity extends AnimalEntity implements IFlyingAnimal
{
    private static final DataParameter<Boolean> ANGRY = EntityDataManager.createKey(FairyEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> RESTING = EntityDataManager.createKey(FairyEntity.class, DataSerializers.BOOLEAN);
    private final float maxStamina = 1000;
    LinkedList<BlockPos> prevAttraction = new LinkedList<BlockPos>();
    private UUID revengeTargetUUID;
    private BlockPos flowerPos = null;
    private int inWaterTick;
    private float staminaRemaining;
    private int attackTimer;

    public FairyEntity(EntityType<? extends FairyEntity> p_i225714_1_, World p_i225714_2_)
    {
        super(p_i225714_1_, p_i225714_2_);
        this.moveController = new FlyingMovementController(this, 20, true);
        this.setPathPriority(PathNodeType.WATER, -1.0F);
        this.setPathPriority(PathNodeType.COCOA, -1.0F);
        this.setPathPriority(PathNodeType.FENCE, -1.0F);
        this.staminaRemaining = this.maxStamina;
    }

    public static AttributeModifierMap.MutableAttribute func_234321_m_()
    {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.FLYING_SPEED, 1.7D).createMutableAttribute(Attributes.MAX_HEALTH, 3.0D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 1.0D)
                .createMutableAttribute(Attributes.FOLLOW_RANGE, 12.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.0D);
    }

    @Override
    public AgeableEntity createChild(AgeableEntity ageable)
    {
        return null;
    }


    @Override
    protected void registerData()
    {
        super.registerData();
        this.dataManager.register(ANGRY, false);
        this.dataManager.register(RESTING, false);
    }

    @Override
    public float getBlockPathWeight(BlockPos pos, IWorldReader worldIn)
    {
        return worldIn.isAirBlock(pos) ? 10.0F : 0.0F;
    }

    @Override
    protected void registerGoals()
    {
        this.goalSelector.addGoal(0, new FairyEntity.AttackGoal(this, 1.4F, true));
        this.goalSelector.addGoal(1, new FairyEntity.RestingGoal());
        this.goalSelector.addGoal(6, new FairyEntity.GoToFlowerGoal());
        this.goalSelector.addGoal(8, new FairyEntity.WanderGoal());
        this.goalSelector.addGoal(9, new SwimGoal(this));
        this.goalSelector.addGoal(10, new LookAtGoal(this, PlayerEntity.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtGoal(this, MobEntity.class, 8.0F));

        this.targetSelector.addGoal(1, new FairyEntity.AttackDendroidGoal(this));
        this.targetSelector.addGoal(2, (new FairyEntity.AngerGoal(this)).setCallsForHelp());
    }

    public boolean isAngry()
    {
        return this.dataManager.get(ANGRY);
    }

    public void setAngry(boolean angry)
    {
        this.dataManager.set(ANGRY, angry);
    }

    public boolean isResting()
    {
        return this.dataManager.get(RESTING);
    }

    public void setResting(boolean resting)
    {
        this.dataManager.set(RESTING, resting);
    }

    public int getAttackTimer()
    {
        return this.attackTimer;
    }

    @Override
    public void writeAdditional(CompoundNBT compound)
    {
        super.writeAdditional(compound);
        if (this.HasFlowerPos())
        {
            compound.put("FlowerPos", NBTUtil.writeBlockPos(this.getFlowerPos()));
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

    @Override
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

    @Override
    public int getMaxSpawnedInChunk()
    {
        return 4;
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn)
    {
        this.attackTimer = 10;
        this.world.setEntityState(this, (byte) 4);

        return entityIn.attackEntityFrom(new EntityDamageSource("fairy", this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
    }

    @Override
    public void tick()
    {
        super.tick();

        if (this.attackTimer > 0)
        {
            --this.attackTimer;
        }

        if (!this.isResting())
            this.setNoGravity(true);
        else
        {
            this.setNoGravity(false);

            Vector3d vec3d = this.getMotion();
            if (!this.onGround && vec3d.y < 0.0D)
            {
                this.setMotion(vec3d.mul(1.0D, 0.6D, 1.0D));
            }
        }

        if (ticksExisted % 2 == 0 && !world.isRemote)
        {
            int _iteration = this.rand.nextInt(2);
            Vector3d _vec = new Vector3d(this.getPosX() - (double) 0.3F, this.getPosYHeight(0.5D), this.getPosZ() + (double) 0.3F);
            SpawnParticleMessage spawnParticleMessage = new SpawnParticleMessage(_vec, new Vector3d(0, -0.1f, 0), _iteration, 0, getWidth());
            MWAWPacketHandler.packetHandler.sendToDimension(this.world.func_234923_W_(), spawnParticleMessage);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handleStatusUpdate(byte id)
    {
        if (id == 4)
        {
            this.attackTimer = 10;
        } else
        {
            super.handleStatusUpdate(id);
        }
    }

    private void MoveToPos(BlockPos p_226433_1_)
    {
        if (p_226433_1_ != null)
        {
            Vector3d vec3d = new Vector3d(p_226433_1_.getX(), p_226433_1_.getY(), p_226433_1_.getZ());
            int i = 0;
            BlockPos blockpos = this.getPosition();
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

            Vector3d vec3d1 = RandomPositionGenerator.func_226344_b_(this, k, l, i, vec3d, (float) Math.PI / 15F);
            if (vec3d1 != null)
            {
                this.navigator.setRangeMultiplier(0.5F);
                this.navigator.tryMoveToXYZ(vec3d1.x, vec3d1.y, vec3d1.z, FairyEntity.this.getAttribute(Attributes.FLYING_SPEED).getBaseValue());
                this.getLookController().setLookPosition(new Vector3d(getFlowerPos().getX(), getFlowerPos().getY(), getFlowerPos().getZ()));

                if (ticksExisted % 5 == 0 && !this.world.isRemote)
                {
                    Vector3d _vec = new Vector3d(this.getPosX() - (double) 0.3F, this.getPosYHeight(0.5D), this.getPosZ() + (double) 0.3F);
                    SpawnParticleMessage spawnParticleMessage = new SpawnParticleMessage(_vec, new Vector3d(0, 0, 0), 1, 1, getWidth());
                    MWAWPacketHandler.packetHandler.sendToDimension(this.world.func_234923_W_(), spawnParticleMessage);
                }
            }
        }
    }

    public BlockPos getFlowerPos()
    {
        return this.flowerPos;
    }

    public boolean HasFlowerPos()
    {
        return this.getFlowerPos() != null;
    }

    @Override
    public void setRevengeTarget(@Nullable LivingEntity livingBase)
    {
        super.setRevengeTarget(livingBase);
        if (livingBase != null)
        {
            this.revengeTargetUUID = livingBase.getUniqueID();
        }
    }

    @Override
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
        return !this.isWithinDistance(p_226437_1_, 15);
    }

    @Override
    protected PathNavigator createNavigator(World worldIn)
    {
        FlyingPathNavigator flyingpathnavigator = new FlyingPathNavigator(this, worldIn)
        {
            @Override
            public boolean canEntityStandOnPos(BlockPos pos)
            {
                return !this.world.isAirBlock(pos.down());
            }

            @Override
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
        return this.world.isBlockPresent(p_226439_1_) && (this.world.getBlockState(p_226439_1_).getBlock().isIn(BlockTags.FLOWERS) || this.world.getBlockState(p_226439_1_).getBlock() == ItemBlockRegistryHandler.FAIRY_MUSHROOM_BLOCK.get());
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn)
    {
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return null;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.BLOCK_FIRE_EXTINGUISH;
    }

    @Override
    protected float getSoundVolume()
    {
        return 0.4F;
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn)
    {
        return sizeIn.height * 0.8F;
    }

    @Override
    public boolean onLivingFall(float distance, float damageMultiplier)
    {
        return false;
    }

    @Override
    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos)
    {
    }

    @Override
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

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (!net.minecraftforge.common.ForgeHooks.onLivingAttack(this, source, amount)) return false;
        if (this.isInvulnerableTo(source))
        {
            return false;
        } else if (this.world.isRemote)
        {
            return false;
        } else if (this.getHealth() <= 0.0F)
        {
            return false;
        } else if (source.isFireDamage() && this.isPotionActive(Effects.FIRE_RESISTANCE))
        {
            return false;
        } else
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
    }

    @Override
    public CreatureAttribute getCreatureAttribute()
    {
        return CreatureAttribute.UNDEFINED;
    }

    @Override
    protected void handleFluidJump(ITag<Fluid> fluidTag)
    {
        this.setMotion(this.getMotion().add(0.0D, 0.01D, 0.0D));
    }

    private boolean isWithinDistance(BlockPos pos, int distance)
    {
        return pos.withinDistance(this.getPosition(), distance);
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

    class AngerGoal extends HurtByTargetGoal
    {
        AngerGoal(FairyEntity p_i225726_2_)
        {
            super(p_i225726_2_);
        }

        @Override
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

        @Override
        public void startExecuting()
        {
            FairyEntity.this.ResetStamina();
            super.startExecuting();
        }

        @Override
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

        @Override
        public boolean func_225506_g_()
        {
            return FairyEntity.this.flowerPos != null && FairyEntity.this.IsFlowerBlock(FairyEntity.this.flowerPos) && !FairyEntity.this.isWithinDistance(FairyEntity.this.flowerPos, 2);
        }

        @Override
        public boolean func_225507_h_()
        {
            return this.func_225506_g_();
        }

        @Override
        public void startExecuting()
        {
            this.goToFlowerTimer = 0;
            super.startExecuting();
        }

        @Override
        public void resetTask()
        {
            this.goToFlowerTimer = 0;
            FairyEntity.this.navigator.clearPath();
            FairyEntity.this.navigator.resetRangeMultiplier();

            if (HasFlowerPos())
            {
                FairyEntity.this.prevAttraction.add(new BlockPos(getFlowerPos()));
                FairyEntity.this.flowerPos = null;
            }

            if (FairyEntity.this.prevAttraction.size() > 10)
            {
                FairyEntity.this.prevAttraction.removeFirst();
            }
        }

        @Override
        public void tick()
        {
            if (FairyEntity.this.flowerPos != null)
            {
                FairyEntity.this.DecreaseStamina();
                ++this.goToFlowerTimer;

                if (this.goToFlowerTimer > 300)
                {
                    FairyEntity.this.prevAttraction.add(new BlockPos(getFlowerPos()));
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

        @Override
        public boolean shouldExecute()
        {
            return this.func_225506_g_();
        }

        @Override
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

        @Override
        public void resetTask()
        {
            this.parentEntity.setAngry(false);
        }

        @Override
        public void tick()
        {
            LivingEntity livingentity = FairyEntity.this.getAttackTarget();
            if (FairyEntity.this.getBoundingBox().intersects(livingentity.getBoundingBox()))
            {
                FairyEntity.this.attackEntityAsMob(livingentity);
            } else
            {
                Vector3d vec3d = livingentity.getEyePosition(1.0F);
                FairyEntity.this.moveController.setMoveTo(vec3d.x, vec3d.y, vec3d.z, FairyEntity.this.getAttribute(Attributes.FLYING_SPEED).getBaseValue() * 2);
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

        @Override
        public boolean shouldExecute()
        {
            return FairyEntity.this.navigator.noPath() && FairyEntity.this.rand.nextInt(10) == 0 && !FairyEntity.this.isResting();
        }

        @Override
        public boolean shouldContinueExecuting()
        {
            return FairyEntity.this.navigator.func_226337_n_() && !FairyEntity.this.isResting();
        }

        @Override
        public void startExecuting()
        {
            Vector3d vec3d = this.GetNextPosViaForward();
            if (vec3d != null)
            {
                FairyEntity.this.navigator.setPath(FairyEntity.this.navigator.getPathToPos(new BlockPos(vec3d), 1), FairyEntity.this.getAttribute(Attributes.FLYING_SPEED).getBaseValue());
            }

        }

        @Override
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

                        if (FairyEntity.this.getFlowerPos() != null)
                        {
                            break;
                        }
                    }

                    if (FairyEntity.this.getFlowerPos() != null)
                    {
                        break;
                    }
                }
            }
        }

        private Vector3d GetNextPosViaForward()
        {
            Vector3d vec3d = FairyEntity.this.getLook(0.0F);

            Vector3d vec3d2 = RandomPositionGenerator.findAirTarget(FairyEntity.this, 8, 7, FairyEntity.this.getLook(0.0F), ((float) Math.PI / 2F), 2, 1);
            return vec3d2 != null ? vec3d2 : RandomPositionGenerator.findGroundTarget(FairyEntity.this, 8, 4, -2, vec3d, (float) Math.PI / 2F);
        }
    }

    class RestingGoal extends Goal
    {

        @Override
        public boolean shouldExecute()
        {
            return FairyEntity.this.isResting();
        }

        @Override
        public boolean shouldContinueExecuting()
        {
            return FairyEntity.this.isResting();
        }

        @Override
        public void startExecuting()
        {
            FairyEntity.this.navigator.clearPath();
            super.startExecuting();
        }

        @Override
        public void tick()
        {
            FairyEntity.this.IncreaseStamina();
        }
    }
}
//TODO Ride fairy jar entity. Have a hunger state if is riding jar. if hungry, becoe ANGERY. try to break jar. get riding on and damage. right click jar to get item. right click jar with sugar to calm the fairy hunger. Fairy pet maybe