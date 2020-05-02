package com.hoopawolf.mwaw.entities;

import com.hoopawolf.mwaw.network.MWAWPacketHandler;
import com.hoopawolf.mwaw.network.packets.client.SpawnParticleMessage;
import com.hoopawolf.mwaw.util.RegistryHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.controller.JumpController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

public class WolpertingerEntity extends AnimalEntity
{
    private int jumpTicks;
    private int jumpDuration;
    private boolean wasOnGround;
    private int currentMoveTypeDuration;
    private float moving_timer;
    private boolean isScared;
    private float scared_timer;
    private float drop_timer;
    private float pickup_timer;

    private Entity grabbedEntity;

    private Class[] grabTargets = {
            FairyEntity.class
    };

    private FlyingAvoidEntityGoal flyingavoidgoal;
    private static final DataParameter<Integer> TYPE = EntityDataManager.createKey(WolpertingerEntity.class, DataSerializers.VARINT);

    MovementController flyingController = new FlyingMovementController(this, 20, true);
    MovementController landController = new WolpertingerEntity.MoveHelperController(this);

    public WolpertingerEntity(EntityType<? extends WolpertingerEntity> type, World worldIn)
    {
        super(type, worldIn);
        this.jumpController = new WolpertingerEntity.JumpHelperController(this);
        this.moveController = landController;
        this.setMovementSpeed(0.0D);
        moving_timer = 0.0F;
        isScared = false;
        scared_timer = 0.0F;
        grabbedEntity = null;
        this.removePassengers();
    }

    public float getBlockPathWeight(BlockPos pos, IWorldReader worldIn)
    {
        return worldIn.isAirBlock(pos) ? 10.0F : 0.0F;
    }

    protected void registerData()
    {
        super.registerData();
        this.dataManager.register(TYPE, 0);
    }

    protected void registerGoals()
    {
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(2, new BreedGoal(this, 0.35D));
        this.goalSelector.addGoal(4, new TemptGoal(this, 0.35D, Ingredient.fromItems(Items.GOLDEN_CARROT), false));
        this.goalSelector.addGoal(5, new RandomWalkingWithRidden(this, 0.35D));
        this.goalSelector.addGoal(6, new JumpToGrabGoal(this, 10));
        this.goalSelector.addGoal(8, new LookAtWithPassenger(this, PlayerEntity.class, 4.0F));
        this.goalSelector.addGoal(8, new LookAtWithPassenger(this, CreatureEntity.class, 4.0F));
    }

    protected void registerAttributes()
    {
        super.registerAttributes();
        this.getAttributes().registerAttribute(SharedMonsterAttributes.FLYING_SPEED);

        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(5.0D);
        this.getAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(1.2D);
    }

    private void MoveToPos(BlockPos p_226433_1_)
    {
        if (p_226433_1_ != null)
        {
            this.navigator.tryMoveToXYZ(p_226433_1_.getX(), p_226433_1_.getY(), p_226433_1_.getZ(), WolpertingerEntity.this.getAttribute(SharedMonsterAttributes.FLYING_SPEED).getBaseValue());

            if (ticksExisted % 5 == 0 && !this.world.isRemote)
            {
                Vec3d _vec = new Vec3d(this.getPosX() - (double) 0.3F, this.getPosYHeight(0.5D), this.getPosZ() + (double) 0.3F);
                SpawnParticleMessage spawnParticleMessage = new SpawnParticleMessage(_vec, new Vec3d(0, 0, 0), 1, 3, 0.5F);
                MWAWPacketHandler.INSTANCE.send(PacketDistributor.DIMENSION.with(() -> this.dimension), spawnParticleMessage);
            }
        }
    }

    protected float getJumpUpwardsMotion()
    {
        if (!this.collidedHorizontally && (!this.moveController.isUpdating() || !(this.moveController.getY() > this.getPosY() + 0.5D)))
        {
            Path path = this.navigator.getPath();
            if (path != null && path.getCurrentPathIndex() < path.getCurrentPathLength())
            {
                Vec3d vec3d = path.getPosition(this);
                if (vec3d.y > this.getPosY() + 0.5D)
                {
                    return 0.5F;
                }
            }

            return this.moveController.getSpeed() <= 0.6D ? 0.2F : 0.3F;
        } else
        {
            return 0.5F;
        }
    }

    /**
     * Causes this entity to do an upwards motion (jumping).
     */
    protected void jump()
    {
        if (!isScared)
        {
            super.jump();
            ++moving_timer;

            if (moving_timer >= 5)
            {
                this.navigator.clearPath();
                this.setMotion(rand.nextDouble() - rand.nextDouble(), 0.0D, rand.nextDouble() - rand.nextDouble());
                moving_timer = 0.0F;
            }

            double d0 = this.moveController.getSpeed();
            if (d0 > 0.0D)
            {
                double d1 = horizontalMag(this.getMotion());
                if (d1 < 0.01D)
                {
                    this.moveRelative(0.1F, new Vec3d(0.0D, 0.0D, 1.0D));
                }
            }

            if (!this.world.isRemote)
            {
                this.world.setEntityState(this, (byte) 1);
            }
        }
    }

    protected PathNavigator createNavigator(World worldIn)
    {
        if (isScared)
        {
            FlyingPathNavigator flyingpathnavigator = new FlyingPathNavigator(this, worldIn)
            {
                public boolean canEntityStandOnPos(BlockPos pos)
                {
                    return !this.world.isAirBlock(pos.down());
                }

                public void tick()
                {
                    super.tick();
                }
            };
            flyingpathnavigator.setCanOpenDoors(false);
            flyingpathnavigator.setCanSwim(false);
            flyingpathnavigator.setCanEnterDoors(true);
            return flyingpathnavigator;
        } else
        {
            return super.createNavigator(worldIn);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public float getJumpCompletion(float p_175521_1_)
    {
        return this.jumpDuration == 0 ? 0.0F : ((float) this.jumpTicks + p_175521_1_) / (float) this.jumpDuration;
    }

    public void setMovementSpeed(double newSpeed)
    {
        this.getNavigator().setSpeed(newSpeed);
        this.moveController.setMoveTo(this.moveController.getX(), this.moveController.getY(), this.moveController.getZ(), newSpeed);
    }

    public void setJumping(boolean jumping)
    {
        super.setJumping(jumping);
        if (jumping)
        {
            this.playSound(this.getJumpSound(), this.getSoundVolume(), ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) * 0.8F);
        }

    }

    public void startJumping()
    {
        this.setJumping(true);
        this.jumpDuration = 10;
        this.jumpTicks = 0;
    }

    protected SoundEvent getJumpSound()
    {
        return SoundEvents.ENTITY_RABBIT_JUMP;
    }

    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_RABBIT_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_RABBIT_HURT;
    }

    public void writeAdditional(CompoundNBT compound)
    {
        super.writeAdditional(compound);
        compound.putInt("WolpertingerType", this.getWolpertingerType());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readAdditional(CompoundNBT compound)
    {
        super.readAdditional(compound);
        this.setWolpertingerType(compound.getInt("WolpertingerType"));
    }

    public int getWolpertingerType()
    {
        return this.dataManager.get(TYPE);
    }

    public void setWolpertingerType(int wolperTypeId)
    {
        this.dataManager.set(TYPE, wolperTypeId);
    }

    public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag)
    {
        int i = 0;

        if (worldIn.getBiome(this.getPosition()) == Biomes.GIANT_TREE_TAIGA)
            i = 1;
        else if (worldIn.getBiome(this.getPosition()) == Biomes.SUNFLOWER_PLAINS)
            i = 2;
        else if (worldIn.getBiome(this.getPosition()) == Biomes.WOODED_MOUNTAINS)
            i = 3;

        this.setWolpertingerType(i);
        if (world.rand.nextInt(100) < 30)
            this.setGrowingAge(-24000);

        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    protected void updateAITasks()
    {
        if (this.currentMoveTypeDuration > 0)
        {
            --this.currentMoveTypeDuration;
        }

        if (this.onGround && !isScared)
        {
            if (!this.wasOnGround)
            {
                this.setJumping(false);
                this.checkLandingDelay();
            }

            WolpertingerEntity.JumpHelperController wolpertingerentity$jumphelpercontroller = (WolpertingerEntity.JumpHelperController) this.jumpController;
            if (!wolpertingerentity$jumphelpercontroller.getIsJumping())
            {
                if (this.moveController.isUpdating() && this.currentMoveTypeDuration == 0)
                {
                    Path path = this.navigator.getPath();
                    Vec3d vec3d = new Vec3d(this.moveController.getX(), this.moveController.getY(), this.moveController.getZ());
                    if (path != null && path.getCurrentPathIndex() < path.getCurrentPathLength())
                    {
                        vec3d = path.getPosition(this);
                    }

                    this.calculateRotationYaw(vec3d.x, vec3d.z);
                    this.startJumping();
                }
            } else if (!wolpertingerentity$jumphelpercontroller.canJump())
            {
                this.enableJumpControl();
            }
        }

        this.wasOnGround = this.onGround;
    }

    private void calculateRotationYaw(double x, double z)
    {
        this.rotationYaw = (float) (MathHelper.atan2(z - this.getPosZ(), x - this.getPosX()) * (double) (180F / (float) Math.PI)) - 90.0F;
    }

    private void enableJumpControl()
    {
        ((WolpertingerEntity.JumpHelperController) this.jumpController).setCanJump(true);
    }

    private void disableJumpControl()
    {
        ((WolpertingerEntity.JumpHelperController) this.jumpController).setCanJump(false);
    }

    private void updateMoveTypeDuration()
    {
        if (this.moveController.getSpeed() < 2.2D)
        {
            this.currentMoveTypeDuration = 10;
        } else
        {
            this.currentMoveTypeDuration = 1;
        }

    }

    private void checkLandingDelay()
    {
        this.updateMoveTypeDuration();
        this.disableJumpControl();
    }

    public void tick()
    {
        super.tick();

        if (!world.isRemote)
        {
            if (!getPassengers().isEmpty())
            {
                if (drop_timer > 0)
                {

                    if (ticksExisted % 2 == 0)
                        --drop_timer;
                } else
                {
                    this.removePassengers();
                    grabbedEntity = null;
                    pickup_timer = 200;
                }
            }

            if (pickup_timer > 0)
            {
                if (ticksExisted % 2 == 0)
                    --pickup_timer;
            }
        }
    }

    public void livingTick()
    {
        super.livingTick();

        if (!hasNoGravity() && !isJumping())
        {
            Vec3d vec3d = this.getMotion();
            if (!this.onGround && vec3d.y < 0.0D)
            {
                this.setMotion(vec3d.mul(1.0D, 0.6D, 1.0D));
            }
        }

        if (isScared)
        {
            if (ticksExisted % 5 == 0)
                --scared_timer;

            if (scared_timer <= 0)
            {
                this.setNoGravity(false);
                this.isScared = false;
                this.goalSelector.removeGoal(flyingavoidgoal);
                this.moveController = landController;
                this.navigator = createNavigator(world);
            }
        }

        if (!this.isScared)
        {
            this.setNoGravity(false);

            if (this.jumpTicks != this.jumpDuration)
            {
                ++this.jumpTicks;
            } else if (this.jumpDuration != 0)
            {
                this.jumpTicks = 0;
                this.jumpDuration = 0;
                this.setJumping(false);
            }
        }
    }

    public boolean isJumping()
    {
        return isJumping;
    }

    public boolean isBreedingItem(ItemStack stack)
    {
        return stack.getItem() == Items.GOLDEN_CARROT;
    }

    public AgeableEntity createChild(AgeableEntity ageable)
    {
        WolpertingerEntity wolpertingerbaby = new WolpertingerEntity(RegistryHandler.WOLPERTINGER_ENTITY.get(), world);

        wolpertingerbaby.setWolpertingerType(this.getWolpertingerType());
        return wolpertingerbaby;
    }

    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (this.isInvulnerableTo(source))
        {
            return false;
        } else
        {
            Entity entity = source.getTrueSource();
            if (!this.world.isRemote && entity != null && this.canEntityBeSeen(entity) && !this.isAIDisabled())
            {
                if (isScared)
                    this.goalSelector.removeGoal(flyingavoidgoal);

                this.isScared = true;

                flyingavoidgoal = new FlyingAvoidEntityGoal(this, entity, 10.0F);
                this.goalSelector.addGoal(3, flyingavoidgoal);
                this.moveController = flyingController;
                this.navigator = createNavigator(world);
                scared_timer = 200;
            }

            return super.attackEntityFrom(source, amount);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id)
    {
        if (id == 1)
        {
            this.createRunningParticles();
            this.jumpDuration = 10;
            this.jumpTicks = 0;
        } else
        {
            super.handleStatusUpdate(id);
        }
    }

    public boolean onLivingFall(float distance, float damageMultiplier)
    {
        return false;
    }

    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos)
    {
    }

    public class JumpHelperController extends JumpController
    {
        private final WolpertingerEntity wolpertinger;
        private boolean canJump;

        public JumpHelperController(WolpertingerEntity _wolpertinger)
        {
            super(_wolpertinger);
            this.wolpertinger = _wolpertinger;
        }

        public boolean getIsJumping()
        {
            return this.isJumping;
        }

        public boolean canJump()
        {
            return this.canJump;
        }

        public void setCanJump(boolean canJumpIn)
        {
            this.canJump = canJumpIn;
        }

        /**
         * Called to actually make the entity jump if isJumping is true.
         */
        public void tick()
        {
            if (this.isJumping && !isScared)
            {
                this.wolpertinger.startJumping();
                this.isJumping = false;
            }
        }
    }

    static class MoveHelperController extends MovementController
    {
        private final WolpertingerEntity wolpertinger;
        private double nextJumpSpeed;

        public MoveHelperController(WolpertingerEntity _wolpertinger)
        {
            super(_wolpertinger);
            this.wolpertinger = _wolpertinger;
        }

        public void tick()
        {
            if (this.wolpertinger.onGround && !this.wolpertinger.isJumping && !((WolpertingerEntity.JumpHelperController) this.wolpertinger.jumpController).getIsJumping())
            {
                this.wolpertinger.setMovementSpeed(0.0D);
            } else if (this.isUpdating())
            {
                this.wolpertinger.setMovementSpeed(this.nextJumpSpeed);
                this.wolpertinger.moving_timer = 0.0F;
            }

            super.tick();
        }

        /**
         * Sets the speed and location to move to
         */
        public void setMoveTo(double x, double y, double z, double speedIn)
        {
            if (this.wolpertinger.isInWater())
            {
                speedIn = 1.5D;
            }

            super.setMoveTo(x, y, z, speedIn);
            if (speedIn > 0.0D)
            {
                this.nextJumpSpeed = speedIn;
            }
        }
    }

    static class FlyingAvoidEntityGoal extends Goal
    {
        private final WolpertingerEntity wolpertinger;
        private float avoidDistance;
        private Entity avoidTarget;

        public FlyingAvoidEntityGoal(WolpertingerEntity _wolpertinger, Entity p_i46403_2_, float p_i46403_3_)
        {
            this.wolpertinger = _wolpertinger;
            this.avoidTarget = p_i46403_2_;
            this.avoidDistance = p_i46403_3_;
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        @Override
        public boolean shouldExecute()
        {
            return this.wolpertinger.isScared;
        }

        public boolean shouldContinueExecuting()
        {
            return this.wolpertinger.isScared;
        }

        public void tick()
        {
            if (this.wolpertinger.getDistance(avoidTarget) < avoidDistance)
            {
                Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.wolpertinger, 10, 5, this.avoidTarget.getPositionVec());
                if (vec3d != null)
                {
                    this.wolpertinger.setNoGravity(true);
                    this.wolpertinger.MoveToPos(new BlockPos(vec3d.x, vec3d.y, vec3d.z));
                }
            } else
            {
                this.wolpertinger.setNoGravity(false);
                if (this.wolpertinger.ticksExisted % 5 == 0)
                    --this.wolpertinger.scared_timer;
            }
        }
    }


    static class JumpToGrabGoal extends Goal
    {
        private final WolpertingerEntity wolpertinger;
        private float checkDist;
        private Entity tryGrabTarget;

        public JumpToGrabGoal(WolpertingerEntity _entity, float checkDistIn)
        {
            wolpertinger = _entity;
            checkDist = checkDistIn;
        }

        @Override
        public boolean shouldExecute()
        {
            if (!this.wolpertinger.isScared && this.wolpertinger.grabbedEntity == null && this.wolpertinger.pickup_timer <= 0.0F && this.wolpertinger.getGrowingAge() >= 0)
            {
                for (Class entClass : this.wolpertinger.grabTargets)
                {
                    List<LivingEntity> list = this.wolpertinger.world.getEntitiesWithinAABB(entClass, (new AxisAlignedBB(this.wolpertinger.getPosX(), this.wolpertinger.getPosY(), this.wolpertinger.getPosZ(), this.wolpertinger.getPosX() + 1.0D, this.wolpertinger.getPosY() + 1.0D, this.wolpertinger.getPosZ() + 1.0D)).grow(checkDist, checkDist, checkDist));
                    Iterator iterator = list.iterator();

                    while (true)
                    {
                        if (!iterator.hasNext())
                        {
                            break;
                        }

                        LivingEntity _ent = (LivingEntity) iterator.next();

                        if (_ent.getRidingEntity() != null)
                            continue;

                        if (tryGrabTarget != null)
                        {
                            if (this.wolpertinger.getDistanceSq(tryGrabTarget) > this.wolpertinger.getDistanceSq(_ent))
                                tryGrabTarget = _ent;
                        } else
                            tryGrabTarget = _ent;
                    }
                }

                if (tryGrabTarget != null)
                    return true;
            }


            return false;
        }

        public void resetTask()
        {
            tryGrabTarget = null;
        }

        public boolean shouldContinueExecuting()
        {
            return this.wolpertinger.grabbedEntity == null && tryGrabTarget.getDistanceSq(this.wolpertinger) < 40;
        }

        public void tick()
        {
            this.wolpertinger.navigator.tryMoveToXYZ(tryGrabTarget.getPosition().getX(), tryGrabTarget.getPosition().getY(), tryGrabTarget.getPosition().getZ(), this.wolpertinger.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue());

            if (this.wolpertinger.onGround && tryGrabTarget.getDistanceSq(this.wolpertinger) < 10)
            {
                Vec3d dir = tryGrabTarget.getPositionVec().subtract(this.wolpertinger.getPositionVec()).normalize();
                Vec3d motion = this.wolpertinger.getMotion().add(dir.x, dir.y * 1.5F, dir.z);

                this.wolpertinger.setMotion(motion.getX(), motion.getY(), motion.getZ());
            }

            if (this.wolpertinger.getBoundingBox().intersects(tryGrabTarget.getBoundingBox()))
            {
                tryGrabTarget.startRiding(this.wolpertinger, true);
                this.wolpertinger.grabbedEntity = tryGrabTarget;
                this.wolpertinger.drop_timer = 200;
            }
        }
    }

    static class RandomWalkingWithRidden extends WaterAvoidingRandomWalkingGoal
    {
        public RandomWalkingWithRidden(CreatureEntity creature, double speedIn)
        {
            super(creature, speedIn);
        }

        public boolean shouldExecute()
        {
            if (!this.mustUpdate)
            {
                if (this.creature.getIdleTime() >= 100)
                {
                    return false;
                }

                if (this.creature.getRNG().nextInt(this.executionChance) != 0)
                {
                    return false;
                }
            }

            Vec3d vec3d = this.getPosition();
            if (vec3d == null)
            {
                return false;
            } else
            {
                this.x = vec3d.x;
                this.y = vec3d.y;
                this.z = vec3d.z;
                this.mustUpdate = false;
                return true;
            }
        }

        public boolean shouldContinueExecuting()
        {
            return !this.creature.getNavigator().noPath();
        }
    }

    static class LookAtWithPassenger extends LookAtGoal
    {
        public LookAtWithPassenger(MobEntity entityIn, Class<? extends LivingEntity> watchTargetClass, float maxDistance)
        {
            super(entityIn, watchTargetClass, maxDistance);
        }

        public boolean shouldExecute()
        {
            return this.entity.getPassengers().isEmpty() && super.shouldExecute();
        }
    }

}