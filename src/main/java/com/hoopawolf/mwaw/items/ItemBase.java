package com.hoopawolf.mwaw.items;

import com.hoopawolf.mwaw.tab.MWAWItemGroup;
import net.minecraft.item.Item;

public class ItemBase extends Item
{
    public ItemBase(Item.Properties _prop)
    {
        super(_prop.group(MWAWItemGroup.instance));
    }
}
