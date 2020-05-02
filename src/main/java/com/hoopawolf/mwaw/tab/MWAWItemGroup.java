package com.hoopawolf.mwaw.tab;

import com.hoopawolf.mwaw.ref.Reference;
import com.hoopawolf.mwaw.util.RegistryHandler;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MWAWItemGroup extends ItemGroup
{
    public static final MWAWItemGroup instance = new MWAWItemGroup(ItemGroup.GROUPS.length, "mwawItemGroup");
    private static final ResourceLocation CREATIVE_INVENTORY_TABS = new ResourceLocation(Reference.MOD_ID, "textures/gui/container/creative_inventory/tab_elementalwitchmod.png");

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

    @OnlyIn(Dist.CLIENT)
    public ResourceLocation getBackgroundImage()
    {
        return CREATIVE_INVENTORY_TABS;
    }
}