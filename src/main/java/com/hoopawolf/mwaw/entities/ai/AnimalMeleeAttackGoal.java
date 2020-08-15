package com.hoopawolf.mwaw.entities.ai;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;

public class AnimalMeleeAttackGoal extends MeleeAttackGoal
{
    private final CreatureEntity host;
    private final double damage;
    private final double knockBack;

    public AnimalMeleeAttackGoal(CreatureEntity creature, double speedIn, boolean useLongMemory, double damageIn, double knockBackIn)
    {
        super(creature, speedIn, useLongMemory);
        host = creature;
        damage = damageIn;
        knockBack = knockBackIn;
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr)
    {
        double d0 = this.getAttackReachSqr(enemy);
        if (distToEnemySqr <= d0 && this.func_234041_j_() <= 0)
        {
            this.func_234039_g_();
            this.attacker.swingArm(Hand.MAIN_HAND);

            float f = (float) damage;
            float f1 = (float) knockBack;

            boolean flag = enemy.attackEntityFrom(DamageSource.causeMobDamage(host), f);

            if (flag)
            {
                if (f1 > 0.0F && enemy instanceof LivingEntity)
                {
                    enemy.applyKnockback(f1 * 0.5F, MathHelper.sin(host.rotationYaw * ((float) Math.PI / 180F)), -MathHelper.cos(host.rotationYaw * ((float) Math.PI / 180F)));
                    host.setMotion(host.getMotion().mul(0.6D, 1.0D, 0.6D));
                }

                host.setLastAttackedEntity(enemy);
            }
        }
    }
}
