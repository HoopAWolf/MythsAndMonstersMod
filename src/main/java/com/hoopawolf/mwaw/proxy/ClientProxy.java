package com.hoopawolf.mwaw.proxy;

import com.hoopawolf.mwaw.util.EntityRegistryHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientProxy
{
    @SubscribeEvent
    public static void onClientSetUp(final FMLClientSetupEvent event)
    {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> EntityRegistryHandler::registerEntityRenderer);
    }
}