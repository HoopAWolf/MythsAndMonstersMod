package com.hoopawolf.mwaw.client.animation;

import com.hoopawolf.mwaw.entities.helper.MathFuncHelper;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.Rotations;

import java.util.HashMap;
import java.util.LinkedList;

public class AnimationHelper
{
    public HashMap<DataParameter<Rotations>, LinkedList<PercentageRotation>> animation = new HashMap<>();

    public void registerData(DataParameter<Rotations> data)
    {
        animation.put(data, new LinkedList<>());
    }

    public void registerRotationPoints(DataParameter<Rotations> data, PercentageRotation rotations)
    {
        if (animation.get(data) != null)
        {
            if (!animation.get(data).isEmpty())
            {
                if (!animation.get(data).getLast().getEndRotation().equals(rotations.getEndRotation()) && animation.get(data).size() < 2)
                {
                    animation.get(data).add(rotations);
                }
            } else
            {
                animation.get(data).add(rotations);
            }
        }
    }

    public void animationTick(EntityDataManager dataManager, float animationSpeed)
    {
        if (!animation.isEmpty())
        {
            for (DataParameter<Rotations> data : animation.keySet())
            {
                if (!animation.get(data).isEmpty())
                {
                    PercentageRotation stored_rotation = null;

                    while (!animation.get(data).isEmpty())
                    {
                        PercentageRotation temp = animation.get(data).getFirst();

                        if (dataManager.get(data).equals(temp.getEndRotation()) || temp.getPercentage() >= 1)
                        {
                            animation.get(data).removeFirst();
                        } else
                        {
                            stored_rotation = temp;
                            break;
                        }
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
}
