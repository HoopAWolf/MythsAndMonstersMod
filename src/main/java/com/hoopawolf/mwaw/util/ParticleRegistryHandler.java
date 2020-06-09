package com.hoopawolf.mwaw.util;

import com.hoopawolf.mwaw.client.particles.GreenEnchantmentSuckingParticle;
import com.hoopawolf.mwaw.client.particles.GreenFlameParticle;
import com.hoopawolf.mwaw.client.particles.YellowEnchantmentOrbitingParticle;
import com.hoopawolf.mwaw.client.particles.YellowEnchantmentParticle;
import com.hoopawolf.mwaw.ref.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ParticleRegistryHandler
{
    public static final DeferredRegister<ParticleType<?>> PARTICLES = new DeferredRegister<>(ForgeRegistries.PARTICLE_TYPES, Reference.MOD_ID);

    //PARTICLES
    public static final RegistryObject<BasicParticleType> YELLOW_ORBITING_ENCHANTMENT_PARTICLE = PARTICLES.register("yelloworbitingenchantparticle", () -> new BasicParticleType(false));
    public static final RegistryObject<BasicParticleType> GREEN_SUCKING_ENCHANTMENT_PARTICLE = PARTICLES.register("yellowsuckingenchantparticle", () -> new BasicParticleType(false));
    public static final RegistryObject<BasicParticleType> GREEN_FLAME_PARTICLE = PARTICLES.register("greenflameparticle", () -> new BasicParticleType(false));
    public static final RegistryObject<BasicParticleType> YELLOW_ENCHANTMENT_PARTICLE = PARTICLES.register("yellowenchantmentparticle", () -> new BasicParticleType(false));

    @SubscribeEvent
    public static void registerFactories(ParticleFactoryRegisterEvent event)
    {
        ParticleManager particles = Minecraft.getInstance().particles;

        particles.registerFactory(YELLOW_ORBITING_ENCHANTMENT_PARTICLE.get(), YellowEnchantmentOrbitingParticle.Factory::new);
        particles.registerFactory(GREEN_SUCKING_ENCHANTMENT_PARTICLE.get(), GreenEnchantmentSuckingParticle.Factory::new);
        particles.registerFactory(GREEN_FLAME_PARTICLE.get(), GreenFlameParticle.Factory::new);
        particles.registerFactory(YELLOW_ENCHANTMENT_PARTICLE.get(), YellowEnchantmentParticle.Factory::new);
    }
}
