package com.hoopawolf.mwaw.entities.ai;

import com.hoopawolf.mwaw.entities.DendroidEntity;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.MathHelper;

import java.util.EnumSet;

public class RangedAttackWithStrafeGoal extends Goal
{
    private final MobEntity entityHost;
    private final IRangedAttackMob rangedAttackEntityHost;
    private LivingEntity attackTarget;
    private int rangedAttackTime = -1;
    private final double entityMoveSpeed;
    private int seeTime;
    private final int attackIntervalMin;
    private final int maxRangedAttackTime;
    private final float attackRadius;
    private final float maxAttackDistance;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;

    public RangedAttackWithStrafeGoal(IRangedAttackMob attacker, double movespeed, int maxAttackTime, float maxAttackDistanceIn)
    {
        this(attacker, movespeed, maxAttackTime, maxAttackTime, maxAttackDistanceIn);
    }

    public RangedAttackWithStrafeGoal(IRangedAttackMob attacker, double movespeed, int minAttackTime, int maxAttackTime, float maxAttackDistanceIn)
    {
        if (!(attacker instanceof LivingEntity))
        {
            throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
        } else
        {
            this.rangedAttackEntityHost = attacker;
            this.entityHost = (MobEntity) attacker;
            this.entityMoveSpeed = movespeed;
            this.attackIntervalMin = minAttackTime;
            this.maxRangedAttackTime = maxAttackTime;
            this.attackRadius = maxAttackDistanceIn;
            this.maxAttackDistance = maxAttackDistanceIn * maxAttackDistanceIn;
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean shouldExecute()
    {
        LivingEntity livingentity = this.entityHost.getAttackTarget();
        if (livingentity != null && livingentity.isAlive())
        {
            attackTarget = livingentity;
            return true;
        } else
        {
            return false;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting()
    {
        return (this.shouldExecute() || !this.entityHost.getNavigator().noPath());
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask()
    {
        this.attackTarget = null;
        this.seeTime = 0;
        this.rangedAttackTime = -1;
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick()
    {
        if (attackTarget != null)
        {
            double d0 = this.entityHost.getDistanceSq(attackTarget.getPosX(), attackTarget.getPosY(), attackTarget.getPosZ());
            boolean flag = this.entityHost.getEntitySenses().canSee(attackTarget);
            boolean flag1 = this.seeTime > 0;
            if (flag != flag1)
            {
                this.seeTime = 0;
            }

            if (flag)
            {
                ++this.seeTime;
            } else
            {
                --this.seeTime;
            }

            if (!(d0 > (double) this.maxAttackDistance) && this.seeTime >= 20)
            {
                this.entityHost.getNavigator().clearPath();
                ++this.strafingTime;
            } else
            {
                this.entityHost.getNavigator().tryMoveToEntityLiving(attackTarget, this.entityMoveSpeed);
                this.strafingTime = -1;
            }

            if (this.strafingTime >= 20)
            {
                if ((double) this.entityHost.getRNG().nextFloat() < 0.3D)
                {
                    this.strafingClockwise = !this.strafingClockwise;
                }

                if ((double) this.entityHost.getRNG().nextFloat() < 0.3D)
                {
                    this.strafingBackwards = !this.strafingBackwards;
                }

                this.strafingTime = 0;
            }

            if (this.strafingTime > -1)
            {
                if (d0 > (double) (this.maxAttackDistance * 0.75F))
                {
                    this.strafingBackwards = false;
                } else if (d0 < (double) (this.maxAttackDistance * 0.25F))
                {
                    this.strafingBackwards = true;
                }

                this.entityHost.getMoveHelper().strafe(this.strafingBackwards ? -0.5F : 0.5F, this.strafingClockwise ? 0.5F : -0.5F);
                this.entityHost.faceEntity(attackTarget, 30.0F, 30.0F);
            } else
            {
                this.entityHost.getLookController().setLookPositionWithEntity(attackTarget, 30.0F, 30.0F);
            }

            if (--this.rangedAttackTime <= 0)
            {
                if (flag)
                {
                    float f = MathHelper.sqrt(d0) / this.attackRadius;
                    float lvt_5_1_ = MathHelper.clamp(f, 0.1F, 1.0F);
                    this.rangedAttackEntityHost.attackEntityWithRangedAttack(this.attackTarget, lvt_5_1_);
                    this.rangedAttackTime = MathHelper.nextInt(entityHost.world.rand, this.attackIntervalMin, this.maxRangedAttackTime);
                    if (this.rangedAttackEntityHost instanceof DendroidEntity)
                    {
                        ((DendroidEntity) this.rangedAttackEntityHost).setIsShooting(true);
                    }
                } else if (this.rangedAttackTime < 0)
                {
                    this.rangedAttackTime = MathHelper.nextInt(entityHost.world.rand, this.attackIntervalMin, this.maxRangedAttackTime);
                }
            }
        }
    }
}