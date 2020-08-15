package com.hoopawolf.mwaw.entities.ai;

import com.hoopawolf.mwaw.entities.HunterEntity;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.BowItem;
import net.minecraft.item.Items;
import net.minecraft.item.ShootableItem;

import java.util.EnumSet;

public class RangedBowAttackHunterGoal<T extends CreatureEntity & IRangedAttackMob> extends Goal
{
    private final T entity;
    private final double moveSpeedAmp;
    private final float maxAttackDistance;
    private final int attackCooldown;
    private int attackTime = -1;
    private int seeTime;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;

    public RangedBowAttackHunterGoal(T mob, double moveSpeedAmpIn, int attackCooldownIn, float maxAttackDistanceIn)
    {
        this.entity = mob;
        this.moveSpeedAmp = moveSpeedAmpIn;
        this.attackCooldown = attackCooldownIn;
        this.maxAttackDistance = maxAttackDistanceIn * maxAttackDistanceIn;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean shouldExecute()
    {
        return this.entity.getAttackTarget() != null && this.isBowInMainhand();
    }

    protected boolean isBowInMainhand()
    {
        return this.entity.getHeldItemMainhand().getItem() instanceof ShootableItem || this.entity.getHeldItemOffhand().getItem() instanceof ShootableItem;
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        return (this.shouldExecute() || !this.entity.getNavigator().noPath()) && this.isBowInMainhand();
    }

    @Override
    public void startExecuting()
    {
        super.startExecuting();
        this.entity.setAggroed(true);
    }

    @Override
    public void resetTask()
    {
        super.resetTask();
        this.entity.setAggroed(false);
        this.seeTime = 0;
        this.attackTime = -1;
        this.entity.resetActiveHand();
        this.entity.getNavigator().clearPath();
        entity.setSneaking(false);
    }

    @Override
    public void tick()
    {
        LivingEntity livingentity = this.entity.getAttackTarget();
        if (livingentity != null)
        {
            if (livingentity instanceof MobEntity)
            {
                entity.setSneaking(((MobEntity) livingentity).getAttackTarget() == null || !(((MobEntity) livingentity).getAttackTarget() instanceof HunterEntity));
            }

            double d0 = this.entity.getDistanceSq(livingentity.getPosX(), livingentity.getPosY(), livingentity.getPosZ());
            boolean flag = this.entity.getEntitySenses().canSee(livingentity);
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
                this.entity.getNavigator().clearPath();
                ++this.strafingTime;
            } else
            {
                this.entity.getNavigator().tryMoveToEntityLiving(livingentity, entity.isSneaking() ? this.moveSpeedAmp * 0.3D : this.moveSpeedAmp);
                this.strafingTime = -1;
            }

            if (this.strafingTime >= 20)
            {
                if ((double) this.entity.getRNG().nextFloat() < 0.3D)
                {
                    this.strafingClockwise = !this.strafingClockwise;
                }

                if ((double) this.entity.getRNG().nextFloat() < 0.3D)
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

                this.entity.getMoveHelper().strafe(this.strafingBackwards ? -(float) (entity.isSneaking() ? this.moveSpeedAmp * 0.3D : this.moveSpeedAmp) : (float) (entity.isSneaking() ? this.moveSpeedAmp * 0.2D : this.moveSpeedAmp), this.strafingClockwise ? (float) (entity.isSneaking() ? this.moveSpeedAmp * 0.3D : this.moveSpeedAmp) : -(float) (entity.isSneaking() ? this.moveSpeedAmp * 0.3D : this.moveSpeedAmp));
                this.entity.faceEntity(livingentity, 30.0F, 30.0F);
            } else
            {
                this.entity.getLookController().setLookPositionWithEntity(livingentity, 30.0F, 30.0F);
            }

            if (this.entity.isHandActive())
            {
                if (!flag && this.seeTime < -60)
                {
                    this.entity.resetActiveHand();
                } else if (flag)
                {
                    int i = this.entity.getItemInUseMaxCount();
                    if (i >= 20)
                    {
                        this.entity.resetActiveHand();
                        this.entity.attackEntityWithRangedAttack(livingentity, BowItem.getArrowVelocity(i));
                        this.attackTime = this.attackCooldown;
                    }
                }
            } else if (--this.attackTime <= 0 && this.seeTime >= -60)
            {
                this.entity.setActiveHand(ProjectileHelper.getHandWith(this.entity, Items.BOW));
            }

        }
    }
}