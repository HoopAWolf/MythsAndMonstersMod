package com.hoopawolf.mwaw.util;

import com.hoopawolf.mwaw.potion.PotionEffectBase;
import com.hoopawolf.mwaw.ref.Reference;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.Potion;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class PotionRegistryHandler
{
    public static final DeferredRegister<Potion> POTION = new DeferredRegister<>(ForgeRegistries.POTION_TYPES, Reference.MOD_ID);
    public static final DeferredRegister<Effect> POTION_EFFECT = new DeferredRegister<>(ForgeRegistries.POTIONS, Reference.MOD_ID);

    //EFFECTS
    public static final RegistryObject<Effect> CLAY_SLOW_EFFECT = POTION_EFFECT.register("claysloweffect", () -> new PotionEffectBase(EffectType.HARMFUL, 0x9da4a6)
            .addAttributesModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160890", -0.10F, AttributeModifier.Operation.MULTIPLY_TOTAL));

    //POTION
    public static final RegistryObject<Potion> CLAY_SLOW_POTION = POTION.register("clayslowpotion", () -> new Potion(new EffectInstance(CLAY_SLOW_EFFECT.get(), 100)));

    public static void init(IEventBus _iEventBus)
    {
        POTION_EFFECT.register(_iEventBus);
        POTION.register(_iEventBus);
    }

}
