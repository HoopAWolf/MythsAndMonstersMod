package com.hoopawolf.mwaw.items;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class ShardItem extends ItemBase
{
    public ShardItem()
    {
        super(new Item.Properties().maxStackSize(16));

//        this.addPropertyOverride(new ResourceLocation("element"), new IItemPropertyGetter()
//        {
//            @OnlyIn(Dist.CLIENT)
//            public float call(ItemStack stack, @Nullable World worldIn, @Nullable LivingEntity entityIn)
//            {
//                return (float) getDamage(stack);
//            }
//        });
    }

    public Rarity getRarity(ItemStack stack)
    {
        return Rarity.EPIC;
    }

    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        String _idWithoutTag = this.getRegistryName().toString().substring(5);

        String _string = "tooltip.mwaw:" + _idWithoutTag;

        tooltip.add(new TranslationTextComponent("\u00A77\u00A7o" + I18n.format("tooltip.mwaw:shard") + " " + I18n.format(_string)));

    }
}
