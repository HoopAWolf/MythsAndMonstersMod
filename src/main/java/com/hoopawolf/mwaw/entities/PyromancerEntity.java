package com.hoopawolf.mwaw.entities;

import com.hoopawolf.mwaw.client.animation.AnimationHelper;
import com.hoopawolf.mwaw.client.animation.PercentageRotation;
import com.hoopawolf.mwaw.entities.ai.controller.MWAWMovementController;
import com.hoopawolf.mwaw.entities.ai.navigation.MWAWPathNavigateGround;
import com.hoopawolf.mwaw.entities.projectiles.SpiritBombEntity;
import com.hoopawolf.mwaw.network.MWAWPacketHandler;
import com.hoopawolf.mwaw.network.packets.client.SpawnParticleMessage;
import com.hoopawolf.mwaw.util.EntityRegistryHandler;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.BlazeEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.PillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Rotations;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.EnumSet;

public class PyromancerEntity extends CreatureEntity implements IRangedAttackMob
{
    private static final DataParameter<Boolean> IS_FLYING = EntityDataManager.createKey(PyromancerEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> ATTACKING_ARM = EntityDataManager.createKey(PyromancerEntity.class, DataSerializers.BOOLEAN); //true - left false - right
    private static final DataParameter<Integer> STATE = EntityDataManager.createKey(PyromancerEntity.class, DataSerializers.VARINT); //STATE: NORMAL/EXPLOSIVE FIRE CHARGE, SPIRIT BOMB, FIRE RAIN, FIRE SPIRIT
    private static final DataParameter<Float> SHOOTING = EntityDataManager.createKey(PyromancerEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> FIRE_CHARGE_RAIN = EntityDataManager.createKey(PyromancerEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> SPIRIT_BOMB = EntityDataManager.createKey(PyromancerEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> FIRE_SPIRIT = EntityDataManager.createKey(PyromancerEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> EXPLOSIVE_FIRE_CHARGE = EntityDataManager.createKey(PyromancerEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> ANIMATION_SPEED = EntityDataManager.createKey(PyromancerEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Rotations> HEAD_ROTATION = EntityDataManager.createKey(PyromancerEntity.class, DataSerializers.ROTATIONS);
    private static final DataParameter<Rotations> BODY_ROTATION = EntityDataManager.createKey(PyromancerEntity.class, DataSerializers.ROTATIONS);
    private static final DataParameter<Rotations> RIGHT_ARM_ROTATION = EntityDataManager.createKey(PyromancerEntity.class, DataSerializers.ROTATIONS);
    private static final DataParameter<Rotations> LEFT_ARM_ROTATION = EntityDataManager.createKey(PyromancerEntity.class, DataSerializers.ROTATIONS);
    private static final DataParameter<Rotations> RIGHT_LEG_ROTATION = EntityDataManager.createKey(PyromancerEntity.class, DataSerializers.ROTATIONS);
    private static final DataParameter<Rotations> LEFT_LEG_ROTATION = EntityDataManager.createKey(PyromancerEntity.class, DataSerializers.ROTATIONS);
    public final AnimationHelper animation = new AnimationHelper();
    MovementController groundController,
            airController;
    private ArrayList<FireSpiritEntity> spiritList = new ArrayList<>();

    public PyromancerEntity(EntityType<? extends PyromancerEntity> type, World worldIn)
    {
        super(type, worldIn);

        animation.registerData(HEAD_ROTATION);
        animation.registerData(BODY_ROTATION);
        animation.registerData(RIGHT_ARM_ROTATION);
        animation.registerData(LEFT_ARM_ROTATION);
        animation.registerData(LEFT_LEG_ROTATION);
        animation.registerData(RIGHT_LEG_ROTATION);

        groundController = new MWAWMovementController(this, 30);
        airController = new PyromancerEntity.MoveHelperController(this);
        this.moveController = groundController;
    }

    public static AttributeModifierMap.MutableAttribute func_234321_m_()
    {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 100.0D).createMutableAttribute(Attributes.FOLLOW_RANGE, 12.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.35D);
    }

    @Override
    protected void registerData()
    {
        super.registerData();
        this.dataManager.register(SHOOTING, 0F);
        this.dataManager.register(ATTACKING_ARM, false);
        this.dataManager.register(IS_FLYING, false);
        this.dataManager.register(STATE, 0);
        this.dataManager.register(FIRE_CHARGE_RAIN, 0F);
        this.dataManager.register(SPIRIT_BOMB, 0F);
        this.dataManager.register(FIRE_SPIRIT, 0F);
        this.dataManager.register(EXPLOSIVE_FIRE_CHARGE, 0F);
        this.dataManager.register(ANIMATION_SPEED, 0F);

        this.dataManager.register(HEAD_ROTATION, new Rotations(0, 0, 0));
        this.dataManager.register(BODY_ROTATION, new Rotations(0, 0, 0));
        this.dataManager.register(RIGHT_ARM_ROTATION, new Rotations(0, 0, 0));
        this.dataManager.register(LEFT_ARM_ROTATION, new Rotations(0, 0, 0));
        this.dataManager.register(LEFT_LEG_ROTATION, new Rotations(0, 0, 0));
        this.dataManager.register(RIGHT_LEG_ROTATION, new Rotations(0, 0, 0));
    }

    @Override
    protected void registerGoals()
    {
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(2, new SpiritSummoningGoal(this));
        this.goalSelector.addGoal(2, new SpiritBombGoal(this));
        this.goalSelector.addGoal(3, new PyroExplosiveRangedAttackGoal(this, 1.0D, 20.0F));
        this.goalSelector.addGoal(4, new PyroRangedAttackGoal(this, 1.0D, 10.0F));
        this.goalSelector.addGoal(5, new PyroWaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new PyromancerEntity.MoveRandomGoal());
        this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 20.0F));
        this.goalSelector.addGoal(8, new LookAtGoal(this, CreatureEntity.class, 20.0F));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, CreatureEntity.class, 10, true, false, (p_213621_0_) ->
        {
            return !(p_213621_0_ instanceof PillagerEntity) && !(p_213621_0_ instanceof PyromancerEntity) && !(p_213621_0_ instanceof BlazeEntity) && !(p_213621_0_ instanceof FireSpiritEntity);  //TODO future cult member
        }));
    }

    @Override
    protected PathNavigator createNavigator(World world)
    {
        return new MWAWPathNavigateGround(this, world);
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


    public boolean getAttackingArm()
    {
        return this.dataManager.get(ATTACKING_ARM);
    }

    public void setAttackingArm(boolean isLeft)
    {
        this.dataManager.set(ATTACKING_ARM, isLeft);
    }

    public float getAnimationSpeed()
    {
        return this.dataManager.get(ANIMATION_SPEED);
    }

    public void setAnimationSpeed(float speedIn)
    {
        this.dataManager.set(ANIMATION_SPEED, speedIn);
    }

    public boolean isFlying()
    {
        return this.dataManager.get(IS_FLYING);
    }

    public void setFlying(boolean isFlying)
    {
        this.dataManager.set(IS_FLYING, isFlying);
    }

    public int getState()
    {
        return this.dataManager.get(STATE);
    }

    public void setState(int state)
    {
        this.dataManager.set(STATE, state);
    }

    public float getShootingTimer()
    {
        return this.dataManager.get(SHOOTING);
    }

    public void setShootingTimer(float _shootTimer)
    {
        this.dataManager.set(SHOOTING, _shootTimer);
    }

    public float getFireChargeRainTimer()
    {
        return this.dataManager.get(FIRE_CHARGE_RAIN);
    }

    public void setFireChargeRainTimer(float timer)
    {
        this.dataManager.set(FIRE_CHARGE_RAIN, timer);
    }

    public float getSpiritBombTimer()
    {
        return this.dataManager.get(SPIRIT_BOMB);
    }

    public void setSpiritBombTimer(float timer)
    {
        this.dataManager.set(SPIRIT_BOMB, timer);
    }

    public float getFireSpiritTimer()
    {
        return this.dataManager.get(FIRE_SPIRIT);
    }

    public void setFireSpiritTimer(float timer)
    {
        this.dataManager.set(FIRE_SPIRIT, timer);
    }

    public float getExplosiveFireChargeTimer()
    {
        return this.dataManager.get(EXPLOSIVE_FIRE_CHARGE);
    }

    public void setExplosiveFireChargeTimer(float timer)
    {
        this.dataManager.set(EXPLOSIVE_FIRE_CHARGE, timer);
    }

    @Override
    protected boolean isDespawnPeaceful()
    {
        return true;
    }

    @Override
    public CreatureAttribute getCreatureAttribute()
    {
        return CreatureAttribute.UNDEFINED;
    }

    @Override
    public float getBrightness()
    {
        return 1.0F;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_BLAZE_DEATH;
    }

    @Override
    public void tick()
    {
        super.tick();

        if (!world.isRemote)
        {
            Vector3d vec3d = this.getMotion();

            Vector3d _vec = new Vector3d(this.getPosX(), this.getPosYHeight(1.2D), this.getPosZ());
            SpawnParticleMessage spawnParticleMessage = new SpawnParticleMessage(_vec, new Vector3d(0, 0.1f, 0), 2, 10, getWidth() * 0.35F);
            MWAWPacketHandler.packetHandler.sendToDimension(this.world.func_234923_W_(), spawnParticleMessage);

            if (!this.onGround && vec3d.y < 0.0D)
            {
                this.setMotion(vec3d.mul(1.0D, 0.6D, 1.0D));

                Vector3d leg_vec = new Vector3d(this.getPosX(), this.getPosY(), this.getPosZ());
                SpawnParticleMessage spawnFlyingParticleMessage = new SpawnParticleMessage(leg_vec, new Vector3d(0, -0.1f, 0), 2, 10, getWidth() * 0.35F);
                MWAWPacketHandler.packetHandler.sendToDimension(this.world.func_234923_W_(), spawnFlyingParticleMessage);
            }

            if (isBurning())
            {
                extinguish();
            }

            setFlying(getAttackTarget() != null && getAttackTarget().isAlive());

            if (isFlying())
            {
                Vector3d leg_vec = new Vector3d(this.getPosX(), this.getPosY(), this.getPosZ());
                SpawnParticleMessage spawnFlyingParticleMessage = new SpawnParticleMessage(leg_vec, new Vector3d(0, -0.1f, 0), 2, 10, getWidth() * 0.35F);
                MWAWPacketHandler.packetHandler.sendToDimension(this.world.func_234923_W_(), spawnFlyingParticleMessage);

                if (!this.moveController.equals(airController))
                {
                    this.moveController = airController;
                    this.setNoGravity(true);
                }
            } else
            {
                if (!this.moveController.equals(groundController))
                {
                    this.moveController = groundController;
                    this.setNoGravity(false);
                }
            }
        } else
        {
            switch (getState())
            {
                case 0:
                {
                    if (getShootingTimer() > 0.5F)
                    {
                        setAnimationSpeed(0.05F);
                        if (getAttackingArm())
                        {
                            animation.registerRotationPoints(RIGHT_ARM_ROTATION, new PercentageRotation(getRightArmRotation(), new Rotations(-90, -15, 0)));
                        } else
                        {
                            animation.registerRotationPoints(LEFT_ARM_ROTATION, new PercentageRotation(getLeftArmRotation(), new Rotations(-90, 15, 0)));
                        }
                    } else if (getShootingTimer() > 0.1F)
                    {
                        setAnimationSpeed(0.1F);
                        if (getAttackingArm())
                        {
                            animation.registerRotationPoints(RIGHT_ARM_ROTATION, new PercentageRotation(getRightArmRotation(), new Rotations(-145, 30, 0)));
                        } else
                        {
                            animation.registerRotationPoints(LEFT_ARM_ROTATION, new PercentageRotation(getLeftArmRotation(), new Rotations(-145, -30, 0)));
                        }
                    } else if (getExplosiveFireChargeTimer() > 0.5F)
                    {
                        setAnimationSpeed(0.05F);
                        animation.registerRotationPoints(RIGHT_ARM_ROTATION, new PercentageRotation(getRightArmRotation(), new Rotations(-90, -15, 0)));
                        animation.registerRotationPoints(LEFT_ARM_ROTATION, new PercentageRotation(getLeftArmRotation(), new Rotations(-90, 15, 0)));
                    } else if (getExplosiveFireChargeTimer() > 0.1F)
                    {
                        setAnimationSpeed(0.1F);
                        animation.registerRotationPoints(RIGHT_ARM_ROTATION, new PercentageRotation(getRightArmRotation(), new Rotations(-145, 30, 0)));
                        animation.registerRotationPoints(LEFT_ARM_ROTATION, new PercentageRotation(getLeftArmRotation(), new Rotations(-145, -30, 0)));
                    } else
                    {
                        setAnimationSpeed(0.1F);
                        animation.registerRotationPoints(HEAD_ROTATION, new PercentageRotation(getHeadRotation(), new Rotations(0, 0, 0)));
                        animation.registerRotationPoints(BODY_ROTATION, new PercentageRotation(getBodyRotation(), new Rotations(0, 0, 0)));
                        animation.registerRotationPoints(RIGHT_ARM_ROTATION, new PercentageRotation(getRightArmRotation(), new Rotations(0, 0, 0)));
                        animation.registerRotationPoints(LEFT_ARM_ROTATION, new PercentageRotation(getLeftArmRotation(), new Rotations(0, 0, 0)));

                        animation.registerRotationPoints(RIGHT_LEG_ROTATION, new PercentageRotation(getRightLegRotation(), new Rotations(0, 0, 0)));
                        animation.registerRotationPoints(LEFT_LEG_ROTATION, new PercentageRotation(getLeftLegRotation(), new Rotations(0, 0, 0)));
                    }
                }
                break;

                case 1:
                case 2:
                {
                    if (getSpiritBombTimer() > 0.5F || getFireSpiritTimer() > 0.5F)
                    {
                        setAnimationSpeed(0.05F);
                        animation.registerRotationPoints(HEAD_ROTATION, new PercentageRotation(getHeadRotation(), new Rotations(-42.5F, 0, 0)));
                        animation.registerRotationPoints(RIGHT_ARM_ROTATION, new PercentageRotation(getRightArmRotation(), new Rotations(-145, 42.5F, 12.5F)));
                        animation.registerRotationPoints(LEFT_ARM_ROTATION, new PercentageRotation(getLeftArmRotation(), new Rotations(-145, -35, 12.5F)));
                    } else if (getSpiritBombTimer() > 0.1F || getFireSpiritTimer() > 0.1F)
                    {
                        setAnimationSpeed(0.2F);
                        animation.registerRotationPoints(HEAD_ROTATION, new PercentageRotation(getHeadRotation(), new Rotations(0, 0, 0)));
                        animation.registerRotationPoints(RIGHT_ARM_ROTATION, new PercentageRotation(getRightArmRotation(), new Rotations(-90, -15, 0)));
                        animation.registerRotationPoints(LEFT_ARM_ROTATION, new PercentageRotation(getLeftArmRotation(), new Rotations(-90, 15, 0)));
                    } else
                    {
                        setAnimationSpeed(0.1F);
                        animation.registerRotationPoints(HEAD_ROTATION, new PercentageRotation(getHeadRotation(), new Rotations(0, 0, 0)));
                        animation.registerRotationPoints(BODY_ROTATION, new PercentageRotation(getBodyRotation(), new Rotations(0, 0, 0)));
                        animation.registerRotationPoints(RIGHT_ARM_ROTATION, new PercentageRotation(getRightArmRotation(), new Rotations(0, 0, 0)));
                        animation.registerRotationPoints(LEFT_ARM_ROTATION, new PercentageRotation(getLeftArmRotation(), new Rotations(0, 0, 0)));

                        animation.registerRotationPoints(RIGHT_LEG_ROTATION, new PercentageRotation(getRightLegRotation(), new Rotations(0, 0, 0)));
                        animation.registerRotationPoints(LEFT_LEG_ROTATION, new PercentageRotation(getLeftLegRotation(), new Rotations(0, 0, 0)));
                    }
                }
                break;
            }
        }
    }

    @Override
    public boolean onLivingFall(float distance, float damageMultiplier)
    {
        return false;
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
            if (source.getTrueSource() instanceof BlazeEntity)
            {
                return false;
            } else if (source.damageType.equals(DamageSource.ON_FIRE.damageType) || source.damageType.equals(DamageSource.IN_FIRE.damageType) || source.isExplosion() || source.getTrueSource() instanceof PyromancerEntity)
            {
                return false;
            }

            return super.attackEntityFrom(source, amount);
        }
    }

    @Override
    public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor)
    {
        double d0 = this.getDistanceSq(target);
        float f = MathHelper.sqrt(MathHelper.sqrt(d0)) * 0.5F;
        double d1 = target.getPosX() - this.getPosX();
        double d2 = target.getPosYHeight(0.5D) - this.getPosYHeight(0.5D);
        double d3 = target.getPosZ() - this.getPosZ();

        SmallFireballEntity smallfireballentity = new SmallFireballEntity(this.world, this, d1 + this.getRNG().nextGaussian() * (double) f, d2, d3 + this.getRNG().nextGaussian() * (double) f);
        smallfireballentity.setPosition(smallfireballentity.getPosX(), this.getPosYHeight(0.5D) + 0.5D, smallfireballentity.getPosZ());
        this.world.addEntity(smallfireballentity);
        this.playSound(SoundEvents.ENTITY_BLAZE_SHOOT, 1.0F, 0.5F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
    }

    public void attackEntityWithExplosiveRangedAttack(LivingEntity target, float distanceFactor)
    {
        double d0 = this.getDistanceSq(target);
        float f = MathHelper.sqrt(MathHelper.sqrt(d0)) * 0.5F;
        double d1 = target.getPosX() - this.getPosX();
        double d2 = target.getPosYHeight(0.5D) - this.getPosYHeight(0.5D);
        double d3 = target.getPosZ() - this.getPosZ();

        FireballEntity smallfireballentity = new FireballEntity(this.world, this, d1 + this.getRNG().nextGaussian() * (double) f, d2, d3 + this.getRNG().nextGaussian() * (double) f);
        smallfireballentity.setPosition(smallfireballentity.getPosX(), this.getPosYHeight(0.5D) + 0.5D, smallfireballentity.getPosZ());
        this.world.addEntity(smallfireballentity);
        this.playSound(SoundEvents.ENTITY_BLAZE_SHOOT, 1.0F, 0.2F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
    }

    class MoveHelperController extends MovementController
    {
        public MoveHelperController(PyromancerEntity vex)
        {
            super(vex);
        }

        @Override
        public void tick()
        {
            if (this.action == MovementController.Action.MOVE_TO)
            {
                Vector3d vec3d = new Vector3d(this.posX - PyromancerEntity.this.getPosX(), this.posY - PyromancerEntity.this.getPosY(), this.posZ - PyromancerEntity.this.getPosZ());
                double d0 = vec3d.length();
                if (d0 < PyromancerEntity.this.getBoundingBox().getAverageEdgeLength())
                {
                    this.action = MovementController.Action.WAIT;
                    PyromancerEntity.this.setMotion(PyromancerEntity.this.getMotion().scale(0.5D));
                } else
                {
                    PyromancerEntity.this.setMotion(PyromancerEntity.this.getMotion().add(vec3d.scale(this.speed * 0.05D / d0)));
                    if (PyromancerEntity.this.getAttackTarget() == null)
                    {
                        Vector3d vec3d1 = PyromancerEntity.this.getMotion();
                        PyromancerEntity.this.rotationYaw = -((float) MathHelper.atan2(vec3d1.x, vec3d1.z)) * (180F / (float) Math.PI);
                    } else
                    {
                        double d2 = PyromancerEntity.this.getAttackTarget().getPosX() - PyromancerEntity.this.getPosX();
                        double d1 = PyromancerEntity.this.getAttackTarget().getPosZ() - PyromancerEntity.this.getPosZ();
                        PyromancerEntity.this.rotationYaw = -((float) MathHelper.atan2(d2, d1)) * (180F / (float) Math.PI);
                    }
                    PyromancerEntity.this.renderYawOffset = PyromancerEntity.this.rotationYaw;
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

        @Override
        public boolean shouldExecute()
        {
            return !PyromancerEntity.this.getMoveHelper().isUpdating() && PyromancerEntity.this.isFlying() && PyromancerEntity.this.getAttackTarget() != null && PyromancerEntity.this.getAttackTarget().isAlive();
        }

        @Override
        public boolean shouldContinueExecuting()
        {
            return PyromancerEntity.this.getAttackTarget() != null && PyromancerEntity.this.getAttackTarget().isAlive();
        }

        @Override
        public void resetTask()
        {
            PyromancerEntity.this.setFlying(false);
        }

        @Override
        public void tick()
        {
            BlockPos blockpos = PyromancerEntity.this.getAttackTarget().getPosition();
            if (blockpos == null)
            {
                blockpos = PyromancerEntity.this.getPosition();
            }

            for (int i = 0; i < 3; ++i)
            {
                BlockPos blockpos1 = blockpos.add(PyromancerEntity.this.rand.nextInt(15) - 7, PyromancerEntity.this.rand.nextInt(11) - 5, PyromancerEntity.this.rand.nextInt(15) - 7);
                if (PyromancerEntity.this.world.isAirBlock(blockpos1))
                {
                    PyromancerEntity.this.moveController.setMoveTo((double) blockpos1.getX() + 0.5D, (double) blockpos1.getY() + 0.5D, (double) blockpos1.getZ() + 0.5D, 0.25D);
                    if (PyromancerEntity.this.getAttackTarget() == null)
                    {
                        PyromancerEntity.this.getLookController().setLookPosition((double) blockpos1.getX() + 0.5D, (double) blockpos1.getY() + 0.5D, (double) blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
                    }
                    break;
                }
            }

        }
    }

    private class PyroRangedAttackGoal extends Goal
    {
        private final PyromancerEntity entityHost;
        private final IRangedAttackMob rangedAttackEntityHost;
        private final double entityMoveSpeed;
        private final float attackRadius;
        private final float maxAttackDistance;
        private LivingEntity attackTarget;
        private int seeTime;
        private boolean hasShot;

        public PyroRangedAttackGoal(IRangedAttackMob attacker, double movespeed, float maxAttackDistanceIn)
        {
            entityHost = (PyromancerEntity) attacker;
            rangedAttackEntityHost = attacker;
            this.entityMoveSpeed = movespeed;
            this.attackRadius = maxAttackDistanceIn;
            this.maxAttackDistance = maxAttackDistanceIn * maxAttackDistanceIn;
        }

        @Override
        public void startExecuting()
        {
            setAttackingArm(entityHost.world.rand.nextBoolean());
            setShootingTimer(1.0F);
            hasShot = false;
        }

        @Override
        public boolean shouldExecute()
        {
            LivingEntity livingentity = this.entityHost.getAttackTarget();
            if (livingentity != null && livingentity.isAlive())
            {
                this.attackTarget = livingentity;
                return getState() == 0 && getExplosiveFireChargeTimer() == 0;
            }

            return false;
        }

        @Override
        public boolean shouldContinueExecuting()
        {
            return getState() == 0 && getShootingTimer() > 0 && this.shouldExecute();
        }

        @Override
        public void resetTask()
        {
            super.resetTask();
            this.attackTarget = null;
            setShootingTimer(0.0F);
        }

        @Override
        public void tick()
        {
            double d2 = entityHost.getAttackTarget().getPosX() - entityHost.getPosX();
            double d1 = entityHost.getAttackTarget().getPosZ() - entityHost.getPosZ();
            entityHost.rotationYaw = -((float) MathHelper.atan2(d2, d1)) * (180F / (float) Math.PI);
            entityHost.renderYawOffset = entityHost.rotationYaw;

            double d0 = this.entityHost.getDistanceSq(this.attackTarget.getPosX(), this.attackTarget.getPosY(), this.attackTarget.getPosZ());
            boolean flag = this.entityHost.getEntitySenses().canSee(this.attackTarget);
            if (flag)
            {
                ++this.seeTime;
            } else
            {
                this.seeTime = 0;
            }

            if (!(d0 > (double) this.maxAttackDistance) && this.seeTime >= 5)
            {
                this.entityHost.getNavigator().clearPath();
            } else
            {
                this.entityHost.getNavigator().tryMoveToEntityLiving(this.attackTarget, this.entityMoveSpeed);
            }

            if (entityHost.ticksExisted % 2 == 0)
            {
                if (getShootingTimer() > 0)
                {
                    setShootingTimer(getShootingTimer() - 0.1F);
                }
            }

            if (getShootingTimer() <= 0.5F && !hasShot)
            {
                Vector3d _vec = new Vector3d(entityHost.getPosX() + entityHost.getForward().getX(), entityHost.getPosYHeight(0.5D), entityHost.getPosZ() + entityHost.getForward().getZ());
                SpawnParticleMessage spawnParticleMessage = new SpawnParticleMessage(_vec, new Vector3d(0, 0.1f, 0), 2, 10, getWidth() * 0.35F);
                MWAWPacketHandler.packetHandler.sendToDimension(entityHost.world.func_234923_W_(), spawnParticleMessage);

                float f = MathHelper.sqrt(d0) / this.attackRadius;
                float lvt_5_1_ = MathHelper.clamp(f, 0.1F, 1.0F);
                this.rangedAttackEntityHost.attackEntityWithRangedAttack(this.attackTarget, lvt_5_1_);
                hasShot = true;
            }
        }
    }

    private class PyroExplosiveRangedAttackGoal extends Goal
    {
        private final PyromancerEntity entityHost;
        private final double entityMoveSpeed;
        private final float attackRadius;
        private final float maxAttackDistance;
        private LivingEntity attackTarget;
        private int seeTime;
        private boolean hasShot;

        public PyroExplosiveRangedAttackGoal(IRangedAttackMob attacker, double movespeed, float maxAttackDistanceIn)
        {
            entityHost = (PyromancerEntity) attacker;
            this.entityMoveSpeed = movespeed;
            this.attackRadius = maxAttackDistanceIn;
            this.maxAttackDistance = maxAttackDistanceIn * maxAttackDistanceIn;
        }

        @Override
        public void startExecuting()
        {
            setExplosiveFireChargeTimer(1.0F);
            hasShot = false;
        }

        @Override
        public boolean shouldExecute()
        {
            LivingEntity livingentity = this.entityHost.getAttackTarget();
            if (livingentity != null && livingentity.isAlive())
            {
                this.attackTarget = livingentity;
                return getState() == 0 && entityHost.rand.nextInt(100) < 20 && getShootingTimer() == 0;
            }

            return false;
        }

        @Override
        public boolean shouldContinueExecuting()
        {
            return getState() == 0 && getExplosiveFireChargeTimer() > 0 && this.entityHost.getAttackTarget() != null && this.entityHost.getAttackTarget().isAlive();
        }

        @Override
        public void resetTask()
        {
            super.resetTask();
            this.attackTarget = null;
            setExplosiveFireChargeTimer(0.0F);
        }

        @Override
        public void tick()
        {
            double d2 = entityHost.getAttackTarget().getPosX() - entityHost.getPosX();
            double d1 = entityHost.getAttackTarget().getPosZ() - entityHost.getPosZ();
            entityHost.rotationYaw = -((float) MathHelper.atan2(d2, d1)) * (180F / (float) Math.PI);
            entityHost.renderYawOffset = entityHost.rotationYaw;

            double d0 = this.entityHost.getDistanceSq(this.attackTarget.getPosX(), this.attackTarget.getPosY(), this.attackTarget.getPosZ());
            boolean flag = this.entityHost.getEntitySenses().canSee(this.attackTarget);
            if (flag)
            {
                ++this.seeTime;
            } else
            {
                this.seeTime = 0;
            }

            if (!(d0 > (double) this.maxAttackDistance) && this.seeTime >= 5)
            {
                this.entityHost.getNavigator().clearPath();
            } else
            {
                this.entityHost.getNavigator().tryMoveToEntityLiving(this.attackTarget, this.entityMoveSpeed);
            }

            if (entityHost.ticksExisted % 5 == 0)
            {
                if (getExplosiveFireChargeTimer() > 0)
                {
                    setExplosiveFireChargeTimer(getExplosiveFireChargeTimer() - 0.1F);
                }
            }

            if (getExplosiveFireChargeTimer() > 0.5F)
            {
                Vector3d _vec = new Vector3d(entityHost.getPosX() + entityHost.getForward().getX(), entityHost.getPosYHeight(0.5D), entityHost.getPosZ() + entityHost.getForward().getZ());
                SpawnParticleMessage spawnParticleMessage = new SpawnParticleMessage(_vec, new Vector3d(0, 0.1f, 0), 2, 10, getWidth() * 0.35F);
                MWAWPacketHandler.packetHandler.sendToDimension(entityHost.world.func_234923_W_(), spawnParticleMessage);
            } else if (getExplosiveFireChargeTimer() <= 0.5F && !hasShot)
            {
                float f = MathHelper.sqrt(d0) / this.attackRadius;
                float lvt_5_1_ = MathHelper.clamp(f, 0.1F, 1.0F);
                entityHost.attackEntityWithExplosiveRangedAttack(this.attackTarget, lvt_5_1_);
                hasShot = true;
            }
        }
    }

    private class SpiritBombGoal extends Goal
    {
        private final PyromancerEntity entityHost;
        private SpiritBombEntity bomb;
        private int coolDown;
        private LivingEntity attackTarget;
        private boolean hasShot;
        private float startHealth;

        public SpiritBombGoal(PyromancerEntity attacker)
        {
            entityHost = attacker;
            coolDown = 400;
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Flag.LOOK));
        }

        @Override
        public void startExecuting()
        {
            setSpiritBombTimer(10.0F);
            hasShot = false;
            setState(1);
            entityHost.getNavigator().clearPath();
            bomb = new SpiritBombEntity(entityHost.world, entityHost, null);
            bomb.setPosition(entityHost.getPosX(), entityHost.getPosYHeight(0.5D) + 3.5D, entityHost.getPosZ());
            entityHost.world.addEntity(bomb);
            startHealth = entityHost.getHealth();
        }

        @Override
        public boolean shouldExecute()
        {
            if (getHealth() > 50)
            {
                return false;
            }

            LivingEntity livingentity = this.entityHost.getAttackTarget();
            if (livingentity != null && livingentity.isAlive())
            {
                if (coolDown > 0)
                {
                    if (entityHost.ticksExisted % 2 == 0)
                    {
                        --coolDown;
                    }
                    return false;
                }

                this.attackTarget = livingentity;
                return getState() == 0 && entityHost.rand.nextInt(100) < 20 && attackTarget != null;
            }

            return false;
        }

        @Override
        public boolean shouldContinueExecuting()
        {
            return getState() == 1 && getSpiritBombTimer() > 0;
        }

        @Override
        public void resetTask()
        {
            super.resetTask();
            this.attackTarget = null;
            setState(0);
            coolDown = 400;
        }

        @Override
        public void tick()
        {
            if (entityHost.getAttackTarget() != null && entityHost.getAttackTarget().isAlive())
            {
                double d2 = entityHost.getAttackTarget().getPosX() - entityHost.getPosX();
                double d1 = entityHost.getAttackTarget().getPosZ() - entityHost.getPosZ();
                entityHost.rotationYaw = -((float) MathHelper.atan2(d2, d1)) * (180F / (float) Math.PI);
                entityHost.renderYawOffset = entityHost.rotationYaw;
            }

            entityHost.setMotion(0.0D, 0.0D, 0.0D);

            if (getSpiritBombTimer() > 0.5F)
            {
                bomb.increaseCharge();
                bomb.setPosition(entityHost.getPosX(), entityHost.getPosYHeight(0.5D) + 3.5D, entityHost.getPosZ());
                bomb.accelerationX = 0;
                bomb.accelerationY = 0;
                bomb.accelerationZ = 0;
                bomb.setMotion(0.0D, 0.0D, 0.0D);

                if (bomb.getChargeTimer() >= 100 || (entityHost.getHealth() / startHealth) * 100F < 80)
                {
                    setSpiritBombTimer(0.5F);
                }
            } else if (getSpiritBombTimer() <= 0.5F)
            {
                if (entityHost.ticksExisted % 3 == 0)
                {
                    if (getSpiritBombTimer() > 0)
                    {
                        setSpiritBombTimer(getSpiritBombTimer() - 0.1F);
                    }
                }

                if (!hasShot)
                {
                    bomb.setHaveShot(true);
                    bomb.setTarget(getAttackTarget());
                    hasShot = true;
                    entityHost.playSound(SoundEvents.ENTITY_BLAZE_SHOOT, 1.0F, 0.5F / (entityHost.getRNG().nextFloat() * 0.4F + 0.8F));
                }
            }
        }
    }

    private class SpiritSummoningGoal extends Goal
    {
        private final PyromancerEntity entityHost;
        private int coolDown;
        private LivingEntity attackTarget;

        public SpiritSummoningGoal(PyromancerEntity attacker)
        {
            entityHost = attacker;
            coolDown = 200;
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Flag.LOOK));
        }

        @Override
        public void startExecuting()
        {
            setFireSpiritTimer(1.0F);
            setState(2);
            entityHost.getNavigator().clearPath();

            ArrayList<FireSpiritEntity> temp = new ArrayList<>();
            for (FireSpiritEntity entity : spiritList)
            {
                if (entity.isAlive())
                {
                    temp.add(entity);
                }
            }

            spiritList = temp;
        }

        @Override
        public boolean shouldExecute()
        {
            LivingEntity livingentity = this.entityHost.getAttackTarget();
            if (livingentity != null && livingentity.isAlive())
            {
                if (coolDown > 0)
                {
                    if (entityHost.ticksExisted % 2 == 0)
                    {
                        --coolDown;
                    }
                    return false;
                }

                this.attackTarget = livingentity;
                return getState() == 0 && entityHost.rand.nextInt(100) < 20 && attackTarget != null;
            }

            return false;
        }

        @Override
        public boolean shouldContinueExecuting()
        {
            return getState() == 2 && getFireSpiritTimer() > 0 && entityHost.getAttackTarget() != null && entityHost.getAttackTarget().isAlive();
        }

        @Override
        public void resetTask()
        {
            super.resetTask();
            this.attackTarget = null;
            setState(0);
            coolDown = 200;
        }

        @Override
        public void tick()
        {
            if (entityHost.getAttackTarget() != null && entityHost.getAttackTarget().isAlive())
            {
                double d2 = entityHost.getAttackTarget().getPosX() - entityHost.getPosX();
                double d1 = entityHost.getAttackTarget().getPosZ() - entityHost.getPosZ();
                entityHost.rotationYaw = -((float) MathHelper.atan2(d2, d1)) * (180F / (float) Math.PI);
                entityHost.renderYawOffset = entityHost.rotationYaw;
            }

            entityHost.setMotion(0.0D, 0.0D, 0.0D);

            if (spiritList.size() < 3)
            {
                if (entityHost.ticksExisted % 10 == 0)
                {
                    FireSpiritEntity entity = EntityRegistryHandler.FIRE_SPIRIT_ENTITY.get().create(entityHost.world);
                    entity.setOwner(entityHost);
                    spiritList.add(entity);

                    entity.setPosition(entityHost.getPosX(), entityHost.getPosYHeight(0.5D) + 3.5D, entityHost.getPosZ());
                    entityHost.world.addEntity(entity);

                    entityHost.playSound(SoundEvents.ENTITY_BLAZE_SHOOT, 1.0F, 0.5F / (entityHost.getRNG().nextFloat() * 0.4F + 0.8F));
                }
            } else
            {
                setFireSpiritTimer(0.0F);
            }
        }
    }

    private class PyroWaterAvoidingRandomWalkingGoal extends WaterAvoidingRandomWalkingGoal
    {
        PyromancerEntity creature;

        public PyroWaterAvoidingRandomWalkingGoal(PyromancerEntity creatureIn, double speedIn)
        {
            super(creatureIn, speedIn);
            creature = creatureIn;
        }

        @Override
        public boolean shouldExecute()
        {
            return super.shouldExecute() && !creature.isFlying();
        }
    }
}
