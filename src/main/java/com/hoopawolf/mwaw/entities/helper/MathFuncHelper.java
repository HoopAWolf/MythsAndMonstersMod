package com.hoopawolf.mwaw.entities.helper;

import net.minecraft.util.math.Rotations;
import net.minecraft.util.math.vector.Vector3d;

public class MathFuncHelper
{
    public static Vector3d lerp(Vector3d start, Vector3d end, float percent)
    {
        return (start.add(end.subtract(start).mul(percent, percent, percent)));
    }

    public static Rotations lerp(Rotations start, Rotations end, float percent)
    {
        Vector3d startingVec = new Vector3d(start.getX(), start.getY(), start.getZ());
        Vector3d endingVec = new Vector3d(end.getX(), end.getY(), end.getZ());

        Vector3d result = lerp(startingVec, endingVec, percent);

        return new Rotations((float) result.getX(), (float) result.getY(), (float) result.getZ());
    }

    public static Vector3d crossProduct(Vector3d vec_A, Vector3d vec_B)
    {
        return new Vector3d(Math.signum((int) (vec_A.getY() * vec_B.getZ() - vec_A.getZ() * vec_B.getY())),
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
