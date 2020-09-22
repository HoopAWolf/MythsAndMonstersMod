package com.hoopawolf.mwaw.entities;

import com.hoopawolf.mwaw.entities.ai.MWAWMeleeAttackGoal;
import com.hoopawolf.mwaw.entities.ai.controller.MWAWMovementController;
import com.hoopawolf.mwaw.entities.ai.navigation.MWAWPathNavigateGround;
import com.hoopawolf.mwaw.entities.helper.EntityHelper;
import com.hoopawolf.mwaw.network.MWAWPacketHandler;
import com.hoopawolf.mwaw.network.packets.client.SpawnParticleMessage;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public class JackalopeEntity extends CreatureEntity
{
    private static final DataParameter<Boolean> ANGRY = EntityDataManager.createKey(JackalopeEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Float> TIME_REARING = EntityDataManager.createKey(JackalopeEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> PREV_TIME_REARING = EntityDataManager.createKey(JackalopeEntity.class, DataSerializers.FLOAT);

    private int sheepTimer,
            ramingCoolDown;
    private boolean isRamming;

    public JackalopeEntity(EntityType<? extends JackalopeEntity> type, World worldIn)
    {
        super(type, worldIn);
        ramingCoolDown = 0;
        this.stepHeight = 1.0F;
        this.moveController = new MWAWMovementController(this, 30);
    }

    public static AttributeModifierMap.MutableAttribute func_234321_m_()
    {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 70.0D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 6.0D)
                .createMutableAttribute(Attributes.FOLLOW_RANGE, 12.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3D);
    }

    @Override
    protected void registerGoals()
    {
        this.targetSelector.addGoal(3, (new HurtByTargetGoal(this)).setCallsForHelp());

        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(3, new JackalopeEntity.RammingGoal(this));
        this.goalSelector.addGoal(4, new JackalopeEntity.LeapAtTargetRamGoal(this, 0.4F));
        this.goalSelector.addGoal(5, new JackalopeEntity.MeleeRamGoal(this, 1.0D, true));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new JackalopeEntity.LookAtRamGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(8, new JackalopeEntity.LookAtRandomRamGoal(this));
    }

    @Override
    protected PathNavigator createNavigator(World world)
    {
        return new MWAWPathNavigateGround(this, world);
    }

    @Override
    protected void updateAITasks()
    {
        if (ticksExisted % 2 == 0)
        {
            if (ramingCoolDown > 0)
            {
                --ramingCoolDown;
            }
        }

        super.updateAITasks();
    }

    @Override
    public void livingTick()
    {
        super.livingTick();

        if (this.world.isRemote)
        {
            this.sheepTimer = Math.max(0, this.sheepTimer - 1);
        }

        if (!this.world.isRemote)
        {
            if (this.getAttackTarget() == null && this.isAngry())
            {
                this.setAngry(false);
            } else if (this.getAttackTarget() != null && !this.isAngry())
            {
                this.setAngry(true);
            }

            if (isRamming)
            {
                isJumping = false;
                moveStrafing = 0.0F;
                moveForward = 0.0F;
                navigator.clearPath();
            } else
            {
                if (this.getRearTime() > 0)
                {
                    this.setRearTime(this.getRearTime() + (0.8F * this.getRearTime() * this.getRearTime() * this.getRearTime() - this.getRearTime()) * 0.8F - 0.05F);
                } else if (this.getRearTime() < 0)
                {
                    this.setRearTime(0.0F);
                }
            }

            if (getHealth() <= (getMaxHealth() * 0.5F) && ticksExisted % 5 == 0)
            {
                List<LivingEntity> entities = EntityHelper.getEntityLivingBaseNearby(this, 10, 3, 10, 10);
                for (LivingEntity entity : entities)
                {
                    if (entity instanceof AnimalEntity && ((AnimalEntity) entity).getAttackTarget() == null)
                    {
                        ((AnimalEntity) entity).setAttackTarget(this.getAttackTarget());
                    }
                }
            }
        }
    }

    @Override
    protected void registerData()
    {
        super.registerData();
        this.dataManager.register(ANGRY, false);
        this.dataManager.register(TIME_REARING, 0.0F);
        this.dataManager.register(PREV_TIME_REARING, 0.0F);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handleStatusUpdate(byte id)
    {
        if (id == 10)
        {
            this.sheepTimer = 40;
        } else
        {
            super.handleStatusUpdate(id);
        }
    }

    public boolean isAngry()
    {
        return this.dataManager.get(ANGRY);
    }

    public void setAngry(boolean angry)
    {
        this.dataManager.set(ANGRY, angry);
    }

    public float getPrevRearTime()
    {
        return this.dataManager.get(PREV_TIME_REARING);
    }

    public void setPrevRearTime(float _prevreartime)
    {
        this.dataManager.set(PREV_TIME_REARING, _prevreartime);
    }

    public float getRearTime()
    {
        return this.dataManager.get(TIME_REARING);
    }

    public void setRearTime(float _reartime)
    {
        this.dataManager.set(TIME_REARING, _reartime);
    }

    @OnlyIn(Dist.CLIENT)
    public float getHeadRotationPointY(float p_70894_1_)
    {
        if (this.sheepTimer <= 0)
        {
            return 0.0F;
        } else if (this.sheepTimer >= 4 && this.sheepTimer <= 36)
        {
            return 1.0F;
        } else
        {
            return this.sheepTimer < 4 ? ((float) this.sheepTimer - p_70894_1_) / 4.0F : -((float) (this.sheepTimer - 40) - p_70894_1_) / 4.0F;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public float getHeadRotationAngleX(float p_70890_1_)
    {
        if (this.sheepTimer > 4 && this.sheepTimer <= 36)
        {
            float f = ((float) (this.sheepTimer - 4) - p_70890_1_) / 32.0F;
            return ((float) Math.PI / 5F) + 0.21991149F * MathHelper.sin(f * 28.7F);
        } else
        {
            return this.sheepTimer > 0 ? ((float) Math.PI / 5F) : this.rotationPitch * ((float) Math.PI / 180F);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public float getRearingAmount(float p_110223_1_)
    {
        return MathHelper.lerp(p_110223_1_, this.getPrevRearTime(), this.getRearTime());
    }

    @Override
    public void writeAdditional(CompoundNBT compound)
    {
        super.writeAdditional(compound);
        compound.putBoolean("Angry", this.isAngry());
    }

    @Override
    public void readAdditional(CompoundNBT compound)
    {
        super.readAdditional(compound);
        this.setAngry(compound.getBoolean("Angry"));
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return SoundEvents.ENTITY_SHEEP_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_SHEEP_HURT;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_SHEEP_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn)
    {
        this.playSound(SoundEvents.ENTITY_SHEEP_STEP, 0.15F, 1.0F);
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn)
    {
        return 0.95F * sizeIn.height;
    }

    private class RammingGoal extends Goal
    {
        private final JackalopeEntity entity;
        private boolean isRearing, isRamming;
        private Vector3d motion;
        private int timer;

        public RammingGoal(JackalopeEntity _entity)
        {
            entity = _entity;
        }

        @Override
        public boolean shouldExecute()
        {
            return rand.nextInt(100) < 40 && ramingCoolDown <= 0 && !entity.isRamming
                    && entity.isOnGround() && entity.getAttackTarget() != null;
        }

        @Override
        public boolean shouldContinueExecuting()
        {
            return entity.isRamming && entity.getAttackTarget() != null;
        }

        @Override
        public void startExecuting()
        {
            entity.isRamming = true;
            isRamming = false;
            isRearing = true;
            timer = 0;
        }

        @Override
        public void resetTask()
        {
            entity.setMotion(0.0F, entity.getMotion().getY(), 0.0F);
            entity.isRamming = false;
            entity.ramingCoolDown = 100;
            entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0.0D);
        }

        @Override
        public void tick()
        {
            entity.setPrevRearTime(entity.getRearTime());
            entity.getLookController().setLookPosition(entity.getAttackTarget().getPosX(), entity.getAttackTarget().getPosYEye(), entity.getAttackTarget().getPosZ());
            entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);

            if (isRearing)
            {
                double d2 = entity.getAttackTarget().getPosX() - entity.getPosX();
                double d1 = entity.getAttackTarget().getPosZ() - entity.getPosZ();
                entity.rotationYaw = -((float) MathHelper.atan2(d2, d1)) * (180F / (float) Math.PI);
                entity.renderYawOffset = entity.rotationYaw;

                entity.setRearTime(entity.getRearTime() + (1.0F - entity.getRearTime()) * 0.2F + 0.05F);
                if (entity.getRearTime() > 1.0F)
                {
                    entity.setRearTime(1.0F);
                    isRearing = false;
                }
            } else if (!isRamming)
            {
                entity.setRearTime(entity.getRearTime() + (0.8F * entity.getRearTime() * entity.getRearTime() * entity.getRearTime() - entity.getRearTime()) * 0.8F - 0.05F);
                double d2 = entity.getAttackTarget().getPosX() - entity.getPosX();
                double d1 = entity.getAttackTarget().getPosZ() - entity.getPosZ();
                entity.rotationYaw = -((float) MathHelper.atan2(d2, d1)) * (180F / (float) Math.PI);
                entity.renderYawOffset = entity.rotationYaw;

                if (entity.getRearTime() < 0.0F)
                {
                    entity.setRearTime(0.0F);
                    isRamming = true;

                    if (entity.getAttackTarget() != null)
                    {
                        Vector3d dir = entity.getAttackTarget().getPositionVec().subtract(entity.getPositionVec()).normalize();
                        motion = new Vector3d(dir.x * 1.5F, dir.y, dir.z * 1.5F);

                        for (int i = 1; i <= 180; ++i)
                        {
                            double yaw = i * 360 / 180;
                            double speed = 5.5;
                            double xSpeed = speed * Math.cos(Math.toRadians(yaw));
                            double zSpeed = speed * Math.sin(Math.toRadians(yaw));

                            SpawnParticleMessage spawnParticleMessage = new SpawnParticleMessage(new Vector3d(getPosX(), getPosY() + 0.1F, getPosZ()), new Vector3d(xSpeed, 0.0D, zSpeed), 3, 2, 0.0F);
                            MWAWPacketHandler.packetHandler.sendToDimension(entity.world.func_234923_W_(), spawnParticleMessage);
                        }

                        entity.playSound(SoundEvents.ENTITY_PUFFER_FISH_BLOW_OUT, 3.0F, 0.1F);
                    }
                }
            } else if (isRamming)
            {
                if (entity.ticksExisted % 2 == 0)
                {
                    ++timer;
                }

                entity.setMotion(entity.getMotion().add(motion));

                LivingEntity livingentity = entity.getAttackTarget();
                if (livingentity != null && entity.getBoundingBox().intersects(livingentity.getBoundingBox().grow(1.0D)))
                {
                    livingentity.attackEntityFrom(new DamageSource("goldenram"), (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() * 2);

                    livingentity.setMotion(livingentity.getMotion().add(motion.mul(2.0D, 2.0D, 2.0D)));
                    entity.setMotion(0.0F, entity.getMotion().getY(), 0.0F);
                    entity.isRamming = false;
                    entity.ramingCoolDown = 100;
                    entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0.0D);
                    entity.playSound(SoundEvents.BLOCK_ANVIL_LAND, 1.0F, 0.1F);
                }

                if (!world.isRemote)
                {
                    for (int j = 0; j < 10; ++j)
                    {
                        SpawnParticleMessage spawnParticleMessage = new SpawnParticleMessage(new Vector3d(JackalopeEntity.this.getPosX(), JackalopeEntity.this.getPosY() + JackalopeEntity.this.getEyeHeight(), JackalopeEntity.this.getPosZ()),
                                new Vector3d(0.0f, -0.1f, 0.0f), 4, 6, getWidth());
                        MWAWPacketHandler.packetHandler.sendToDimension(JackalopeEntity.this.world.func_234923_W_(), spawnParticleMessage);
                    }
                }

                if (timer >= 5 || (int) entity.prevPosX == (int) entity.getPosX() && (int) entity.prevPosZ == (int) entity.getPosZ() && entity.collidedHorizontally || !onGround)
                {
                    entity.setMotion(0.0F, entity.getMotion().getY(), 0.0F);
                    entity.isRamming = false;
                    entity.ramingCoolDown = 100;
                    entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0.0D);

                }
            }
        }
    }


    private class LeapAtTargetRamGoal extends LeapAtTargetGoal
    {

        public LeapAtTargetRamGoal(MobEntity leapingEntity, float leapMotionYIn)
        {
            super(leapingEntity, leapMotionYIn);
        }

        @Override
        public boolean shouldExecute()
        {
            return !JackalopeEntity.this.isRamming && super.shouldExecute();
        }
    }

    private class MeleeRamGoal extends MWAWMeleeAttackGoal
    {
        public MeleeRamGoal(CreatureEntity creature, double speedIn, boolean useLongMemory)
        {
            super(creature, speedIn, useLongMemory);
        }

        @Override
        public boolean shouldExecute()
        {
            return !JackalopeEntity.this.isRamming && super.shouldExecute();
        }
    }

    private class LookAtRamGoal extends LookAtGoal
    {
        public LookAtRamGoal(MobEntity entityIn, Class<? extends LivingEntity> watchTargetClass, float maxDistance)
        {
            super(entityIn, watchTargetClass, maxDistance);
        }

        @Override
        public boolean shouldExecute()
        {
            return !JackalopeEntity.this.isRamming && super.shouldExecute();
        }
    }

    private class LookAtRandomRamGoal extends LookRandomlyGoal
    {
        public LookAtRandomRamGoal(MobEntity entitylivingIn)
        {
            super(entitylivingIn);
        }

        @Override
        public boolean shouldExecute()
        {
            return !JackalopeEntity.this.isRamming && super.shouldExecute();
        }
    }
}
