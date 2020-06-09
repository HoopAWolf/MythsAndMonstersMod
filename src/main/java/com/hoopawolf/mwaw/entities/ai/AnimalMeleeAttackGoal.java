package com.hoopawolf.mwaw.entities.ai;

import com.hoopawolf.mwaw.network.MWAWPacketHandler;
import com.hoopawolf.mwaw.network.packets.client.SpawnParticleMessage;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;

public class AnimalMeleeAttackGoal extends MeleeAttackGoal
{
    private final AnimalEntity parentEntity;
    private final LivingEntity livingentity;
    private final double speed;

    public AnimalMeleeAttackGoal(AnimalEntity p_i225718_2_, double p_i225718_3_, boolean p_i225718_5_)
    {
        super(p_i225718_2_, p_i225718_3_, p_i225718_5_);
        parentEntity = p_i225718_2_;
        livingentity = parentEntity.getAttackTarget();
        speed = p_i225718_3_;
    }

    @Override
    public void tick()
    {
        if (livingentity != null)
        {
            if (parentEntity.ticksExisted % 5 == 0 && !parentEntity.world.isRemote)
            {
                Vec3d _vec = new Vec3d(parentEntity.getPosX() - (double) 0.3F, parentEntity.getPosYHeight(0.5D), parentEntity.getPosZ() + (double) 0.3F);
                SpawnParticleMessage spawnParticleMessage = new SpawnParticleMessage(_vec, new Vec3d(0, 0, 0), 1, 3, parentEntity.getWidth());
                MWAWPacketHandler.packetHandler.sendToDimension(parentEntity.dimension, spawnParticleMessage);
            }

            if (parentEntity.getBoundingBox().intersects(livingentity.getBoundingBox().grow(1.0D)))
            {
                livingentity.attackEntityFrom(new DamageSource("animal"), 2.0F);
            } else
            {
                Vec3d vec3d = livingentity.getEyePosition(1.0F);
                parentEntity.getMoveHelper().setMoveTo(vec3d.x, vec3d.y, vec3d.z, speed);
            }
        }
    }
}