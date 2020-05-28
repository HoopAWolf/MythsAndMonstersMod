package com.hoopawolf.mwaw.entities.helper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;
import java.util.stream.Collectors;

public class EntityHelper
{
    public static EntityHelper INSTANCE = new EntityHelper();

    public double getAngleBetweenEntities(Entity first, Entity second)
    {
        return Math.atan2(second.getPosZ() - first.getPosZ(), second.getPosX() - first.getPosX()) * (180 / Math.PI) + 90;
    }

    public List<PlayerEntity> getPlayersNearby(Entity ent, double distanceX, double distanceY, double distanceZ, double radius)
    {
        List<Entity> nearbyEntities = ent.world.getEntitiesWithinAABBExcludingEntity(ent, ent.getBoundingBox().grow(distanceX, distanceY, distanceZ));
        List<PlayerEntity> listEntityPlayers = nearbyEntities.stream().filter(entityNeighbor -> entityNeighbor instanceof PlayerEntity && ent.getDistance(entityNeighbor) <= radius).map(entityNeighbor -> (PlayerEntity) entityNeighbor).collect(Collectors.toList());
        return listEntityPlayers;
    }

    public List<LivingEntity> getAttackableEntityLivingBaseNearby(Entity ent, double distanceX, double distanceY, double distanceZ, double radius)
    {
        List<Entity> nearbyEntities = ent.world.getEntitiesWithinAABBExcludingEntity(ent, ent.getBoundingBox().grow(distanceX, distanceY, distanceZ));
        List<LivingEntity> listEntityLivingBase = nearbyEntities.stream().filter(entityNeighbor -> entityNeighbor instanceof LivingEntity && ((LivingEntity) entityNeighbor).attackable() && (!(entityNeighbor instanceof PlayerEntity) || !((PlayerEntity) entityNeighbor).isCreative()) && ent.getDistance(entityNeighbor) <= radius).map(entityNeighbor -> (LivingEntity) entityNeighbor).collect(Collectors.toList());
        return listEntityLivingBase;
    }

    public List<LivingEntity> getEntityLivingBaseNearby(Entity ent, double distanceX, double distanceY, double distanceZ, double radius)
    {
        return getEntitiesNearby(ent, LivingEntity.class, distanceX, distanceY, distanceZ, radius);
    }

    public <T extends Entity> List<T> getEntitiesNearby(Entity ent, Class<T> entityClass, double r)
    {
        return ent.world.getEntitiesWithinAABB(entityClass, ent.getBoundingBox().grow(r, r, r), e -> e != ent && ent.getDistance(e) <= r);
    }

    public <T extends Entity> List<T> getEntitiesNearby(Entity ent, Class<T> entityClass, double dX, double dY, double dZ, double r)
    {
        return ent.world.getEntitiesWithinAABB(entityClass, ent.getBoundingBox().grow(dX, dY, dZ), e -> e != ent && ent.getDistance(e) <= r && e.getPosY() <= ent.getPosY() + dY);
    }
}
