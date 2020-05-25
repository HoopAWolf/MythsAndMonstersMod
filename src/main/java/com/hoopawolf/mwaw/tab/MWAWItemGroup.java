package com.hoopawolf.mwaw.tab;

import com.hoopawolf.mwaw.util.RegistryHandler;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class MWAWItemGroup extends ItemGroup
{
    public static final MWAWItemGroup instance = new MWAWItemGroup(ItemGroup.GROUPS.length, "mwawItemGroup");

    public MWAWItemGroup(int index, String label)
    {
        super(index, label);
    }

    @Override
    public ItemStack createIcon()
    {
        return new ItemStack(RegistryHandler.GOLDEN_BOW.get());
    }
}