package com.hoopawolf.mwaw.util;

import com.hoopawolf.mwaw.entities.HunterEntity;
import com.hoopawolf.mwaw.entities.KitsuneEntity;
import com.hoopawolf.mwaw.entities.helper.EntityHelper;
import com.hoopawolf.mwaw.ref.Reference;
import com.hoopawolf.mwaw.tab.MWAWItemGroup;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.PillagerEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class MWAWEventHandler
{
    @SubscribeEvent
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

    @SubscribeEvent
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

    @SubscribeEvent
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

    @SubscribeEvent
    public static void onAttackedEvent(LivingDamageEvent event)
    {
        LivingEntity entity = event.getEntityLiving();
        World world = entity.world;

        if (!world.isRemote)
        {
            if (entity instanceof FoxEntity)
            {
                List<KitsuneEntity> ent_list = EntityHelper.INSTANCE.getEntitiesNearby(entity, KitsuneEntity.class, 16.0D);

                for (KitsuneEntity kit : ent_list)
                {
                    if (kit.getAttackTarget() == null)
                    {
                        kit.setAttackTarget((LivingEntity) event.getSource().getTrueSource());
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onJoinWorld(EntityJoinWorldEvent event)
    {
        Entity entity = event.getEntity();

        World world = entity.world;

        if (!world.isRemote)
        {
            if (entity instanceof ZombieEntity)
            {
                ((ZombieEntity) entity).targetSelector.addGoal(2, new NearestAttackableTargetGoal<>((ZombieEntity) entity, HunterEntity.class, true));
            }

            if (entity instanceof PillagerEntity)
            {
                ((PillagerEntity) entity).targetSelector.addGoal(2, new NearestAttackableTargetGoal<>((PillagerEntity) entity, HunterEntity.class, true));
            }
        }
    }
}
