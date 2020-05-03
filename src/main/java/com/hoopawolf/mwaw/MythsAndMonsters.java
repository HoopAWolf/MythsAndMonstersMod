package com.hoopawolf.mwaw;

import com.hoopawolf.mwaw.items.MWAWSpawnEggItem;
import com.hoopawolf.mwaw.network.MWAWPacketHandler;
import com.hoopawolf.mwaw.ref.Reference;
import com.hoopawolf.mwaw.util.EntityRegistryHandler;
import com.hoopawolf.mwaw.util.ParticleRegistryHandler;
import com.hoopawolf.mwaw.util.RegistryHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Reference.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class MythsAndMonsters
{
    public MythsAndMonsters()
    {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        RegistryHandler.init(modEventBus);
        EntityRegistryHandler.ENTITIES.register(modEventBus);
        ParticleRegistryHandler.PARTICLES.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public static void onCommonSetupEvent(FMLCommonSetupEvent event)
    {
        MWAWPacketHandler.init();
        MWAWSpawnEggItem.initUnaddedEggs();
        DeferredWorkQueue.runLater(EntityRegistryHandler::generateEntityWorldSpawn);
        DeferredWorkQueue.runLater(RegistryHandler::generateBlockWorldSpawn);
    }

    @SubscribeEvent
    public static void onClientSetUp(final FMLClientSetupEvent event)
    {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> EntityRegistryHandler::registerEntityRenderer);
    }
}