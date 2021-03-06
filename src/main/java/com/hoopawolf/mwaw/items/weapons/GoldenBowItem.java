package com.hoopawolf.mwaw.items.weapons;

import com.hoopawolf.mwaw.network.MWAWPacketHandler;
import com.hoopawolf.mwaw.network.packets.client.SpawnOrbitingParticleMessage;
import com.hoopawolf.mwaw.util.ItemBlockRegistryHandler;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShootableItem;
import net.minecraft.item.UseAction;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.function.Predicate;

public class GoldenBowItem extends ShootableItem
{

    public GoldenBowItem(Item.Properties builder)
    {
        super(builder);
    }

    /**
     * Gets the velocity of the arrow entity from the bow's charge
     */
    public static float getArrowVelocity(int charge)
    {
        float f = (float) charge / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F)
        {
            f = 1.0F;
        }

        return f;
    }

    @Override
    public void onUse(World worldIn, LivingEntity livingEntityIn, ItemStack stack, int count)
    {
        if (!worldIn.isRemote)
        {
            SpawnOrbitingParticleMessage spawnParticleMessage = new SpawnOrbitingParticleMessage(livingEntityIn.getPositionVec(), new Vector3d(0.0D, 0.02D, 0.0D), 1, 0, 0.5F);
            MWAWPacketHandler.packetHandler.sendToDimension(livingEntityIn.world.func_234923_W_(), spawnParticleMessage);
        }
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft)
    {
        if (entityLiving instanceof PlayerEntity)
        {
            PlayerEntity playerentity = (PlayerEntity) entityLiving;
            boolean flag = playerentity.abilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
            ItemStack itemstack = playerentity.findAmmo(stack);

            int i = this.getUseDuration(stack) - timeLeft;
            i = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(stack, worldIn, playerentity, i, !itemstack.isEmpty() || flag);
            if (i < 0) return;

            if (!itemstack.isEmpty() || flag)
            {
                if (itemstack.isEmpty())
                {
                    itemstack = new ItemStack(ItemBlockRegistryHandler.GOLDEN_ARROW.get());
                }

                float f = getArrowVelocity(i);
                if (!((double) f < 0.1D))
                {
                    boolean flag1 = playerentity.abilities.isCreativeMode || (itemstack.getItem() instanceof GoldenArrowItem && ((GoldenArrowItem) itemstack.getItem()).isInfinite(itemstack, stack, playerentity));
                    if (!worldIn.isRemote)
                    {
                        GoldenArrowItem arrowitem = (GoldenArrowItem) (itemstack.getItem() instanceof GoldenArrowItem ? itemstack.getItem() : ItemBlockRegistryHandler.GOLDEN_ARROW.get());
                        AbstractArrowEntity abstractarrowentity = arrowitem.createArrow(worldIn, itemstack, playerentity);
                        abstractarrowentity = customeArrow(abstractarrowentity);
                        abstractarrowentity.func_234612_a_(playerentity, playerentity.rotationPitch, playerentity.rotationYaw, 0.0F, f * 3.0F, 1.0F);
                        if (f == 1.0F)
                        {
                            abstractarrowentity.setIsCritical(true);
                        }

                        int j = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
                        if (j > 0)
                        {
                            abstractarrowentity.setDamage(abstractarrowentity.getDamage() + (double) j * 0.5D + 0.5D);
                        }

                        int k = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);
                        if (k > 0)
                        {
                            abstractarrowentity.setKnockbackStrength(k);
                        }

                        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0)
                        {
                            abstractarrowentity.setFire(100);
                        }

                        stack.damageItem(1, playerentity, (p_220009_1_) ->
                        {
                            p_220009_1_.sendBreakAnimation(playerentity.getActiveHand());
                        });

                        worldIn.addEntity(abstractarrowentity);
                    }

                    worldIn.playSound(null, playerentity.getPosX(), playerentity.getPosY(), playerentity.getPosZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
                    if (!flag1 && !playerentity.abilities.isCreativeMode)
                    {
                        itemstack.shrink(1);
                        if (itemstack.isEmpty())
                        {
                            playerentity.inventory.deleteStack(itemstack);
                        }
                    }

                    playerentity.addStat(Stats.ITEM_USED.get(this));
                }
            }
        }
    }

    @Override
    public int getUseDuration(ItemStack stack)
    {
        return 72000;
    }

    @Override
    public UseAction getUseAction(ItemStack stack)
    {
        return UseAction.BOW;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn)
    {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        boolean flag = !playerIn.findAmmo(itemstack).isEmpty();

        ActionResult<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onArrowNock(itemstack, worldIn, playerIn, handIn, flag);
        if (ret != null) return ret;

        if (!playerIn.abilities.isCreativeMode && !flag)
        {
            return ActionResult.resultFail(itemstack);
        } else
        {
            playerIn.setActiveHand(handIn);
            return ActionResult.resultConsume(itemstack);
        }
    }

    @Override
    public Predicate<ItemStack> getInventoryAmmoPredicate()
    {
        return itemStack -> itemStack.getItem() instanceof GoldenArrowItem;
    }

    @Override
    public int func_230305_d_()
    {
        return 15;
    }

    public AbstractArrowEntity customeArrow(AbstractArrowEntity arrow)
    {
        return arrow;
    }
}