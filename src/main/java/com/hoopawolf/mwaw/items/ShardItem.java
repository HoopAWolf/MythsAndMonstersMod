package com.hoopawolf.mwaw.items;

import com.hoopawolf.mwaw.tab.MWAWItemGroup;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class ShardItem extends Item
{
    public ShardItem()
    {
        super(new Item.Properties().maxStackSize(16).group(MWAWItemGroup.instance));

//        this.addPropertyOverride(new ResourceLocation("element"), new IItemPropertyGetter()
//        {
//
//            public float call(ItemStack stack, @Nullable World worldIn, @Nullable LivingEntity entityIn)
//            {
//                return (float) getDamage(stack);
//            }
//        });
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        String _idWithoutTag = this.getRegistryName().toString().substring(5);

        String _string = "tooltip.mwaw:" + _idWithoutTag;

        tooltip.add(new TranslationTextComponent(I18n.format("tooltip.mwaw:shard") + " " + I18n.format(_string)).setStyle(new Style().setItalic(true).setColor(TextFormatting.LIGHT_PURPLE)));

    }
}
