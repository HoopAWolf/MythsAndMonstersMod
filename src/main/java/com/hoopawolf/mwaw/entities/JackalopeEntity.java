package com.hoopawolf.mwaw.entities;

import com.hoopawolf.mwaw.entities.ai.MWAWMeleeAttackGoal;
import com.hoopawolf.mwaw.entities.ai.controller.MWAWMovementController;
import com.hoopawolf.mwaw.entities.ai.navigation.MWAWPathNavigateGround;
import com.hoopawolf.mwaw.network.MWAWPacketHandler;
import com.hoopawolf.mwaw.network.packets.client.SpawnParticleMessage;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.MonsterEntity;
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

public class JackalopeEntity extends CreatureEntity
{
    private static final DataParameter<Boolean> ANGRY = EntityDataManager.createKey(JackalopeEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> ESCAPE = EntityDataManager.createKey(JackalopeEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Float> ESCAPE_TIMER = EntityDataManager.createKey(JackalopeEntity.class, DataSerializers.FLOAT);
    private int ramingCoolDown,
            escapeCoolDown;
    private boolean isRamming;
    private Vector3d attackedPos;

    public JackalopeEntity(EntityType<? extends JackalopeEntity> type, World worldIn)
    {
        super(type, worldIn);
        ramingCoolDown = 0;
        escapeCoolDown = 0;
        this.stepHeight = 1.0F;
        attackedPos = null;
        this.moveController = new MWAWMovementController(this, 30);
    }

    public static AttributeModifierMap.MutableAttribute func_234321_m_()
    {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 70.0D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 1.0D)
                .createMutableAttribute(Attributes.FOLLOW_RANGE, 12.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3D);
    }

    @Override
    protected void registerGoals()
    {
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(0, new AvoidPlayerJackalopeGoal(this, PlayerEntity.class, 10.0F, 1.0D, 1.2D));
        this.goalSelector.addGoal(3, new JackalopeEntity.RammingGoal(this));
        this.goalSelector.addGoal(3, new JackalopeEntity.EscapeGoal(this));
        this.goalSelector.addGoal(4, new JackalopeEntity.LeapAtTargetJackalopeGoal(this, 0.4F));
        this.goalSelector.addGoal(5, new JackalopeEntity.MeleeJackalopeGoal(this, 1.0D, true));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new JackalopeEntity.LookAtJackalopeGoal(this, PlayerEntity.class, 10.0F));
        this.goalSelector.addGoal(8, new JackalopeEntity.LookAtRandomJackalopeGoal(this));
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

            if (escapeCoolDown > 0)
            {
                --escapeCoolDown;
            }
        }

        super.updateAITasks();
    }

    @Override
    public void livingTick()
    {
        super.livingTick();

        if (!this.world.isRemote)
        {
            if (this.getAttackTarget() == null && this.isAngry())
            {
                this.setAngry(false);
            } else if (this.getAttackTarget() != null && !this.isAngry())
            {
                this.setAngry(true);
            }

            if (isRamming || isEscaping())
            {
                isJumping = false;
                moveStrafing = 0.0F;
                moveForward = 0.0F;
                navigator.clearPath();
            }

            if (isEscaping())
            {
                setEscapeTimer(getEscapingTimer() + 0.1F);
            }
        }
    }

    @Override
    protected void registerData()
    {
        super.registerData();
        this.dataManager.register(ANGRY, false);
        this.dataManager.register(ESCAPE, false);
        this.dataManager.register(ESCAPE_TIMER, 0.0F);
    }

    public boolean isAngry()
    {
        return this.dataManager.get(ANGRY);
    }

    public void setAngry(boolean angry)
    {
        this.dataManager.set(ANGRY, angry);
    }

    public boolean isEscaping()
    {
        return this.dataManager.get(ESCAPE);
    }

    public void setEscape(boolean escape)
    {
        this.dataManager.set(ESCAPE, escape);
    }

    public float getEscapingTimer()
    {
        return this.dataManager.get(ESCAPE_TIMER);
    }

    public void setEscapeTimer(float escapeTimerIn)
    {
        this.dataManager.set(ESCAPE_TIMER, escapeTimerIn);
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
        return SoundEvents.ENTITY_RABBIT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_RABBIT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_RABBIT_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn)
    {
        this.playSound(SoundEvents.ENTITY_SHEEP_STEP, 0.15F, 1.0F);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (!world.isRemote)
        {
            if (source.getImmediateSource() != null && !isRamming)
            {
                setEscape(true);
                attackedPos = new Vector3d(source.getImmediateSource().getPosition().getX(), source.getImmediateSource().getPosition().getY(), source.getImmediateSource().getPosition().getZ());

                if (world.rand.nextInt(100) < 50)
                {
                    if (source.getTrueSource() != null)
                    {
                        double d1 = source.getTrueSource().getPosX() - this.getPosX();

                        double d0;
                        for (d0 = source.getTrueSource().getPosZ() - this.getPosZ(); d1 * d1 + d0 * d0 < 1.0E-4D; d0 = (Math.random() - Math.random()) * 0.01D)
                        {
                            d1 = (Math.random() - Math.random()) * 0.01D;
                        }

                        this.attackedAtYaw = (float) (MathHelper.atan2(d0, d1) * (double) (180F / (float) Math.PI) - (double) this.rotationYaw);
                        this.applyKnockback(0.4F, d1, d0);
                    }

                    return false;
                }
            }
        }

        return super.attackEntityFrom(source, amount);

    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn)
    {
        return 0.95F * sizeIn.height;
    }

    private class RammingGoal extends Goal
    {
        private final JackalopeEntity entity;
        private Vector3d motion;
        private int timer;

        public RammingGoal(JackalopeEntity _entity)
        {
            entity = _entity;
        }

        @Override
        public boolean shouldExecute()
        {
            return ramingCoolDown <= 0 && !entity.isRamming
                    && entity.isOnGround() && entity.getAttackTarget() != null && entity.getAttackTarget().getDistance(entity) < 5;
        }

        @Override
        public boolean shouldContinueExecuting()
        {
            return entity.isRamming && entity.getAttackTarget() != null && entity.getAttackTarget().getDistance(entity) < 5;
        }

        @Override
        public void startExecuting()
        {
            entity.isRamming = false;
            entity.setEscape(false);
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
            entity.getLookController().setLookPosition(entity.getAttackTarget().getPosX(), entity.getAttackTarget().getPosYEye(), entity.getAttackTarget().getPosZ());
            entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);

            if (!isRamming)
            {
                double d2 = entity.getAttackTarget().getPosX() - entity.getPosX();
                double d1 = entity.getAttackTarget().getPosZ() - entity.getPosZ();
                entity.rotationYaw = -((float) MathHelper.atan2(d2, d1)) * (180F / (float) Math.PI);
                entity.renderYawOffset = entity.rotationYaw;

                isRamming = true;

                if (entity.getAttackTarget() != null)
                {
                    Vector3d dir = entity.getAttackTarget().getPositionVec().subtract(entity.getPositionVec()).normalize();
                    motion = new Vector3d(dir.x, dir.y, dir.z);

                    entity.playSound(SoundEvents.BLOCK_WOOL_PLACE, 3.0F, 0.1F);
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
                    entity.playSound(SoundEvents.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1.0F, 0.1F);
                }

                if (!world.isRemote)
                {
                    for (int j = 0; j < 10; ++j)
                    {
                        SpawnParticleMessage spawnParticleMessage = new SpawnParticleMessage(new Vector3d(JackalopeEntity.this.getPosX(), JackalopeEntity.this.getPosY() + JackalopeEntity.this.getEyeHeight(), JackalopeEntity.this.getPosZ()),
                                new Vector3d(0.0f, -0.1f, 0.0f), 4, 4, getWidth());
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

    private class EscapeGoal extends Goal
    {
        private final JackalopeEntity entity;
        private Vector3d motion;

        public EscapeGoal(JackalopeEntity _entity)
        {
            entity = _entity;
        }

        @Override
        public boolean shouldExecute()
        {
            return entity.isEscaping() && attackedPos != null;
        }

        @Override
        public boolean shouldContinueExecuting()
        {
            return entity.isEscaping() && entity.getEscapingTimer() < 0.5F;
        }

        @Override
        public void startExecuting()
        {
            double d2 = attackedPos.getX() - entity.getPosX();
            double d1 = attackedPos.getZ() - entity.getPosZ();
            entity.rotationYaw = -((float) MathHelper.atan2(d2, d1)) * (180F / (float) Math.PI);
            entity.renderYawOffset = entity.rotationYaw;

            Vector3d dir = attackedPos.subtract(entity.getPositionVec()).normalize().inverse();
            motion = new Vector3d(dir.x * 0.09F, 0.0D, dir.z * 0.09F);
            entity.playSound(SoundEvents.BLOCK_WOOL_PLACE, 3.0F, 0.1F);
        }

        @Override
        public void resetTask()
        {
            entity.escapeCoolDown = 0;
            attackedPos = null;
            entity.setEscape(false);
            entity.setEscapeTimer(0);
            entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0.0D);
        }

        @Override
        public void tick()
        {
            entity.getLookController().setLookPosition(attackedPos.getX(), attackedPos.getY() + 2, attackedPos.getZ());
            entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);

            double d2 = attackedPos.getX() - entity.getPosX();
            double d1 = attackedPos.getZ() - entity.getPosZ();
            entity.rotationYaw = -((float) MathHelper.atan2(d2, d1)) * (180F / (float) Math.PI);
            entity.renderYawOffset = entity.rotationYaw;

            entity.setMotion(entity.getMotion().add(motion));
        }
    }

    private class LeapAtTargetJackalopeGoal extends LeapAtTargetGoal
    {

        public LeapAtTargetJackalopeGoal(MobEntity leapingEntity, float leapMotionYIn)
        {
            super(leapingEntity, leapMotionYIn);
        }

        @Override
        public boolean shouldExecute()
        {
            return !JackalopeEntity.this.isRamming && super.shouldExecute() && !JackalopeEntity.this.isEscaping();
        }
    }

    private class MeleeJackalopeGoal extends MWAWMeleeAttackGoal
    {
        public MeleeJackalopeGoal(CreatureEntity creature, double speedIn, boolean useLongMemory)
        {
            super(creature, speedIn, useLongMemory);
        }

        @Override
        public boolean shouldExecute()
        {
            return !JackalopeEntity.this.isRamming && super.shouldExecute() && !JackalopeEntity.this.isEscaping();
        }
    }

    private class LookAtJackalopeGoal extends LookAtGoal
    {
        public LookAtJackalopeGoal(MobEntity entityIn, Class<? extends LivingEntity> watchTargetClass, float maxDistance)
        {
            super(entityIn, watchTargetClass, maxDistance);
        }

        @Override
        public boolean shouldExecute()
        {
            return !JackalopeEntity.this.isRamming && super.shouldExecute() && !JackalopeEntity.this.isEscaping();
        }
    }

    private class LookAtRandomJackalopeGoal extends LookRandomlyGoal
    {
        public LookAtRandomJackalopeGoal(MobEntity entitylivingIn)
        {
            super(entitylivingIn);
        }

        @Override
        public boolean shouldExecute()
        {
            return !JackalopeEntity.this.isRamming && super.shouldExecute() && !JackalopeEntity.this.isEscaping();
        }
    }

    private class AvoidPlayerJackalopeGoal extends AvoidEntityGoal
    {

        public AvoidPlayerJackalopeGoal(CreatureEntity entityIn, Class classToAvoidIn, float avoidDistanceIn, double farSpeedIn, double nearSpeedIn)
        {
            super(entityIn, classToAvoidIn, avoidDistanceIn, farSpeedIn, nearSpeedIn);
        }

        @Override
        public boolean shouldExecute()
        {
            return (getAttackTarget() == null || JackalopeEntity.this.getHealth() < 5) && super.shouldExecute();
        }
    }
}
