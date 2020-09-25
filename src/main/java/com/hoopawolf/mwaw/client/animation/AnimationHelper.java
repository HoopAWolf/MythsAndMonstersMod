package com.hoopawolf.mwaw.client.animation;

import com.hoopawolf.mwaw.entities.helper.MathFuncHelper;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.Rotations;

import java.util.HashMap;

public class AnimationHelper
{
    public HashMap<DataParameter<Rotations>, PercentageRotation> animation = new HashMap<>();

    public void registerData(DataParameter<Rotations> data)
    {
        animation.put(data, null);
    }

    public void registerRotationPoints(DataParameter<Rotations> data, PercentageRotation rotations)
    {
        if (animation.get(data) != null)
        {
            if (!animation.get(data).getEndRotation().equals(rotations.getEndRotation()))
            {
                animation.put(data, rotations);
            }
        } else
        {
            animation.put(data, rotations);
        }
    }

    public void animationTick(EntityDataManager dataManager, float animationSpeed)
    {
        if (!animation.isEmpty())
        {
            for (DataParameter<Rotations> data : animation.keySet())
            {
                if (animation.get(data) != null)
                {
                    PercentageRotation stored_rotation = animation.get(data);

                    if (dataManager.get(data).equals(stored_rotation.getEndRotation()) || stored_rotation.getPercentage() >= 1)
                    {
                        animation.put(data, null);
                        stored_rotation = null;
                    }

                    if (stored_rotation != null)
                    {
                        dataManager.set(data, MathFuncHelper.lerp(stored_rotation.getStartingRotation(), stored_rotation.getEndRotation(), stored_rotation.getPercentage()));
                        stored_rotation.increasePercentage(animationSpeed);
                    }
                }
            }
        }
    }

    public boolean isAnimationDone(DataParameter<Rotations> data, EntityDataManager dataManager)
    {
        return atDefaultRotation(dataManager.get(data).getX(), dataManager.get(data).getY(), dataManager.get(data).getZ());
    }

    public boolean atDefaultRotation(double x, double y, double z)
    {
        return x == 0 && y == 0 && z == 0;
    }

    public void resetRotation(EntityDataManager dataManager, DataParameter<Rotations> data)
    {
        dataManager.set(data, new Rotations(0, 0, 0));
    }
}
