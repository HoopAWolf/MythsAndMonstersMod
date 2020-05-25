package com.hoopawolf.mwaw.items.weapons;

import com.hoopawolf.mwaw.entities.projectiles.GoldenArrowEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.world.World;

public class GoldenArrowItem extends ArrowItem
{
    public GoldenArrowItem(Properties builder)
    {
        super(builder);
    }

    @Override
    public AbstractArrowEntity createArrow(World worldIn, ItemStack stack, LivingEntity shooter)
    {
        return new GoldenArrowEntity(worldIn, shooter);
    }

    @Override
    public Rarity getRarity(ItemStack stack)
    {
        return Rarity.UNCOMMON;
    }
}