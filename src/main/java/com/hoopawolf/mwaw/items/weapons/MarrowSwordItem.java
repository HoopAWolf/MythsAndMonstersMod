package com.hoopawolf.mwaw.items.weapons;

import com.hoopawolf.mwaw.tab.MWAWItemGroup;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class MarrowSwordItem extends SwordItem
{
    public MarrowSwordItem(IItemTier tier, int attackDamageIn, float attackSpeedIn, Properties builder)
    {
        super(tier, attackDamageIn, attackSpeedIn, builder.group(MWAWItemGroup.instance));

    }

    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
    {
        if (isSelected)
        {
            if (entityIn instanceof PlayerEntity)
            {
                PlayerEntity _playerEntity = (PlayerEntity) entityIn;

                if (_playerEntity.getHealth() != _playerEntity.getMaxHealth() && _playerEntity.getFoodStats().getFoodLevel() > 1)
                {
                    if (!worldIn.isRemote())
                    {
                        _playerEntity.heal(1f);
                        _playerEntity.getFoodStats().setFoodLevel(_playerEntity.getFoodStats().getFoodLevel() - 1);
                    } else
                    {
                        worldIn.playSound(_playerEntity.getPosX(), _playerEntity.getPosY() + 2, _playerEntity.getPosZ(), SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.NEUTRAL, 1, 100.0f, false);
                    }
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        tooltip.add(new TranslationTextComponent("\u00A77\u00A7o" + I18n.format("tooltip.mwaw:marrowinfo")));
    }

}
