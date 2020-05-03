package com.hoopawolf.mwaw.items.weapons;

import com.hoopawolf.mwaw.tab.MWAWItemGroup;
import net.minecraft.item.IItemTier;
import net.minecraft.item.SwordItem;

public class BoneDaggerItem extends SwordItem
{
    public BoneDaggerItem(IItemTier tier, int attackDamageIn, float attackSpeedIn, Properties builder)
    {
        super(tier, attackDamageIn, attackSpeedIn, builder.group(MWAWItemGroup.instance));

    }
}