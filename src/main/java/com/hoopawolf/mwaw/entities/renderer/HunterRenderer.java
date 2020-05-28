package com.hoopawolf.mwaw.entities.renderer;

import com.hoopawolf.mwaw.entities.HunterEntity;
import com.hoopawolf.mwaw.entities.model.HunterModel;
import com.hoopawolf.mwaw.ref.Reference;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;

public class HunterRenderer extends BipedRenderer<HunterEntity, HunterModel>
{
    private static final ResourceLocation Hunter_Texture = new ResourceLocation(Reference.MOD_ID, "textures/entity/hunter.png");

    public HunterRenderer(EntityRendererManager renderManagerIn)
    {
        super(renderManagerIn, new HunterModel(0.0F), 0.5F);
        this.addLayer(new BipedArmorLayer<>(this, new BipedModel(0.5F), new BipedModel(1.0F)));
    }

    @Override
    public void render(HunterEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn)
    {
        this.setModelVisibilities(entityIn);
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    private void setModelVisibilities(HunterEntity clientPlayer)
    {
        HunterModel playermodel = this.getEntityModel();

        ItemStack itemstack = clientPlayer.getHeldItemMainhand();
        ItemStack itemstack1 = clientPlayer.getHeldItemOffhand();
        playermodel.isSneak = clientPlayer.isCrouching();
        BipedModel.ArmPose bipedmodel$armpose = this.getArmPose(clientPlayer, itemstack, itemstack1, Hand.MAIN_HAND);
        BipedModel.ArmPose bipedmodel$armpose1 = this.getArmPose(clientPlayer, itemstack, itemstack1, Hand.OFF_HAND);
        if (clientPlayer.getPrimaryHand() == HandSide.RIGHT)
        {
            playermodel.rightArmPose = bipedmodel$armpose;
            playermodel.leftArmPose = bipedmodel$armpose1;
        } else
        {
            playermodel.rightArmPose = bipedmodel$armpose1;
            playermodel.leftArmPose = bipedmodel$armpose;
        }
    }

    private BipedModel.ArmPose getArmPose(HunterEntity playerIn, ItemStack itemStackMain, ItemStack itemStackOff, Hand handIn)
    {
        BipedModel.ArmPose bipedmodel$armpose = BipedModel.ArmPose.EMPTY;
        ItemStack itemstack = handIn == Hand.MAIN_HAND ? itemStackMain : itemStackOff;
        if (!itemstack.isEmpty())
        {
            bipedmodel$armpose = BipedModel.ArmPose.ITEM;
            if (playerIn.getItemInUseCount() > 0)
            {
                UseAction useaction = itemstack.getUseAction();
                if (useaction == UseAction.BLOCK)
                {
                    bipedmodel$armpose = BipedModel.ArmPose.BLOCK;
                } else if (useaction == UseAction.BOW)
                {
                    bipedmodel$armpose = BipedModel.ArmPose.BOW_AND_ARROW;
                } else if (useaction == UseAction.SPEAR)
                {
                    bipedmodel$armpose = BipedModel.ArmPose.THROW_SPEAR;
                } else if (useaction == UseAction.CROSSBOW && handIn == playerIn.getActiveHand())
                {
                    bipedmodel$armpose = BipedModel.ArmPose.CROSSBOW_CHARGE;
                }
            } else
            {
                boolean flag3 = itemStackMain.getItem() == Items.CROSSBOW;
                boolean flag = CrossbowItem.isCharged(itemStackMain);
                boolean flag1 = itemStackOff.getItem() == Items.CROSSBOW;
                boolean flag2 = CrossbowItem.isCharged(itemStackOff);
                if (flag3 && flag)
                {
                    bipedmodel$armpose = BipedModel.ArmPose.CROSSBOW_HOLD;
                }

                if (flag1 && flag2 && itemStackMain.getItem().getUseAction(itemStackMain) == UseAction.NONE)
                {
                    bipedmodel$armpose = BipedModel.ArmPose.CROSSBOW_HOLD;
                }
            }
        }

        return bipedmodel$armpose;
    }

    @Override
    public ResourceLocation getEntityTexture(HunterEntity _entity)
    {
        return Hunter_Texture;
    }
}
