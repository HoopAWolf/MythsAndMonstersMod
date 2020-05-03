package com.hoopawolf.mwaw.proxy;

import com.hoopawolf.mwaw.client.particles.GreenEnchantmentSuckingParticle;
import com.hoopawolf.mwaw.client.particles.GreenFlameParticle;
import com.hoopawolf.mwaw.client.particles.YellowEnchantmentOrbitingParticle;
import com.hoopawolf.mwaw.entities.projectiles.SapEntity;
import com.hoopawolf.mwaw.entities.renderer.*;
import com.hoopawolf.mwaw.entities.renderer.projectiles.FoxHeadRenderer;
import com.hoopawolf.mwaw.entities.renderer.projectiles.GoldenArrowRenderer;
import com.hoopawolf.mwaw.ref.Reference;
import com.hoopawolf.mwaw.util.RegistryHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;


@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientProxy implements IProxy
{
    @Override
    public World getClientWorld()
    {
        return Minecraft.getInstance().world;
    }

    @Override
    public PlayerEntity getClientPlayer()
    {
        return Minecraft.getInstance().player;
    }

    @SubscribeEvent
    public static void onClientSetUp(final FMLClientSetupEvent event)
    {
        RenderingRegistry.registerEntityRenderingHandler(RegistryHandler.FAIRY_ENTITY.get(), FairyRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(RegistryHandler.SAND_WYRM_ENTITY.get(), SandWyrmRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(RegistryHandler.DENDROID_ENTITY.get(), DendroidRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(RegistryHandler.WOLPERTINGER_ENTITY.get(), WolpertingerRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(RegistryHandler.KITSUNE_ENTITY.get(), KitsuneRenderer::new);

        RenderingRegistry.registerEntityRenderingHandler(RegistryHandler.GOLDEN_ARROW_ENTITY.get(), GoldenArrowRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(RegistryHandler.SAP_ENTITY.get(), m -> new SpriteRenderer<SapEntity>(m, Minecraft.getInstance().getItemRenderer()));
        RenderingRegistry.registerEntityRenderingHandler(RegistryHandler.FOX_HEAD_ENTITY.get(), FoxHeadRenderer::new);

        RenderTypeLookup.setRenderLayer(RegistryHandler.FAIRY_MUSHROOM_BLOCK.get(), RenderType.getCutout());
    }

    @SubscribeEvent
    public static void registerFactories(ParticleFactoryRegisterEvent event)
    {
        ParticleManager particles = Minecraft.getInstance().particles;

        particles.registerFactory(RegistryHandler.YELLOW_ORBITING_ENCHANTMENT_PARTICLE.get(), YellowEnchantmentOrbitingParticle.Factory::new);
        particles.registerFactory(RegistryHandler.GREEN_SUCKING_ENCHANTMENT_PARTICLE.get(), GreenEnchantmentSuckingParticle.Factory::new);
        particles.registerFactory(RegistryHandler.GREEN_FLAME_PARTICLE.get(), GreenFlameParticle.Factory::new);
    }
}