package com.hoopawolf.mwaw.items;

import com.hoopawolf.mwaw.util.RegistryHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class BadAppleItem extends Item
{
    public BadAppleItem(Item.Properties _prop)
    {
        super(_prop);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving)
    {
        if (!worldIn.isRemote)
        {
            worldIn.playSound(null, entityLiving.getPosX(), entityLiving.getPosY(), entityLiving.getPosZ(), this.getEatSound(), SoundCategory.NEUTRAL, 1.0F, 1.0F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.4F);

            if (entityLiving instanceof PlayerEntity && !((PlayerEntity) entityLiving).inventory.armorItemInSlot(3).isEmpty() && ((PlayerEntity) entityLiving).inventory.armorItemInSlot(3).getItem() == Items.DIAMOND_HELMET)
            {
                entityLiving.addPotionEffect(new EffectInstance(new EffectInstance(Effects.ABSORPTION, 200, 1)));
                entityLiving.addPotionEffect(new EffectInstance(new EffectInstance(Effects.RESISTANCE, 200, 1)));
                entityLiving.addPotionEffect(new EffectInstance(new EffectInstance(Effects.SPEED, 200, 1)));
                entityLiving.addPotionEffect(new EffectInstance(new EffectInstance(Effects.STRENGTH, 200, 1)));
                entityLiving.addPotionEffect(new EffectInstance(new EffectInstance(Effects.REGENERATION, 100, 1)));
                entityLiving.addPotionEffect(new EffectInstance(new EffectInstance(Effects.SATURATION, 200, 1)));
            } else
            {
                entityLiving.addPotionEffect(new EffectInstance(new EffectInstance(Effects.BLINDNESS, 200, 1)));
                entityLiving.addPotionEffect(new EffectInstance(new EffectInstance(Effects.BAD_OMEN, 200, 1)));
                entityLiving.addPotionEffect(new EffectInstance(new EffectInstance(Effects.SLOWNESS, 200, 1)));
                entityLiving.addPotionEffect(new EffectInstance(new EffectInstance(Effects.WEAKNESS, 200, 1)));
                entityLiving.addPotionEffect(new EffectInstance(new EffectInstance(Effects.WITHER, 100, 1)));
                entityLiving.addPotionEffect(new EffectInstance(new EffectInstance(Effects.HUNGER, 200, 1)));
            }

            if (!(entityLiving instanceof PlayerEntity) || !((PlayerEntity) entityLiving).abilities.isCreativeMode)
            {
                stack.shrink(1);
            }

            if (worldIn.rand.nextInt(100) < 50)
                worldIn.addEntity(new ItemEntity(worldIn, entityLiving.getPosX(), entityLiving.getPosY(), entityLiving.getPosZ(), RegistryHandler.TAINTED_SEED.get().getDefaultInstance()));
        }

        return stack;
    }
}
