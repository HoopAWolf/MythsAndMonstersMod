package com.hoopawolf.mwaw.entities.helper;

import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Rotations;

import java.util.HashMap;
import java.util.LinkedList;

public class AnimationHelper
{
    public HashMap<DataParameter<Rotations>, LinkedList<Rotations>> animation = new HashMap<>();

    public void registerData(DataParameter<Rotations> data)
    {
        animation.put(data, new LinkedList<>());
    }

    public void registerRotationPoints(DataParameter<Rotations> data, Rotations endRotation)
    {
        if (animation.get(data) != null)
        {
            if (!animation.get(data).isEmpty())
            {
                if (!animation.get(data).getLast().equals(endRotation) && animation.get(data).size() < 2)
                {
                    animation.get(data).add(endRotation);
                }
            } else
            {
                animation.get(data).add(endRotation);
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
                    float tempX, tempY, tempZ;
                    Rotations stored_rotation = null;

                    while (!animation.get(data).isEmpty())
                    {
                        Rotations temp = animation.get(data).getFirst();

                        if (dataManager.get(data).equals(temp))
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
                        Rotations current_rotation = dataManager.get(data);

                        if (!current_rotation.equals(stored_rotation))
                        {
                            tempX = current_rotation.getX() + ((current_rotation.getX() != stored_rotation.getX()) ? ((current_rotation.getX() > stored_rotation.getX()) ? -animationSpeed : animationSpeed) : 0);
                            tempY = current_rotation.getY() + ((current_rotation.getY() != stored_rotation.getY()) ? ((current_rotation.getY() > stored_rotation.getY()) ? -animationSpeed : animationSpeed) : 0);
                            tempZ = current_rotation.getZ() + ((current_rotation.getZ() != stored_rotation.getZ()) ? ((current_rotation.getZ() > stored_rotation.getZ()) ? -animationSpeed : animationSpeed) : 0);

                            tempX = ((current_rotation.getX() > stored_rotation.getX()) ? MathHelper.clamp(tempX, stored_rotation.getX(), tempX) : MathHelper.clamp(tempX, tempX, stored_rotation.getX()));
                            tempY = ((current_rotation.getY() > stored_rotation.getY()) ? MathHelper.clamp(tempY, stored_rotation.getY(), tempY) : MathHelper.clamp(tempY, tempY, stored_rotation.getY()));
                            tempZ = ((current_rotation.getZ() > stored_rotation.getZ()) ? MathHelper.clamp(tempZ, stored_rotation.getZ(), tempZ) : MathHelper.clamp(tempZ, tempZ, stored_rotation.getZ()));

                            dataManager.set(data, new Rotations(tempX, tempY, tempZ));
                        }
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
