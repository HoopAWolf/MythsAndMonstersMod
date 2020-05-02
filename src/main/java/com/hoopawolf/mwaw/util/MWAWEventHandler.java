package com.hoopawolf.mwaw.util;

import com.hoopawolf.mwaw.ref.Reference;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class MWAWEventHandler
{
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void playerEntityInteractEvent(PlayerInteractEvent.EntityInteract event)
    {
        PlayerEntity player = event.getPlayer();
        Entity entity = event.getTarget();

        if (entity instanceof ChickenEntity)
        {
            if (entity.getRidingEntity() == null)
                entity.startRiding(player);
            else
            {
                if (player.isCrouching())
                    entity.stopRiding();
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onLivingFallEvent(LivingEvent event)
    {
        LivingEntity entity = event.getEntityLiving();

        if (entity instanceof PlayerEntity)
        {
            if (entity.getPassengers().size() > 0 && entity.getPassengers().get(0) instanceof ChickenEntity)
            {
                Vec3d vec3d = entity.getMotion();
                entity.setMotion(vec3d.mul(1.0D, 0.95D, 1.0D));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onLivingHurtEvent(LivingHurtEvent event)
    {
        LivingEntity entity = event.getEntityLiving();
        World world = entity.world;

        if (!world.isRemote)
        {
            if (entity.getPassengers().size() > 0 && entity.getPassengers().get(0) instanceof ChickenEntity)
            {
                if (event.getSource() == DamageSource.FALL)
                {
                    event.setCanceled(true);
                }
            }
        }
    }
}
