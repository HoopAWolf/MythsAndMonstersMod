package com.hoopawolf.mwaw.entities.ai;

import com.hoopawolf.mwaw.entities.HunterEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;

import java.util.EnumSet;

public class TradeWithPlayerHunterGoal extends Goal
{
    private final HunterEntity hunter;

    public TradeWithPlayerHunterGoal(HunterEntity _hunter)
    {
        this.hunter = _hunter;
        this.setMutexFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
    }

    @Override
    public boolean shouldExecute()
    {
        if (!this.hunter.isAlive())
        {
            return false;
        } else if (this.hunter.isInWater())
        {
            return false;
        } else if (!this.hunter.isOnGround())
        {
            return false;
        } else if (this.hunter.velocityChanged)
        {
            return false;
        } else
        {
            PlayerEntity playerentity = this.hunter.getCustomer();
            if (playerentity == null)
            {
                return false;
            } else if (this.hunter.getDistanceSq(playerentity) > 16.0D)
            {
                return false;
            } else
            {
                return playerentity.openContainer != null;
            }
        }
    }

    @Override
    public void startExecuting()
    {
        this.hunter.getNavigator().clearPath();
    }

    @Override
    public void resetTask()
    {
        this.hunter.setCustomer(null);
    }
}