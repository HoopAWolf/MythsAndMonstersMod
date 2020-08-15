package com.hoopawolf.mwaw.util;

import com.hoopawolf.mwaw.client.particles.*;
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
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Reference.MOD_ID);

    //PARTICLES
    public static final RegistryObject<BasicParticleType> YELLOW_ORBITING_ENCHANTMENT_PARTICLE = PARTICLES.register("yelloworbitingenchantparticle", () -> new BasicParticleType(false));
    public static final RegistryObject<BasicParticleType> GREEN_SUCKING_ENCHANTMENT_PARTICLE = PARTICLES.register("yellowsuckingenchantparticle", () -> new BasicParticleType(false));
    public static final RegistryObject<BasicParticleType> NATURE_AURA_SUCKING_PARTICLE = PARTICLES.register("natureaurasuckingparticle", () -> new BasicParticleType(false));
    public static final RegistryObject<BasicParticleType> GREEN_FLAME_PARTICLE = PARTICLES.register("greenflameparticle", () -> new BasicParticleType(false));
    public static final RegistryObject<BasicParticleType> NATURE_AURA_PARTICLE = PARTICLES.register("natureauraparticle", () -> new BasicParticleType(false));
    public static final RegistryObject<BasicParticleType> YELLOW_ENCHANTMENT_PARTICLE = PARTICLES.register("yellowenchantmentparticle", () -> new BasicParticleType(false));
    public static final RegistryObject<BasicParticleType> FIRE_PARTICLE = PARTICLES.register("fireparticle", () -> new BasicParticleType(true));
    public static final RegistryObject<BasicParticleType> FIRE_SUCKING_PARTICLE = PARTICLES.register("firesuckingparticle", () -> new BasicParticleType(true));

    @SubscribeEvent
    public static void registerFactories(ParticleFactoryRegisterEvent event)
    {
        ParticleManager particles = Minecraft.getInstance().particles;

        particles.registerFactory(YELLOW_ORBITING_ENCHANTMENT_PARTICLE.get(), YellowEnchantmentOrbitingParticle.Factory::new);
        particles.registerFactory(GREEN_SUCKING_ENCHANTMENT_PARTICLE.get(), GreenEnchantmentSuckingParticle.Factory::new);
        particles.registerFactory(GREEN_FLAME_PARTICLE.get(), GreenFlameParticle.Factory::new);
        particles.registerFactory(NATURE_AURA_PARTICLE.get(), NatureAuraParticle.Factory::new);
        particles.registerFactory(YELLOW_ENCHANTMENT_PARTICLE.get(), YellowEnchantmentParticle.Factory::new);
        particles.registerFactory(NATURE_AURA_SUCKING_PARTICLE.get(), NatureAuraSuckingParticle.Factory::new);
        particles.registerFactory(FIRE_PARTICLE.get(), FireParticle.FireSmokeFactory::new);
        particles.registerFactory(FIRE_SUCKING_PARTICLE.get(), FireSuckingParticle.Factory::new);
    }
}
