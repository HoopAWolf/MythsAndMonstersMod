package com.hoopawolf.mwaw.entities;

import com.hoopawolf.mwaw.client.animation.AnimationHelper;
import com.hoopawolf.mwaw.client.animation.PercentageRotation;
import com.hoopawolf.mwaw.entities.ai.MWAWMeleeAttackGoal;
import com.hoopawolf.mwaw.entities.ai.controller.MWAWMovementController;
import com.hoopawolf.mwaw.entities.ai.navigation.MWAWPathNavigateGround;
import com.hoopawolf.mwaw.entities.helper.EntityHelper;
import com.hoopawolf.mwaw.entities.helper.MathFuncHelper;
import com.hoopawolf.mwaw.network.MWAWPacketHandler;
import com.hoopawolf.mwaw.network.packets.client.SpawnParticleMessage;
import com.hoopawolf.mwaw.network.packets.client.SpawnSuckingParticleMessage;
import com.hoopawolf.mwaw.util.ItemBlockRegistryHandler;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Rotations;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class DendroidElderEntity extends CreatureEntity
{
    private static final DataParameter<Boolean> ATTACKING_ARM = EntityDataManager.createKey(DendroidElderEntity.class, DataSerializers.BOOLEAN); //true - left false - right
    private static final DataParameter<Boolean> DEFEND_MODE = EntityDataManager.createKey(DendroidElderEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> STATE = EntityDataManager.createKey(DendroidElderEntity.class, DataSerializers.VARINT); //STATE: NORMAL MELEE, RECOVERY, SLAM ATTACk
    private static final DataParameter<Float> ABSORB_TIMER = EntityDataManager.createKey(DendroidElderEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> SLAM_TIMER = EntityDataManager.createKey(DendroidElderEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> ATTACK_TIMER = EntityDataManager.createKey(DendroidElderEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> ANIMATION_SPEED = EntityDataManager.createKey(DendroidElderEntity.class, DataSerializers.FLOAT);

    private static final DataParameter<Rotations> HEAD_ROTATION = EntityDataManager.createKey(DendroidElderEntity.class, DataSerializers.ROTATIONS);
    private static final DataParameter<Rotations> BODY_ROTATION = EntityDataManager.createKey(DendroidElderEntity.class, DataSerializers.ROTATIONS);
    private static final DataParameter<Rotations> RIGHT_ARM_ROTATION = EntityDataManager.createKey(DendroidElderEntity.class, DataSerializers.ROTATIONS);
    private static final DataParameter<Rotations> RIGHT_JOINT_ROTATION = EntityDataManager.createKey(DendroidElderEntity.class, DataSerializers.ROTATIONS);
    private static final DataParameter<Rotations> LEFT_ARM_ROTATION = EntityDataManager.createKey(DendroidElderEntity.class, DataSerializers.ROTATIONS);
    private static final DataParameter<Rotations> LEFT_JOINT_ROTATION = EntityDataManager.createKey(DendroidElderEntity.class, DataSerializers.ROTATIONS);
    private static final DataParameter<Rotations> RIGHT_LEG_ROTATION = EntityDataManager.createKey(DendroidElderEntity.class, DataSerializers.ROTATIONS);
    private static final DataParameter<Rotations> RIGHT_FOOT_ROTATION = EntityDataManager.createKey(DendroidElderEntity.class, DataSerializers.ROTATIONS);
    private static final DataParameter<Rotations> LEFT_LEG_ROTATION = EntityDataManager.createKey(DendroidElderEntity.class, DataSerializers.ROTATIONS);
    private static final DataParameter<Rotations> LEFT_FOOT_ROTATION = EntityDataManager.createKey(DendroidElderEntity.class, DataSerializers.ROTATIONS);
    public final AnimationHelper animation = new AnimationHelper();
    private final float absorbTimerMax = 250, slamTimerMax = 20, attackTimerMax = 10;
    //STATE: NORMAL MELEE, RECOVERY, SLAM ATTACk
    private float defendTimer = 0;

    public DendroidElderEntity(EntityType<? extends DendroidElderEntity> type, World worldIn)
    {
        super(type, worldIn);
        this.stepHeight = 1.0F;

        animation.registerData(HEAD_ROTATION);
        animation.registerData(BODY_ROTATION);
        animation.registerData(RIGHT_ARM_ROTATION);
        animation.registerData(RIGHT_JOINT_ROTATION);
        animation.registerData(LEFT_ARM_ROTATION);
        animation.registerData(LEFT_JOINT_ROTATION);
        animation.registerData(LEFT_LEG_ROTATION);
        animation.registerData(LEFT_FOOT_ROTATION);
        animation.registerData(RIGHT_LEG_ROTATION);
        animation.registerData(RIGHT_FOOT_ROTATION);

        this.moveController = new MWAWMovementController(this, 30);
    }

    public static AttributeModifierMap.MutableAttribute func_234321_m_()
    {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 140.0D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 7.0D)
                .createMutableAttribute(Attributes.FOLLOW_RANGE, 12.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.2D);
    }

    @Override
    protected void registerData()
    {
        super.registerData();
        this.dataManager.register(ATTACKING_ARM, false);
        this.dataManager.register(DEFEND_MODE, false);
        this.dataManager.register(STATE, 0);
        this.dataManager.register(ABSORB_TIMER, 0F);
        this.dataManager.register(SLAM_TIMER, 0F);
        this.dataManager.register(ATTACK_TIMER, 0F);
        this.dataManager.register(ANIMATION_SPEED, 0F);

        this.dataManager.register(HEAD_ROTATION, new Rotations(0, 0, 0));
        this.dataManager.register(BODY_ROTATION, new Rotations(0, 0, 0));
        this.dataManager.register(RIGHT_ARM_ROTATION, new Rotations(0, 0, 0));
        this.dataManager.register(LEFT_ARM_ROTATION, new Rotations(0, 0, 0));
        this.dataManager.register(RIGHT_JOINT_ROTATION, new Rotations(0, 0, 0));
        this.dataManager.register(LEFT_JOINT_ROTATION, new Rotations(0, 0, 0));
        this.dataManager.register(LEFT_LEG_ROTATION, new Rotations(0, 0, 0));
        this.dataManager.register(RIGHT_LEG_ROTATION, new Rotations(0, 0, 0));
        this.dataManager.register(LEFT_FOOT_ROTATION, new Rotations(0, 0, 0));
        this.dataManager.register(RIGHT_FOOT_ROTATION, new Rotations(0, 0, 0));
    }

    @Override
    protected PathNavigator createNavigator(World world)
    {
        return new MWAWPathNavigateGround(this, world);
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

    public boolean isDefensiveMode()
    {
        return this.dataManager.get(DEFEND_MODE);
    }

    public void setDefendMode(boolean isDefending)
    {
        this.dataManager.set(DEFEND_MODE, isDefending);
    }

    public int getState()
    {
        return this.dataManager.get(STATE);
    }

    public void setState(int state)
    {
        this.dataManager.set(STATE, state);
    }

    public float getAbsorbTimer()
    {
        return this.dataManager.get(ABSORB_TIMER);
    }

    public void setAbsorbTimer(float timer)
    {
        this.dataManager.set(ABSORB_TIMER, timer);
    }

    public float getSlamTimer()
    {
        return this.dataManager.get(SLAM_TIMER);
    }

    public void setSlamTimer(float timer)
    {
        this.dataManager.set(SLAM_TIMER, timer);
    }

    public float getAttackTimer()
    {
        return this.dataManager.get(ATTACK_TIMER);
    }

    public void setAttackTimer(float timer)
    {
        this.dataManager.set(ATTACK_TIMER, timer);
    }

    public float getAbsorbTimerMax()
    {
        return absorbTimerMax;
    }

    public float getSlamTimerMax()
    {
        return slamTimerMax;
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

    public Rotations getRightJointRotation()
    {
        return this.dataManager.get(RIGHT_JOINT_ROTATION);
    }

    public Rotations getLeftArmRotation()
    {
        return this.dataManager.get(LEFT_ARM_ROTATION);
    }

    public Rotations getLeftJointRotation()
    {
        return this.dataManager.get(LEFT_JOINT_ROTATION);
    }

    public Rotations getRightLegRotation()
    {
        return this.dataManager.get(RIGHT_LEG_ROTATION);
    }

    public Rotations getRightFootRotation()
    {
        return this.dataManager.get(RIGHT_FOOT_ROTATION);
    }

    public Rotations getLeftLegRotation()
    {
        return this.dataManager.get(LEFT_LEG_ROTATION);
    }

    public Rotations getLeftFootRotation()
    {
        return this.dataManager.get(LEFT_FOOT_ROTATION);
    }

    @Override
    protected void registerGoals()
    {
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(0, new ElderRecoveryGoal(this));
        this.goalSelector.addGoal(0, new ElderGroundSlamGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 4.0F));
        this.goalSelector.addGoal(8, new LookAtGoal(this, CreatureEntity.class, 4.0F));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, CreatureEntity.class, 10, true, false, (p_213621_0_) ->
        {
            return !(p_213621_0_ instanceof DendroidEntity) && !(p_213621_0_ instanceof DendroidElderEntity);
        }));
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
    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn)
    {
        return worldIn.canSeeSky(getPosition()) && worldIn.getBlockState(this.getPositionUnderneath()).getBlock().equals(Blocks.GRASS_BLOCK);
    }

    @Override
    public void tick()
    {
        super.tick();

        if (!world.isRemote)
        {
            switch (getState())
            {
                case 0:
                {
                    if (defendTimer > 0)
                    {
                        if (ticksExisted % 2 == 0)
                        {
                            --defendTimer;
                        }
                    } else if (isDefensiveMode())
                    {
                        setDefendMode(false);
                    }

                    if (getAttackTimer() > 0)
                    {
                        setAttackTimer(getAttackTimer() - 1.0F);
                    }
                }
                break;
            }
        } else
        {
            switch (getState())
            {
                case 0:
                {
                    setAnimationSpeed(0.08F);

                    if (isDefensiveMode())
                    {
                        animation.registerRotationPoints(RIGHT_JOINT_ROTATION, new PercentageRotation(getRightJointRotation(), new Rotations(-25, -45, -75)));
                        animation.registerRotationPoints(RIGHT_ARM_ROTATION, new PercentageRotation(getRightArmRotation(), new Rotations(-45, 0, 0)));
                    }

                    if (getAttackTimer() > attackTimerMax * 0.3F)
                    {
                        if (getAttackingArm())
                        {
                            animation.registerRotationPoints(LEFT_ARM_ROTATION, new PercentageRotation(getLeftArmRotation(), new Rotations(-95, -35, 0)));
                            animation.registerRotationPoints(LEFT_JOINT_ROTATION, new PercentageRotation(getLeftJointRotation(), new Rotations(-55, 0, 0)));
                            animation.registerRotationPoints(BODY_ROTATION, new PercentageRotation(getBodyRotation(), new Rotations(-15, -11, 0)));
                        } else
                        {
                            animation.registerRotationPoints(RIGHT_ARM_ROTATION, new PercentageRotation(getRightArmRotation(), new Rotations(-95, 35, 0)));
                            animation.registerRotationPoints(RIGHT_JOINT_ROTATION, new PercentageRotation(getRightJointRotation(), new Rotations(-55, 0, 0)));
                            animation.registerRotationPoints(BODY_ROTATION, new PercentageRotation(getBodyRotation(), new Rotations(-15, 11, 0)));
                        }
                    } else if (getAttackTimer() > 0)
                    {
                        if (getAttackingArm())
                        {
                            animation.registerRotationPoints(LEFT_ARM_ROTATION, new PercentageRotation(getLeftArmRotation(), new Rotations(-55, 25, 0)));
                            animation.registerRotationPoints(LEFT_JOINT_ROTATION, new PercentageRotation(getLeftJointRotation(), new Rotations(0, 0, 20)));
                            animation.registerRotationPoints(BODY_ROTATION, new PercentageRotation(getBodyRotation(), new Rotations(10, 22, -3)));
                        } else
                        {
                            animation.registerRotationPoints(RIGHT_ARM_ROTATION, new PercentageRotation(getRightArmRotation(), new Rotations(-55, -25, 0)));
                            animation.registerRotationPoints(RIGHT_JOINT_ROTATION, new PercentageRotation(getRightJointRotation(), new Rotations(0, 0, -20)));
                            animation.registerRotationPoints(BODY_ROTATION, new PercentageRotation(getBodyRotation(), new Rotations(10, -22, 3)));
                        }
                    } else
                    {
                        if (!isDefensiveMode())
                        {
                            animation.registerRotationPoints(RIGHT_JOINT_ROTATION, new PercentageRotation(getRightJointRotation(), new Rotations(0, 0, 0)));
                            animation.registerRotationPoints(RIGHT_ARM_ROTATION, new PercentageRotation(getRightArmRotation(), new Rotations(0, 0, 0)));
                        }

                        animation.registerRotationPoints(HEAD_ROTATION, new PercentageRotation(getHeadRotation(), new Rotations(0, 0, 0)));
                        animation.registerRotationPoints(BODY_ROTATION, new PercentageRotation(getBodyRotation(), new Rotations(0, 0, 0)));

                        animation.registerRotationPoints(LEFT_ARM_ROTATION, new PercentageRotation(getLeftArmRotation(), new Rotations(0, 0, 0)));
                        animation.registerRotationPoints(LEFT_JOINT_ROTATION, new PercentageRotation(getLeftJointRotation(), new Rotations(0, 0, 0)));

                        animation.registerRotationPoints(RIGHT_LEG_ROTATION, new PercentageRotation(getRightLegRotation(), new Rotations(0, 0, 0)));
                        animation.registerRotationPoints(LEFT_LEG_ROTATION, new PercentageRotation(getLeftLegRotation(), new Rotations(0, 0, 0)));
                        animation.registerRotationPoints(RIGHT_FOOT_ROTATION, new PercentageRotation(getRightFootRotation(), new Rotations(0, 0, 0)));
                        animation.registerRotationPoints(LEFT_FOOT_ROTATION, new PercentageRotation(getLeftFootRotation(), new Rotations(0, 0, 0)));
                    }
                }
                break;

                case 1:
                {
                    if (getAbsorbTimer() < absorbTimerMax * 0.90F)
                    {
                        setAnimationSpeed(0.05F);

                        animation.registerRotationPoints(HEAD_ROTATION, new PercentageRotation(getHeadRotation(), new Rotations(45, 0, 0)));
                        animation.registerRotationPoints(BODY_ROTATION, new PercentageRotation(getBodyRotation(), new Rotations(0, 0, 0)));
                        animation.registerRotationPoints(RIGHT_JOINT_ROTATION, new PercentageRotation(getRightJointRotation(), new Rotations(-25, -45, -75)));
                        animation.registerRotationPoints(RIGHT_ARM_ROTATION, new PercentageRotation(getRightArmRotation(), new Rotations(-45, 0, 0)));

                        animation.registerRotationPoints(LEFT_JOINT_ROTATION, new PercentageRotation(getLeftJointRotation(), new Rotations(-25, 45, 75)));
                        animation.registerRotationPoints(LEFT_ARM_ROTATION, new PercentageRotation(getLeftArmRotation(), new Rotations(-45, 0, 0)));

                        animation.registerRotationPoints(RIGHT_LEG_ROTATION, new PercentageRotation(getRightLegRotation(), new Rotations(-65, 0, 0)));
                        animation.registerRotationPoints(LEFT_LEG_ROTATION, new PercentageRotation(getLeftLegRotation(), new Rotations(-65, 0, 0)));

                        animation.registerRotationPoints(RIGHT_FOOT_ROTATION, new PercentageRotation(getRightFootRotation(), new Rotations(95, 0, 0)));
                        animation.registerRotationPoints(LEFT_FOOT_ROTATION, new PercentageRotation(getLeftFootRotation(), new Rotations(95, 0, 0)));
                    } else if (getAbsorbTimer() < absorbTimerMax)
                    {
                        setAnimationSpeed(0.07F);

                        animation.registerRotationPoints(HEAD_ROTATION, new PercentageRotation(getLeftArmRotation(), new Rotations(-45, 0, 0)));
                        animation.registerRotationPoints(BODY_ROTATION, new PercentageRotation(getBodyRotation(), new Rotations(0, 0, 0)));
                        animation.registerRotationPoints(RIGHT_JOINT_ROTATION, new PercentageRotation(getRightJointRotation(), new Rotations(60, 0, 0)));
                        animation.registerRotationPoints(RIGHT_ARM_ROTATION, new PercentageRotation(getRightArmRotation(), new Rotations(0, -105, 60)));

                        animation.registerRotationPoints(LEFT_JOINT_ROTATION, new PercentageRotation(getLeftJointRotation(), new Rotations(60, 0, 0)));
                        animation.registerRotationPoints(LEFT_ARM_ROTATION, new PercentageRotation(getLeftArmRotation(), new Rotations(0, 105, -60)));

                        animation.registerRotationPoints(RIGHT_LEG_ROTATION, new PercentageRotation(getRightLegRotation(), new Rotations(0, -25, 30)));
                        animation.registerRotationPoints(LEFT_LEG_ROTATION, new PercentageRotation(getLeftLegRotation(), new Rotations(0, 25, -30)));

                        animation.registerRotationPoints(RIGHT_FOOT_ROTATION, new PercentageRotation(getRightFootRotation(), new Rotations(25, 0, 0)));
                        animation.registerRotationPoints(LEFT_FOOT_ROTATION, new PercentageRotation(getLeftFootRotation(), new Rotations(25, 0, 0)));
                    }
                }
                break;

                case 2:
                {
                    if (getSlamTimer() < slamTimerMax * 0.8F)
                    {
                        setAnimationSpeed(0.05F);

                        animation.registerRotationPoints(HEAD_ROTATION, new PercentageRotation(getHeadRotation(), new Rotations(-40, 0, 0)));
                        animation.registerRotationPoints(BODY_ROTATION, new PercentageRotation(getBodyRotation(), new Rotations(-25, 0, 0)));

                        animation.registerRotationPoints(RIGHT_JOINT_ROTATION, new PercentageRotation(getRightJointRotation(), new Rotations(-75, 0, 0)));
                        animation.registerRotationPoints(RIGHT_ARM_ROTATION, new PercentageRotation(getRightArmRotation(), new Rotations(-140, 0, 30)));

                        animation.registerRotationPoints(LEFT_JOINT_ROTATION, new PercentageRotation(getLeftJointRotation(), new Rotations(-75, 0, 0)));
                        animation.registerRotationPoints(LEFT_ARM_ROTATION, new PercentageRotation(getLeftArmRotation(), new Rotations(-140, 0, -30)));

                        animation.registerRotationPoints(RIGHT_LEG_ROTATION, new PercentageRotation(getRightLegRotation(), new Rotations(-15, 20, 0)));
                        animation.registerRotationPoints(LEFT_LEG_ROTATION, new PercentageRotation(getLeftLegRotation(), new Rotations(-15, -20, 0)));
                        animation.registerRotationPoints(RIGHT_FOOT_ROTATION, new PercentageRotation(getRightFootRotation(), new Rotations(15, 0, 0)));
                        animation.registerRotationPoints(LEFT_FOOT_ROTATION, new PercentageRotation(getLeftFootRotation(), new Rotations(15, 0, 0)));
                    } else if (getSlamTimer() < slamTimerMax)
                    {
                        setAnimationSpeed(0.09F);

                        animation.registerRotationPoints(HEAD_ROTATION, new PercentageRotation(getHeadRotation(), new Rotations(-55, 0, 0)));
                        animation.registerRotationPoints(BODY_ROTATION, new PercentageRotation(getBodyRotation(), new Rotations(85, 0, 0)));

                        animation.registerRotationPoints(RIGHT_ARM_ROTATION, new PercentageRotation(getRightArmRotation(), new Rotations(-55, -30, -5)));
                        animation.registerRotationPoints(RIGHT_JOINT_ROTATION, new PercentageRotation(getRightJointRotation(), new Rotations(-85, 0, 20)));
                        animation.registerRotationPoints(LEFT_ARM_ROTATION, new PercentageRotation(getLeftArmRotation(), new Rotations(-55, 30, 5)));
                        animation.registerRotationPoints(LEFT_JOINT_ROTATION, new PercentageRotation(getLeftJointRotation(), new Rotations(-85, 0, -20)));

                        animation.registerRotationPoints(RIGHT_LEG_ROTATION, new PercentageRotation(getRightLegRotation(), new Rotations(-15, 20, 0)));
                        animation.registerRotationPoints(LEFT_LEG_ROTATION, new PercentageRotation(getLeftLegRotation(), new Rotations(-15, -20, 0)));
                        animation.registerRotationPoints(RIGHT_FOOT_ROTATION, new PercentageRotation(getRightFootRotation(), new Rotations(15, 0, 0)));
                        animation.registerRotationPoints(LEFT_FOOT_ROTATION, new PercentageRotation(getLeftFootRotation(), new Rotations(15, 0, 0)));
                    }
                }
                break;
            }
        }
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn)
    {
        this.playSound(this.getStepSound(), 1.15F, 0.1F);
    }

    protected SoundEvent getStepSound()
    {
        return SoundEvents.BLOCK_STONE_BREAK;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR;
    }

    @Override
    public int getMaxSpawnedInChunk()
    {
        return 1;
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
            if (source.getTrueSource() instanceof DendroidEntity || source.getTrueSource() instanceof DendroidElderEntity)
                return false;

            if (source.damageType.equals(DamageSource.ON_FIRE.damageType))
            {
                this.setFire(100);
            } else if (source.damageType.equals(DamageSource.DROWN.damageType) || source.damageType.equals(DamageSource.CACTUS.damageType))
            {
                return false;
            } else if (source.getTrueSource() instanceof LivingEntity && ((LivingEntity) source.getTrueSource()).getHeldItemMainhand().getItem() instanceof AxeItem)
            {
                return super.attackEntityFrom(source, amount * 2.0F);
            }

            if (getState() == 0 && source.getTrueSource() != null && !(source.getImmediateSource() instanceof LivingEntity))
            {
                Vector3d dir = this.getPositionVec().subtract(source.getTrueSource().getPositionVec());

                if (this.isDefensiveMode())
                {
                    if (MathHelper.signum(this.getLookVec().getX()) != MathHelper.signum(dir.getX()) && MathHelper.signum(this.getLookVec().getZ()) != MathHelper.signum(dir.getZ()))
                    {
                        this.playSound(SoundEvents.ENTITY_ARMOR_STAND_HIT, this.getSoundVolume(), this.getSoundPitch());
                        return false;
                    }
                } else
                {
                    if (this.world.rand.nextInt(100) < 40)
                    {
                        this.setDefendMode(true);
                        this.defendTimer = 100 + rand.nextInt(50);
                    }
                }
            }

            if (getState() == 1)
            {
                if (source.isProjectile() || source.getImmediateSource() instanceof LivingEntity)
                {
                    Vector3d dir = this.getPositionVec().subtract(source.getImmediateSource().getPositionVec());

                    if (MathHelper.signum(this.getLookVec().getX()) != MathHelper.signum(dir.getX()) && MathHelper.signum(this.getLookVec().getZ()) != MathHelper.signum(dir.getZ()))
                    {
                        this.playSound(SoundEvents.ENTITY_ARMOR_STAND_HIT, this.getSoundVolume(), this.getSoundPitch());
                        return false;
                    }
                }
            }

            return super.attackEntityFrom(source, amount);
        }
    }

    @Override
    public void remove()
    {
        int radius = 5;

        if (!this.world.isRemote)
        {
            for (float y = -radius; y < radius; ++y)
            {
                for (float x = -radius; x < radius; ++x)
                {
                    for (float z = -radius; z < radius; ++z)
                    {
                        BlockPos blockPos = new BlockPos(x + this.getPosX(), y + this.getPosY(), z + this.getPosZ());

                        if (world.rand.nextInt(100) < 40)
                        {
                            if (this.world.getBlockState(blockPos).getBlock() instanceof SpreadableSnowyDirtBlock)
                            {
                                if (!this.world.getBlockState(new BlockPos(blockPos.getX(), blockPos.getY() + 1, blockPos.getZ())).isSolid())
                                {
                                    this.world.setBlockState(new BlockPos(blockPos.getX(), blockPos.getY() + 1, blockPos.getZ()), ItemBlockRegistryHandler.DENDROID_ROOTS_BLOCK.get().getDefaultState());
                                }
                            }
                        }
                    }
                }
            }
            super.remove();
        }
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn)
    {
        this.setAttackTimer(attackTimerMax);
        setAttackingArm((isDefensiveMode() || rand.nextInt(2) == 1));
        this.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 0.1F);

        return super.attackEntityAsMob(entityIn);
    }

    private class ElderAttackGoal extends MWAWMeleeAttackGoal
    {
        DendroidElderEntity host;
        LivingEntity livingentity;

        public ElderAttackGoal(DendroidElderEntity creature, double speedIn, double runningSpeedIn, boolean useLongMemory)
        {
            super(creature, speedIn, runningSpeedIn, useLongMemory);
            host = creature;
        }

        @Override
        public boolean shouldExecute()
        {
            return super.shouldExecute() && host.getState() == 0;
        }

        @Override
        public void startExecuting()
        {
            super.startExecuting();
        }

        @Override
        public void resetTask()
        {
            super.resetTask();
        }

        @Override
        public void tick()
        {
            super.tick();
            livingentity = this.attacker.getAttackTarget();
            double dist = this.attacker.getDistanceSq(livingentity.getPosX(), livingentity.getPosY(), livingentity.getPosZ());
        }
    }

    private class ElderRecoveryGoal extends Goal
    {
        private final float radius;
        private final ArrayList<BlockPos> natureBlocks;
        DendroidElderEntity host;
        private float coolDown;

        public ElderRecoveryGoal(DendroidElderEntity creature)
        {
            host = creature;
            coolDown = 0;
            radius = 5;
            natureBlocks = new ArrayList<>();
            this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        }

        @Override
        public boolean shouldExecute()
        {
            if (host.ticksExisted % 2 == 0)
            {
                --coolDown;
            }

            if (coolDown > 0)
            {
                return false;
            }
            return host.getState() == 0 && host.getHealth() < host.getMaxHealth() * 0.3F && rand.nextInt(100) < 40;
        }

        @Override
        public boolean shouldContinueExecuting()
        {
            return host.getAbsorbTimer() <= host.getAbsorbTimerMax();
        }

        @Override
        public void startExecuting()
        {
            natureBlocks.clear();
            host.getNavigator().clearPath();
            host.setAbsorbTimer(0.0F);
            host.setState(1);

            for (float y = -radius; y < radius; ++y)
            {
                for (float x = -radius; x < radius; ++x)
                {
                    for (float z = -radius; z < radius; ++z)
                    {
                        BlockPos blockPos = new BlockPos(x + host.getPosX(), y + host.getPosY(), z + host.getPosZ());

                        if (host.world.getBlockState(blockPos).getBlock() instanceof IPlantable || host.world.getBlockState(blockPos).getBlock() instanceof LeavesBlock ||
                                host.world.getBlockState(blockPos).getBlock() instanceof IGrowable)
                        {
                            natureBlocks.add(blockPos);
                        }
                    }
                }
            }
        }

        @Override
        public void resetTask()
        {
            coolDown = 1000;
            host.setState(0);
        }

        @Override
        public void tick()
        {
            if (host.getAbsorbTimer() <= host.getAbsorbTimerMax())
            {
                host.setAbsorbTimer(host.getAbsorbTimer() + 1.0F);
                host.setMotion(Vector3d.ZERO);

                if (!host.world.isRemote)
                {
                    if (host.getAbsorbTimer() < host.getAbsorbTimerMax() * 0.90F)
                    {
                        if (host.ticksExisted % 2 == 0)
                        {
                            if (!natureBlocks.isEmpty())
                            {
                                int randomBlock = host.world.rand.nextInt(natureBlocks.size());
                                BlockPos chosenBlock = natureBlocks.remove(randomBlock);

                                if (host.world.getBlockState(chosenBlock).getBlock() instanceof IGrowable && !(host.world.getBlockState(chosenBlock).getBlock() instanceof IPlantable))
                                {
                                    host.world.setBlockState(chosenBlock, Blocks.COARSE_DIRT.getDefaultState());
                                } else
                                {
                                    host.world.setBlockState(chosenBlock, Blocks.AIR.getDefaultState());
                                }

                                SpawnParticleMessage spawnParticleMessage = new SpawnParticleMessage(new Vector3d(chosenBlock.getX() + 0.5F, chosenBlock.getY() + 1.0F, chosenBlock.getZ() + 0.5F), new Vector3d(0.05D, -1.05D, 0.05D), 10, 8, 0.5F);
                                MWAWPacketHandler.packetHandler.sendToDimension(host.world.func_234923_W_(), spawnParticleMessage);

                                host.playSound(SoundEvents.BLOCK_GRASS_BREAK, 0.3F, 0.1F);

                                SpawnSuckingParticleMessage spawnSuckingParticleMessage = new SpawnSuckingParticleMessage(new Vector3d(host.getPosX(), host.getPosY() + (host.getHeight() * 0.5F) + 1.0F, host.getPosZ()), new Vector3d(0.05D, 0.05D, 0.05D), 3, 1, 0.5F);
                                MWAWPacketHandler.packetHandler.sendToDimension(host.world.func_234923_W_(), spawnSuckingParticleMessage);
                                host.heal(5);
                            } else
                            {
                                host.playSound(SoundEvents.ENTITY_EVOKER_PREPARE_SUMMON, 0.3F, 0.1F);
                            }
                        }
                    } else
                    {
                        for (int i = 1; i <= 180; ++i)
                        {
                            double yaw = i * 360 / 180;
                            double speed = 0.4;
                            double xSpeed = speed * Math.cos(Math.toRadians(yaw));
                            double zSpeed = speed * Math.sin(Math.toRadians(yaw));

                            SpawnParticleMessage spawnParticleMessage = new SpawnParticleMessage(new Vector3d(host.getPosX(), host.getPosY() + (host.getHeight() * 0.5F) + 1.0F, host.getPosZ()), new Vector3d(xSpeed, 0.0D, zSpeed), 1, 7, 0.0F);
                            MWAWPacketHandler.packetHandler.sendToDimension(host.world.func_234923_W_(), spawnParticleMessage);
                        }

                        List<LivingEntity> entities = EntityHelper.getEntityLivingBaseNearby(host, 10, 3, 10, 10);
                        for (LivingEntity entity : entities)
                        {
                            if (!(entity instanceof PlayerEntity && ((PlayerEntity) entity).isCreative()) && !(entity instanceof DendroidElderEntity) && !(entity instanceof DendroidEntity))
                            {
                                double angle = (EntityHelper.getAngleBetweenEntities(host, entity) + 90) * Math.PI / 180;
                                double distance = getDistance(entity);
                                entity.setMotion(
                                        entity.getMotion().getX() + Math.min(1 / (distance * distance), 1) * -4 * Math.cos(angle),
                                        entity.getMotion().getY(),
                                        entity.getMotion().getZ() + Math.min(1 / (distance * distance), 1) * -4 * Math.sin(angle));

                                if (entity instanceof PlayerEntity)
                                {
                                    entity.velocityChanged = true;
                                }

                                int i = 0;
                                if (host.world.getDifficulty() == Difficulty.NORMAL)
                                {
                                    i = 5;
                                } else if (host.world.getDifficulty() == Difficulty.HARD)
                                {
                                    i = 10;
                                }

                                entity.addPotionEffect(new EffectInstance(Effects.POISON, 20 * i, 1));
                                entity.addPotionEffect(new EffectInstance(Effects.NAUSEA, 20 * i, 1));
                            }
                        }

                        host.playSound(SoundEvents.ENTITY_EVOKER_CAST_SPELL, 5.0F, 0.1F);

                        if (host.getAbsorbTimer() == host.getAbsorbTimerMax())
                        {
                            if (natureBlocks.size() > 0)
                            {
                                for (BlockPos pos : natureBlocks)
                                {
                                    if (host.world.getBlockState(pos).getBlock() instanceof IGrowable && !(host.world.getBlockState(pos).getBlock() instanceof IPlantable))
                                    {
                                        host.world.setBlockState(new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ()), ItemBlockRegistryHandler.DENDROID_ROOTS_BLOCK.get().getDefaultState());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private class ElderGroundSlamGoal extends Goal
    {
        DendroidElderEntity host;
        private int coolDown;

        public ElderGroundSlamGoal(DendroidElderEntity creature)
        {
            host = creature;
            coolDown = 0;

            this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        }

        @Override
        public boolean shouldExecute()
        {
            if (host.ticksExisted % 2 == 0)
            {
                --coolDown;
            }

            if (coolDown > 0)
            {
                return false;
            }

            return host.getAttackTarget() != null && host.getDistance(host.getAttackTarget()) < 10 && host.getState() == 0 && rand.nextInt(100) < 40;
        }

        @Override
        public boolean shouldContinueExecuting()
        {
            return host.getSlamTimer() <= host.getSlamTimerMax();
        }

        @Override
        public void startExecuting()
        {
            host.getNavigator().clearPath();
            host.setSlamTimer(0.0F);
            host.setState(2);
        }

        @Override
        public void resetTask()
        {
            coolDown = 100;
            host.setState(0);
        }

        @Override
        public void tick()
        {
            if (host.getSlamTimer() <= host.getSlamTimerMax())
            {
                host.setSlamTimer(host.getSlamTimer() + 1.0F);

                if (!host.world.isRemote)
                {
                    if (host.getSlamTimer() < host.getSlamTimerMax() * 0.1F)
                    {
                        host.playSound(SoundEvents.ENTITY_ILLUSIONER_PREPARE_MIRROR, 5.0F, 0.1F);
                        double d2 = host.getAttackTarget().getPosX() - host.getPosX();
                        double d1 = host.getAttackTarget().getPosZ() - host.getPosZ();
                        host.rotationYaw = -((float) MathHelper.atan2(d2, d1)) * (180F / (float) Math.PI);
                        host.renderYawOffset = host.rotationYaw;

                    } else if (host.getSlamTimer() >= host.getSlamTimerMax() * 0.9F)
                    {
                        List<LivingEntity> entities = EntityHelper.getEntityLivingBaseNearby(host, 30, 2, 30, 50);

                        host.playSound(SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 5.0F, 0.1F);
                        float wide = 0;
                        Vector3d forward = new Vector3d(MathFuncHelper.signum(host.getLookVec().getX()), 1, MathFuncHelper.signum(host.getLookVec().getZ()));
                        Vector3d right = MathFuncHelper.crossProduct(forward, new Vector3d(0, 1, 0));
                        for (int dist = 0; dist < 10; ++dist)
                        {
                            for (int i = -(int) wide; i <= (int) wide; ++i)
                            {
                                BlockPos pos = new BlockPos(host.getPosX() + (forward.getX() * dist) + (right.getX() * i), host.getPosY() - 1, host.getPosZ() + (forward.getZ() * dist) + (right.getZ() * i));
                                FallingBlockEntity fallingblockentity = new FallingBlockEntity(host.world, (double) pos.getX() + 0.5D, pos.getY(), (double) pos.getZ() + 0.5D, host.world.getBlockState(pos));
                                fallingblockentity.setMotion(0, dist * 0.1F, 0);
                                host.world.addEntity(fallingblockentity);

                                for (LivingEntity entity : entities)
                                {
                                    if (!(entity instanceof PlayerEntity && ((PlayerEntity) entity).isCreative()) && !(entity instanceof DendroidElderEntity))
                                    {
                                        if (entity.getPosX() > pos.getX() - 1 && entity.getPosX() < pos.getX() + 1 &&
                                                entity.getPosZ() > pos.getZ() - 1 && entity.getPosZ() < pos.getZ() + 1)
                                        {
                                            entity.setMotion(entity.getMotion().getX(), entity.getMotion().getY() + (dist * 0.02F), entity.getMotion().getZ());
                                            entity.attackEntityFrom(new DamageSource("slam"), (dist - 10) * 0.5F);
                                            entity.velocityChanged = true;
                                        }
                                    }
                                }
                            }
                            wide += 0.5F;
                        }
                    }
                }
            }
        }
    }
}