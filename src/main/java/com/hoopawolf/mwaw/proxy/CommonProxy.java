package com.hoopawolf.mwaw.proxy;

import com.hoopawolf.mwaw.items.MWAWSpawnEggItem;
import com.hoopawolf.mwaw.network.MWAWPacketHandler;
import com.hoopawolf.mwaw.ref.Reference;
import com.hoopawolf.mwaw.util.RegistryHandler;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonProxy
{
    @SubscribeEvent
    public static void onCommonSetupEvent(FMLCommonSetupEvent event)
    {
        MWAWPacketHandler.init();
        MWAWSpawnEggItem.initUnaddedEggs();
        DeferredWorkQueue.runLater(RegistryHandler::generateEntityWorldSpawn);
        DeferredWorkQueue.runLater(RegistryHandler::generateBlockWorldSpawn);
    }
}