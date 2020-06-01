package com.hoopawolf.mwaw.entities.renderer.layer;

import com.hoopawolf.mwaw.entities.KitsuneEntity;
import com.hoopawolf.mwaw.entities.model.KitsuneModel;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KitsuneHeldItemLayer extends LayerRenderer<KitsuneEntity, KitsuneModel>
{
    public KitsuneHeldItemLayer(IEntityRenderer<KitsuneEntity, KitsuneModel> p_i50938_1_)
    {
        super(p_i50938_1_);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, KitsuneEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
    {
        matrixStackIn.push();
        matrixStackIn.translate((this.getEntityModel()).Head.rotationPointX / 16.0F, (this.getEntityModel()).Head.rotationPointY / 16.0F, (this.getEntityModel()).Head.rotationPointZ / 16.0F);
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(netHeadYaw));
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(headPitch));

        matrixStackIn.translate(0.0F, 0.1F, -0.5D);
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0F));
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90.0F));

        ItemStack itemstack = entitylivingbaseIn.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
        Minecraft.getInstance().getFirstPersonRenderer().renderItemSide(entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.pop();
    }
}
