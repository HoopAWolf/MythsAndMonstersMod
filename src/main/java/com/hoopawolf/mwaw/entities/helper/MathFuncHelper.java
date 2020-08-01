package com.hoopawolf.mwaw.entities.helper;

import net.minecraft.util.math.Vec3d;

public class MathFuncHelper
{
    public static Vec3d Lerp(Vec3d start, Vec3d end, float percent)
    {
        return (start.add(end.subtract(start).mul(percent, percent, percent)));
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
