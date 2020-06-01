package com.hoopawolf.mwaw.tab;

import com.hoopawolf.mwaw.util.RegistryHandler;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MWAWItemGroup extends ItemGroup
{
    public static final MWAWItemGroup instance = new MWAWItemGroup(ItemGroup.GROUPS.length, "mwawItemGroup");

    public MWAWItemGroup(int index, String label)
    {
        super(index, label);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ItemStack createIcon()
    {
        return new ItemStack(RegistryHandler.GOLDEN_BOW.get());
    }
}