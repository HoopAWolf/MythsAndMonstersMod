package com.hoopawolf.mwaw.items.weapons;

import com.hoopawolf.mwaw.tab.MWAWItemGroup;
import net.minecraft.block.material.Material;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
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

public class DendroidSwordItem extends SwordItem
{
    public DendroidSwordItem(IItemTier tier, int attackDamageIn, float attackSpeedIn, Properties builder)
    {
        super(tier, attackDamageIn, attackSpeedIn, builder.group(MWAWItemGroup.instance));

    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
    {
        if (isSelected && stack.isDamaged())
        {
            if (entityIn instanceof PlayerEntity)
            {
                if (entityIn.ticksExisted % 3 == 0)
                {
                    PlayerEntity _playerEntity = (PlayerEntity) entityIn;

                    if (!_playerEntity.getHeldItemOffhand().isEmpty())
                    {
                        if (_playerEntity.getHeldItemOffhand().getItem() instanceof BlockItem && ((BlockItem) _playerEntity.getHeldItemOffhand().getItem()).getBlock().getDefaultState().getMaterial().equals(Material.WOOD))
                        {
                            if (!worldIn.isRemote())
                            {
                                int _recoveryAmount = 5;

                                stack.setDamage(stack.getDamage() - _recoveryAmount);
                                _playerEntity.inventory.offHandInventory.get(0).setCount(_playerEntity.inventory.offHandInventory.get(0).getCount() - 1);
                            } else
                            {
                                worldIn.playSound(_playerEntity.getPosX(), _playerEntity.getPosY() + 2, _playerEntity.getPosZ(), SoundEvents.BLOCK_BAMBOO_BREAK, SoundCategory.NEUTRAL, 1, 5, false);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        tooltip.add(new TranslationTextComponent("\u00A77\u00A7o" + I18n.format("tooltip.mwaw:dendroidinfo")));
    }

    /*public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn)
    {
        if (playerIn.getHeldItem(handIn).isDamaged() && !playerIn.getHeldItemOffhand().isEmpty())
        {
            for (Item _item : log_list)
            {
                if (playerIn.getHeldItemOffhand().getItem() == _item)
                {
                    if (!worldIn.isRemote())
                    {
                        int _recoveryAmount = 5;

                        playerIn.getHeldItem(handIn).setDamage(playerIn.getHeldItem(handIn).getDamage() - _recoveryAmount);
                        playerIn.inventory.offHandInventory.get(0).setCount(playerIn.inventory.offHandInventory.get(0).getCount() - 1);
                    } else
                    {
                        worldIn.playSound(playerIn.getPosX(), playerIn.getPosY() + 2, playerIn.getPosZ(), SoundEvents.BLOCK_BAMBOO_BREAK, SoundCategory.NEUTRAL, 1, 1, false);
                    }
                    break;
                }
            }
        }

        return ActionResult.resultPass(playerIn.getHeldItem(handIn));
    }*/
}
