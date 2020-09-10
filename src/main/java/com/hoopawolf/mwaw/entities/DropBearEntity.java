package com.hoopawolf.mwaw.entities;

import com.hoopawolf.mwaw.client.animation.AnimationHelper;
import com.hoopawolf.mwaw.client.animation.PercentageRotation;
import com.hoopawolf.mwaw.entities.ai.controller.MWAWMovementController;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.ClimberPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Rotations;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

public class DropBearEntity extends CreatureEntity implements IMob
{
    private static final DataParameter<Boolean> TIRED = EntityDataManager.createKey(DropBearEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> GRABBEDTARGET = EntityDataManager.createKey(DropBearEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> HUGGING = EntityDataManager.createKey(DropBearEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Float> ANIMATION_SPEED = EntityDataManager.createKey(DropBearEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> HUG_DIR = EntityDataManager.createKey(DropBearEntity.class, DataSerializers.VARINT); //PZ, PX, NZ, NX

    private static final DataParameter<Float> GRABBEDTARGETX = EntityDataManager.createKey(DropBearEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> GRABBEDTARGETY = EntityDataManager.createKey(DropBearEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> GRABBEDTARGETZ = EntityDataManager.createKey(DropBearEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> ATTACK_TIMER = EntityDataManager.createKey(DendroidElderEntity.class, DataSerializers.FLOAT);

    private static final DataParameter<Rotations> HEAD_ROTATION = EntityDataManager.createKey(DropBearEntity.class, DataSerializers.ROTATIONS);
    private static final DataParameter<Rotations> BODY_ROTATION = EntityDataManager.createKey(DropBearEntity.class, DataSerializers.ROTATIONS);
    private static final DataParameter<Rotations> RIGHT_ARM_ROTATION = EntityDataManager.createKey(DropBearEntity.class, DataSerializers.ROTATIONS);
    private static final DataParameter<Rotations> LEFT_ARM_ROTATION = EntityDataManager.createKey(DropBearEntity.class, DataSerializers.ROTATIONS);
    private static final DataParameter<Rotations> RIGHT_LEG_ROTATION = EntityDataManager.createKey(DropBearEntity.class, DataSerializers.ROTATIONS);
    private static final DataParameter<Rotations> LEFT_LEG_ROTATION = EntityDataManager.createKey(DropBearEntity.class, DataSerializers.ROTATIONS);
    public final AnimationHelper animation = new AnimationHelper();

    private final Class[] grabTargets = {
            PlayerEntity.class
    };
    private final float attackTimerMax = 10;
    private BlockPos huggingBlockPos;
    private Vector3d stayingPos;
    private LivingEntity grabbedEntity;

    public DropBearEntity(EntityType<? extends DropBearEntity> type, World worldIn)
    {
        super(type, worldIn);
        this.setTired(false);

        animation.registerData(HEAD_ROTATION);
        animation.registerData(BODY_ROTATION);
        animation.registerData(RIGHT_ARM_ROTATION);
        animation.registerData(LEFT_ARM_ROTATION);
        animation.registerData(LEFT_LEG_ROTATION);
        animation.registerData(RIGHT_LEG_ROTATION);

        this.moveController = new MWAWMovementController(this, 30);

        this.setPathPriority(PathNodeType.LEAVES, -1.0F);

        huggingBlockPos = null;
    }

    public static AttributeModifierMap.MutableAttribute func_234321_m_()
    {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 8.0D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 3.0D)
                .createMutableAttribute(Attributes.FOLLOW_RANGE, 12.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.2D);
    }

    @Override
    protected void registerData()
    {
        super.registerData();

        this.dataManager.register(TIRED, true);
        this.dataManager.register(GRABBEDTARGET, false);
        this.dataManager.register(HUGGING, false);
        this.dataManager.register(ANIMATION_SPEED, 0F);
        this.dataManager.register(HUG_DIR, 0);

        this.dataManager.register(ATTACK_TIMER, 0F);
        this.dataManager.register(GRABBEDTARGETX, 0F);
        this.dataManager.register(GRABBEDTARGETY, 0F);
        this.dataManager.register(GRABBEDTARGETZ, 0F);

        this.dataManager.register(HEAD_ROTATION, new Rotations(0, 0, 0));
        this.dataManager.register(BODY_ROTATION, new Rotations(0, 0, 0));
        this.dataManager.register(RIGHT_ARM_ROTATION, new Rotations(0, 0, 0));
        this.dataManager.register(LEFT_ARM_ROTATION, new Rotations(0, 0, 0));
        this.dataManager.register(LEFT_LEG_ROTATION, new Rotations(0, 0, 0));
        this.dataManager.register(RIGHT_LEG_ROTATION, new Rotations(0, 0, 0));
    }

    @Override
    protected PathNavigator createNavigator(World worldIn)
    {
        return new ClimberPathNavigator(this, worldIn);
    }

    @Override
    protected void registerGoals()
    {
        this.targetSelector.addGoal(2, (new HurtByTargetGoal(this)));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));

        this.goalSelector.addGoal(0, new DropBearSwim(this));
        this.goalSelector.addGoal(1, new JumpToHugGoal(this, 20));
        this.goalSelector.addGoal(2, new DropBearMelee(this, 1.0D, true));
        this.goalSelector.addGoal(3, new FindLogToHugGoal(this, 10));
        this.goalSelector.addGoal(12, new DropBearLookAt(this, PlayerEntity.class, 20.0F));
    }

    @Override
    protected boolean isDespawnPeaceful()
    {
        return true;
    }

    public boolean isTired()
    {
        return this.dataManager.get(TIRED);
    }

    public void setTired(boolean tired)
    {
        this.dataManager.set(TIRED, tired);
    }

    public boolean isHugging()
    {
        return this.dataManager.get(HUGGING);
    }

    public void setHugging(boolean hugging)
    {
        this.dataManager.set(HUGGING, hugging);
    }

    public boolean grabbedTarget()
    {
        return this.dataManager.get(GRABBEDTARGET);
    }

    public void setGrabbedTarget(boolean grabbed)
    {
        this.dataManager.set(GRABBEDTARGET, grabbed);
    }

    public float getAnimationSpeed()
    {
        return this.dataManager.get(ANIMATION_SPEED);
    }

    public void setAnimationSpeed(float speedIn)
    {
        this.dataManager.set(ANIMATION_SPEED, speedIn);
    }

    public int getHuggingDir()
    {
        return this.dataManager.get(HUG_DIR);
    }

    public void setHuggingDir(int huggingDirIn)
    {
        this.dataManager.set(HUG_DIR, huggingDirIn);
    }

    public float getAttackTimer()
    {
        return this.dataManager.get(ATTACK_TIMER);
    }

    public void setAttackTimer(float timer)
    {
        this.dataManager.set(ATTACK_TIMER, timer);
    }

    public Vector3d getTargetPos()
    {
        return new Vector3d(this.dataManager.get(GRABBEDTARGETX), this.dataManager.get(GRABBEDTARGETY), this.dataManager.get(GRABBEDTARGETZ));
    }

    public void setTargetPos(double xIn, double yIn, double zIn)
    {
        this.dataManager.set(GRABBEDTARGETX, (float) xIn);
        this.dataManager.set(GRABBEDTARGETY, (float) yIn);
        this.dataManager.set(GRABBEDTARGETZ, (float) zIn);
    }

    public Rotations getHeadRotation()
    {
        return this.dataManager.get(HEAD_ROTATION);
    }

    public Rotations getBodyRotation()
    {
        return this.dataManager.get(BODY_ROTATION);
    }

    public Rotations getRightArmRotation()
    {
        return this.dataManager.get(RIGHT_ARM_ROTATION);
    }

    public Rotations getLeftArmRotation()
    {
        return this.dataManager.get(LEFT_ARM_ROTATION);
    }

    public Rotations getRightLegRotation()
    {
        return this.dataManager.get(RIGHT_LEG_ROTATION);
    }

    public Rotations getLeftLegRotation()
    {
        return this.dataManager.get(LEFT_LEG_ROTATION);
    }

    @Override
    public void tick()
    {
        super.tick();

        if (isHugging())
        {
            renderYawOffset = 0;
        }

        if (grabbedTarget())
        {
            this.setPosition(getTargetPos().getX(), getTargetPos().getY(), getTargetPos().getZ());
        }

        if (!world.isRemote)
        {
            if (isHugging())
            {
                setNoGravity(true);
                noClip = true;
                setMotion(Vector3d.ZERO);
                this.setPosition(stayingPos.getX(), stayingPos.getY(), stayingPos.getZ());

                if (world.isAirBlock(huggingBlockPos))
                {
                    huggingBlockPos = null;
                    setHugging(false);
                }
            } else
            {
                setNoGravity(false);

                if (grabbedEntity != null)
                {
                    if (grabbedEntity.isAlive())
                    {
                        noClip = true;
                        setGrabbedTarget(true);
                        setTargetPos(grabbedEntity.getPosX() + grabbedEntity.getLookVec().getX() * 0.2F, grabbedEntity.getPosYEye() - 0.5D, grabbedEntity.getPosZ() + grabbedEntity.getLookVec().getZ() * 0.2F);
                    } else
                    {
                        this.setMotion(getMotion().getX(), 0.0D, getMotion().getZ());
                        this.setPosition(grabbedEntity.getPosX() + grabbedEntity.getLookVec().getX() * 0.5F, grabbedEntity.getPosYEye() - 0.5D, grabbedEntity.getPosZ() + grabbedEntity.getLookVec().getZ() * 0.5F);
                        grabbedEntity = null;
                        noClip = false;
                        setGrabbedTarget(false);
                    }
                } else
                {
                    noClip = false;
                }

                if (getAttackTimer() > 0)
                {
                    setAttackTimer(getAttackTimer() - 1.0F);
                }
            }

            if (isHugging())
            {
                this.setTired(world.isDaytime());
            } else
            {
                this.setTired(false);
            }
        } else
        {
            setAnimationSpeed(0.08F);

            if (!isHugging())
            {
                if (getAttackTimer() > attackTimerMax * 0.3F)
                {
                    animation.registerRotationPoints(LEFT_ARM_ROTATION, new PercentageRotation(getLeftArmRotation(), new Rotations(-95, -35, 0)));
                    animation.registerRotationPoints(RIGHT_ARM_ROTATION, new PercentageRotation(getRightArmRotation(), new Rotations(-95, 35, 0)));
                } else if (getAttackTimer() > 0)
                {
                    animation.registerRotationPoints(LEFT_ARM_ROTATION, new PercentageRotation(getLeftArmRotation(), new Rotations(-55, 25, 0)));
                    animation.registerRotationPoints(RIGHT_ARM_ROTATION, new PercentageRotation(getRightArmRotation(), new Rotations(-55, -25, 0)));
                } else
                {
                    animation.registerRotationPoints(HEAD_ROTATION, new PercentageRotation(getHeadRotation(), new Rotations(0, 0, 0)));
                    animation.registerRotationPoints(BODY_ROTATION, new PercentageRotation(getBodyRotation(), new Rotations(0, 0, 0)));
                    animation.registerRotationPoints(RIGHT_ARM_ROTATION, new PercentageRotation(getRightArmRotation(), new Rotations(0, 0, 0)));
                    animation.registerRotationPoints(LEFT_ARM_ROTATION, new PercentageRotation(getLeftArmRotation(), new Rotations(0, 0, 0)));

                    animation.registerRotationPoints(RIGHT_LEG_ROTATION, new PercentageRotation(getRightLegRotation(), new Rotations(0, 0, 0)));
                    animation.registerRotationPoints(LEFT_LEG_ROTATION, new PercentageRotation(getLeftLegRotation(), new Rotations(0, 0, 0)));
                }
            } else
            {
                if (!isTired())
                {
                    animation.registerRotationPoints(HEAD_ROTATION, new PercentageRotation(getHeadRotation(), new Rotations(-30, 0, 0)));
                    animation.registerRotationPoints(BODY_ROTATION, new PercentageRotation(getBodyRotation(), new Rotations(90, 90 * this.getHuggingDir(), 0)));
                } else
                {
                    animation.registerRotationPoints(HEAD_ROTATION, new PercentageRotation(getHeadRotation(), new Rotations(75, 0, 0)));
                    animation.registerRotationPoints(BODY_ROTATION, new PercentageRotation(getBodyRotation(), new Rotations(-90, (90 * this.getHuggingDir()) - 180, 0)));
                }

                animation.registerRotationPoints(RIGHT_ARM_ROTATION, new PercentageRotation(getRightArmRotation(), new Rotations(-30, 0, 0)));
                animation.registerRotationPoints(LEFT_ARM_ROTATION, new PercentageRotation(getLeftArmRotation(), new Rotations(-30, 0, 0)));
                animation.registerRotationPoints(RIGHT_LEG_ROTATION, new PercentageRotation(getRightLegRotation(), new Rotations(20, 0, 0)));
                animation.registerRotationPoints(LEFT_LEG_ROTATION, new PercentageRotation(getLeftLegRotation(), new Rotations(20, 0, 0)));

            }
        }
    }

    @Override
    public boolean isOnLadder()
    {
        return false;
    }

    @Override
    public boolean canBePushed()
    {
        return !noClip;
    }

    @Override
    protected void collideWithEntity(Entity entityIn)
    {
        if (!world.isRemote)
        {
            if (!noClip)
            {
                super.collideWithEntity(entityIn);
            }
        }
    }

    @Override
    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos)
    {
    }

    @Override
    protected float getSoundPitch()
    {
        return this.rand.nextFloat() - this.rand.nextFloat() * 0.2F + 1.9F;
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return (isTired() ? SoundEvents.ENTITY_FOX_SLEEP : null);
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_PANDA_HURT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_HUSK_DEATH;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (!world.isRemote)
        {
            setHugging(false);
            huggingBlockPos = null;
        }

        return super.attackEntityFrom(source, amount);
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn)
    {
        this.setAttackTimer(attackTimerMax);
        this.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 0.5F, 0.8F);

        return super.attackEntityAsMob(entityIn);
    }

    private static class DropBearSwim extends SwimGoal
    {
        DropBearEntity host;

        public DropBearSwim(MobEntity entityIn)
        {
            super(entityIn);
            host = (DropBearEntity) entityIn;
        }

        @Override
        public void startExecuting()
        {
            super.startExecuting();
            host.setTired(false);
        }
    }

    private static class DropBearLookAt extends LookAtGoal
    {
        DropBearEntity host;

        public DropBearLookAt(MobEntity entityIn, Class<? extends LivingEntity> watchTargetClass, float maxDistance)
        {
            super(entityIn, watchTargetClass, maxDistance);
            host = (DropBearEntity) entityIn;
        }

        @Override
        public boolean shouldExecute()
        {
            return super.shouldExecute() && !host.isHugging();
        }
    }

    private static class DropBearMelee extends MeleeAttackGoal
    {
        DropBearEntity host;

        public DropBearMelee(CreatureEntity creature, double speedIn, boolean useLongMemory)
        {
            super(creature, speedIn, useLongMemory);
            host = (DropBearEntity) creature;
        }

        @Override
        public boolean shouldExecute()
        {
            return super.shouldExecute() && !host.isHugging();
        }
    }

    private static class JumpToHugGoal extends Goal
    {
        private final DropBearEntity dropBearEntity;
        private final float checkDist;
        private LivingEntity tryHugTarget;

        public JumpToHugGoal(DropBearEntity _entity, float checkDistIn)
        {
            dropBearEntity = _entity;
            checkDist = checkDistIn;


            this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        }

        @Override
        public boolean shouldExecute()
        {
            if (!this.dropBearEntity.isTired() && this.dropBearEntity.grabbedEntity == null && this.dropBearEntity.isHugging())
            {
                for (Class entClass : this.dropBearEntity.grabTargets)
                {
                    List<LivingEntity> list = this.dropBearEntity.world.getEntitiesWithinAABB(entClass, (new AxisAlignedBB(this.dropBearEntity.getPosX(), this.dropBearEntity.getPosY(), this.dropBearEntity.getPosZ(), this.dropBearEntity.getPosX() + 1.0D, this.dropBearEntity.getPosY() + 1.0D, this.dropBearEntity.getPosZ() + 1.0D)).grow(checkDist * 0.2F, checkDist, checkDist * 0.2F));
                    Iterator iterator = list.iterator();

                    while (iterator.hasNext())
                    {
                        LivingEntity _ent = (LivingEntity) iterator.next();

                        if (!dropBearEntity.canEntityBeSeen(_ent) || (_ent instanceof PlayerEntity && ((PlayerEntity) _ent).isCreative()))
                            continue;

                        if (tryHugTarget != null)
                        {
                            if (this.dropBearEntity.getDistanceSq(tryHugTarget) > this.dropBearEntity.getDistanceSq(_ent))
                                tryHugTarget = _ent;
                        } else
                            tryHugTarget = _ent;
                    }
                }

                return tryHugTarget != null;
            }


            return false;
        }

        @Override
        public void resetTask()
        {
            tryHugTarget = null;
        }

        @Override
        public boolean shouldContinueExecuting()
        {
            return this.dropBearEntity.grabbedEntity == null && tryHugTarget.getDistanceSq(this.dropBearEntity) < 220 && tryHugTarget.isAlive() && !dropBearEntity.isOnGround();
        }

        @Override
        public void tick()
        {
            if (tryHugTarget.getDistanceSq(this.dropBearEntity) < 220 && !dropBearEntity.isOnGround() && dropBearEntity.isHugging())
            {
                if (dropBearEntity.isHugging())
                {
                    dropBearEntity.setHugging(false);
                }

                Vector3d dir = tryHugTarget.getPositionVec().subtract(this.dropBearEntity.getPositionVec()).normalize();
                Vector3d motion = new Vector3d(dir.x * 1.5F, MathHelper.signum(dir.y), dir.z * 1.5F);

                double d2 = tryHugTarget.getPosX() - dropBearEntity.getPosX();
                double d1 = tryHugTarget.getPosZ() - dropBearEntity.getPosZ();
                dropBearEntity.rotationYaw = -((float) MathHelper.atan2(d2, d1)) * (180F / (float) Math.PI);
                dropBearEntity.renderYawOffset = dropBearEntity.rotationYaw;

                this.dropBearEntity.setMotion(motion.getX(), motion.getY(), motion.getZ());
            }

            if (this.dropBearEntity.getBoundingBox().intersects(tryHugTarget.getBoundingBox().grow(1.0D)) && !(tryHugTarget instanceof PlayerEntity && !((PlayerEntity) tryHugTarget).inventory.armorInventory.get(3).isEmpty()))
            {
                this.dropBearEntity.grabbedEntity = tryHugTarget;
                this.dropBearEntity.setAttackTarget(tryHugTarget);
                this.dropBearEntity.playSound(SoundEvents.ENTITY_HUSK_DEATH, 1.0F, 0.1F);
            }
        }
    }

    private static class FindLogToHugGoal extends Goal
    {
        private final DropBearEntity dropBearEntity;
        private final int checkDist;
        private BlockPos tryHugTarget;
        private float timer = 0;

        public FindLogToHugGoal(DropBearEntity _entity, int checkDistIn)
        {
            dropBearEntity = _entity;
            checkDist = checkDistIn;

            this.setMutexFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean shouldExecute()
        {
            if (!this.dropBearEntity.isHugging() && this.dropBearEntity.isOnGround())
            {
                BlockPos highestBlock = null;
                for (int x = -checkDist; x < checkDist; ++x)
                {
                    for (int z = -checkDist; z < checkDist; ++z)
                    {
                        for (int y = 0; y < 16; ++y)
                        {
                            if (dropBearEntity.world.getBlockState(dropBearEntity.getPosition().add(x, y, z)).getMaterial().equals(Material.WOOD))
                            {
                                BlockPos currBlockPos = dropBearEntity.getPosition().add(x, y, z);

                                if (highestBlock != null)
                                {
                                    if (currBlockPos.getY() > highestBlock.getY())
                                    {
                                        highestBlock = dropBearEntity.getPosition().add(x, y, z);
                                    }
                                } else
                                {
                                    highestBlock = dropBearEntity.getPosition().add(x, y, z);
                                }
                            } else
                            {
                                if (y >= 3)
                                {
                                    break;
                                }
                            }
                        }
                    }
                }

                if (highestBlock != null)
                {
                    tryHugTarget = new BlockPos(highestBlock.getX() + 0.5F, highestBlock.getY(), highestBlock.getZ() + 0.5F);
                }

                return tryHugTarget != null;
            }


            return false;
        }

        @Override
        public void resetTask()
        {
            tryHugTarget = null;
            timer = 0.0F;
            this.dropBearEntity.navigator.clearPath();
        }

        @Override
        public boolean shouldContinueExecuting()
        {
            return timer < 50 && tryHugTarget != null && dropBearEntity.world.getBlockState(tryHugTarget).getMaterial().equals(Material.WOOD) && !dropBearEntity.isHugging();
        }

        @Override
        public void tick()
        {
            if (this.dropBearEntity.ticksExisted % 8 == 0)
            {
                timer += 1.0F;
            }

            if (this.dropBearEntity.getDistanceSq(tryHugTarget.getX(), tryHugTarget.getY(), tryHugTarget.getZ()) < 2.0D || timer > 30)
            {
                if (this.dropBearEntity.world.getBlockState(new BlockPos(this.dropBearEntity.getPosition().getX() + 1, this.dropBearEntity.getPosition().getY(), this.dropBearEntity.getPosition().getZ())).getMaterial().equals(Material.WOOD) ||
                        this.dropBearEntity.world.getBlockState(new BlockPos(this.dropBearEntity.getPosition().getX() - 1, this.dropBearEntity.getPosition().getY(), this.dropBearEntity.getPosition().getZ())).getMaterial().equals(Material.WOOD) ||
                        this.dropBearEntity.world.getBlockState(new BlockPos(this.dropBearEntity.getPosition().getX(), this.dropBearEntity.getPosition().getY(), this.dropBearEntity.getPosition().getZ() + 1)).getMaterial().equals(Material.WOOD) ||
                        this.dropBearEntity.world.getBlockState(new BlockPos(this.dropBearEntity.getPosition().getX(), this.dropBearEntity.getPosition().getY(), this.dropBearEntity.getPosition().getZ() - 1)).getMaterial().equals(Material.WOOD))
                {
                    if (this.dropBearEntity.world.getBlockState(new BlockPos(this.dropBearEntity.getPosition().getX() + 1, this.dropBearEntity.getPosition().getY(), this.dropBearEntity.getPosition().getZ())).getMaterial().equals(Material.WOOD))
                    {
                        this.dropBearEntity.huggingBlockPos = new BlockPos(this.dropBearEntity.getPosition().getX() + 1, this.dropBearEntity.getPosition().getY(), this.dropBearEntity.getPosition().getZ());
                        this.dropBearEntity.setHuggingDir(1);
                    } else if (this.dropBearEntity.world.getBlockState(new BlockPos(this.dropBearEntity.getPosition().getX() - 1, this.dropBearEntity.getPosition().getY(), this.dropBearEntity.getPosition().getZ())).getMaterial().equals(Material.WOOD))
                    {
                        this.dropBearEntity.huggingBlockPos = new BlockPos(this.dropBearEntity.getPosition().getX() - 1, this.dropBearEntity.getPosition().getY(), this.dropBearEntity.getPosition().getZ());
                        this.dropBearEntity.setHuggingDir(3);
                    } else if (this.dropBearEntity.world.getBlockState(new BlockPos(this.dropBearEntity.getPosition().getX(), this.dropBearEntity.getPosition().getY(), this.dropBearEntity.getPosition().getZ() + 1)).getMaterial().equals(Material.WOOD))
                    {
                        this.dropBearEntity.huggingBlockPos = new BlockPos(this.dropBearEntity.getPosition().getX(), this.dropBearEntity.getPosition().getY(), this.dropBearEntity.getPosition().getZ() + 1);
                        this.dropBearEntity.setHuggingDir(2);
                    } else if (this.dropBearEntity.world.getBlockState(new BlockPos(this.dropBearEntity.getPosition().getX(), this.dropBearEntity.getPosition().getY(), this.dropBearEntity.getPosition().getZ() - 1)).getMaterial().equals(Material.WOOD))
                    {
                        this.dropBearEntity.huggingBlockPos = new BlockPos(this.dropBearEntity.getPosition().getX(), this.dropBearEntity.getPosition().getY(), this.dropBearEntity.getPosition().getZ() - 1);
                        this.dropBearEntity.setHuggingDir(0);
                    }
                } else
                { //PZ, PX, NZ, NX
                    Vector3d dir = new Vector3d(tryHugTarget.getX() - (int) this.dropBearEntity.getPosX(), 0, tryHugTarget.getZ() - (int) this.dropBearEntity.getPosZ());
                    if (dir.getX() == 1)
                    {
                        this.dropBearEntity.setPosition(tryHugTarget.getX() + 1, this.dropBearEntity.getPosY(), tryHugTarget.getZ());
                    } else if (dir.getX() == -1)
                    {
                        this.dropBearEntity.setPosition(tryHugTarget.getX() - 1, this.dropBearEntity.getPosY(), tryHugTarget.getZ());
                    } else if (dir.getZ() == -1)
                    {
                        this.dropBearEntity.setPosition(tryHugTarget.getX(), this.dropBearEntity.getPosY(), tryHugTarget.getZ() + 1);
                    } else
                    {
                        this.dropBearEntity.setPosition(tryHugTarget.getX(), this.dropBearEntity.getPosY(), tryHugTarget.getZ() - 1);
                    }
                }

                if (this.dropBearEntity.huggingBlockPos != null)
                {
                    this.dropBearEntity.setHugging(true);
                    this.dropBearEntity.setPosition((float) this.dropBearEntity.getPosition().getX() + 0.5F, this.dropBearEntity.getPosition().getY(), (float) this.dropBearEntity.getPosition().getZ() + 0.5F);
                    this.dropBearEntity.stayingPos = new Vector3d((float) this.dropBearEntity.getPosition().getX() + 0.5F, this.dropBearEntity.getPosition().getY(), (float) this.dropBearEntity.getPosition().getZ() + 0.5F);
                }
            } else
            {
                this.dropBearEntity.moveController.setMoveTo(tryHugTarget.getX(), tryHugTarget.getY(), tryHugTarget.getZ(), 1.0D);

                if (dropBearEntity.collidedHorizontally)
                {
                    Vector3d dir = new Vector3d(tryHugTarget.getX(), tryHugTarget.getY(), tryHugTarget.getZ()).subtract(this.dropBearEntity.getPositionVec()).normalize();
                    Vector3d motion = new Vector3d(0, MathHelper.signum(dir.y) * 0.1F, 0);

                    double d2 = tryHugTarget.getX() - dropBearEntity.getPosX();
                    double d1 = tryHugTarget.getZ() - dropBearEntity.getPosZ();
                    dropBearEntity.rotationYaw = -((float) MathHelper.atan2(d2, d1)) * (180F / (float) Math.PI);
                    dropBearEntity.renderYawOffset = dropBearEntity.rotationYaw;

                    this.dropBearEntity.setMotion(motion.getX(), motion.getY(), motion.getZ());
                }
            }
        }
    }
}
