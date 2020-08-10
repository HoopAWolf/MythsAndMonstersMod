package com.hoopawolf.mwaw.entities.helper;

import net.minecraft.util.math.Rotations;
import net.minecraft.util.math.Vec3d;

public class MathFuncHelper
{
    public static Vec3d lerp(Vec3d start, Vec3d end, float percent)
    {
        return (start.add(end.subtract(start).mul(percent, percent, percent)));
    }

    public static Rotations lerp(Rotations start, Rotations end, float percent)
    {
        Vec3d startingVec = new Vec3d(start.getX(), start.getY(), start.getZ());
        Vec3d endingVec = new Vec3d(end.getX(), end.getY(), end.getZ());

        Vec3d result = lerp(startingVec, endingVec, percent);

        return new Rotations((float) result.getX(), (float) result.getY(), (float) result.getZ());
    }

    public static Vec3d crossProduct(Vec3d vec_A, Vec3d vec_B)
    {
        return new Vec3d(Math.signum((int) (vec_A.getY() * vec_B.getZ() - vec_A.getZ() * vec_B.getY())),
                Math.signum((int) (vec_A.getZ() * vec_B.getX() - vec_A.getX() * vec_B.getZ())),
                Math.signum((int) (vec_A.getX() * vec_B.getY() - vec_A.getY() * vec_B.getX())));
    }

    public static double signum(double d)
    {
        if (d > 0F && d <= 0.5F)
            return 0;
        else if (d > 0.5F)
            return 1;
        else if (d > -0.5F && d < 0F)
            return 0;
        else if (d < -0.5F)
            return -1;

        return 0;
    }
}
