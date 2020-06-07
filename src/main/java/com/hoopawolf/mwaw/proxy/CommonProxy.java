package com.hoopawolf.mwaw.proxy;

import com.hoopawolf.mwaw.items.MWAWSpawnEggItem;
import com.hoopawolf.mwaw.network.MWAWPacketHandler;
import com.hoopawolf.mwaw.util.EntityRegistryHandler;
import com.hoopawolf.mwaw.util.ItemBlockRegistryHandler;
import com.hoopawolf.mwaw.util.StructureRegistryHandler;
import net.minecraft.world.gen.feature.Feature;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.registries.IForgeRegistry;


@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonProxy
{
    @SubscribeEvent
    public static void onCommonSetupEvent(FMLCommonSetupEvent event)
    {
        MWAWPacketHandler.init();
        MWAWSpawnEggItem.initUnaddedEggs();
        DeferredWorkQueue.runLater(EntityRegistryHandler::generateEntityWorldSpawn);
        DeferredWorkQueue.runLater(ItemBlockRegistryHandler::generateBlockWorldSpawn);
    }

    @SubscribeEvent
    public static void onLoadComplete(FMLLoadCompleteEvent event)
    {
        DeferredWorkQueue.runLater(StructureRegistryHandler::generateStructureWorldSpawn);
    }

    @SubscribeEvent
    public static void registerFeaturesEvent(RegistryEvent.Register<Feature<?>> event)
    {
        IForgeRegistry<Feature<?>> registry = event.getRegistry();

        registry.register(StructureRegistryHandler.HUNTER_CAMP.setRegistryName("huntercamp"));
        StructureRegistryHandler.registerFeature();
    }
}