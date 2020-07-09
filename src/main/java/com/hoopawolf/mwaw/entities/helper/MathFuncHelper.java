package com.hoopawolf.mwaw.entities.helper;

import net.minecraft.util.math.Vec3d;

public class MathFuncHelper
{
    public static Vec3d crossProduct(Vec3d vec_A, Vec3d vec_B)
    {
        return new Vec3d(Math.signum(vec_A.getY() * vec_B.getZ() - vec_A.getZ() * vec_B.getY()),
                Math.signum(vec_A.getZ() * vec_B.getX() - vec_A.getX() * vec_B.getZ()),
                Math.signum(vec_A.getX() * vec_B.getY() - vec_A.getY() * vec_B.getX()));
    }
}
