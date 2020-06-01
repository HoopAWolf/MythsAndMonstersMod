package com.hoopawolf.mwaw.entities.ai;

import com.hoopawolf.mwaw.entities.HunterEntity;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.player.PlayerEntity;

public class LookAtCustomerHunterGoal extends LookAtGoal
{
    private final HunterEntity hunter;

    public LookAtCustomerHunterGoal(HunterEntity hunterEntity)
    {
        super(hunterEntity, PlayerEntity.class, 8.0F);
        this.hunter = hunterEntity;
    }

    @Override
    public boolean shouldExecute()
    {
        if (this.hunter.hasCustomer() && this.hunter.getAttackTarget() == null)
        {
            this.closestEntity = this.hunter.getCustomer();
            return true;
        } else
        {
            return false;
        }
    }
}