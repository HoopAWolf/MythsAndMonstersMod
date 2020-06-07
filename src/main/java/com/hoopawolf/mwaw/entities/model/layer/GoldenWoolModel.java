package com.hoopawolf.mwaw.entities.model.layer;

import com.hoopawolf.mwaw.entities.GoldenRamEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GoldenWoolModel extends EntityModel<GoldenRamEntity>
{
    private final ModelRenderer Body;
    private final ModelRenderer Head;
    private final ModelRenderer FrontRightLeg;
    private final ModelRenderer FrontLeftLeg;
    private final ModelRenderer BackRightLeg;
    private final ModelRenderer BackLeftLeg;

    private float headRotationAngleX;

    public GoldenWoolModel()
    {
        textureWidth = 128;
        textureHeight = 128;

        Body = new ModelRenderer(this);
        Body.setRotationPoint(0.0F, 10.0F, -0.75F);
        Body.addBox("Body", -5.5F, -4.0F, -9.25F, 11, 8, 19, 0.0F, 0, 0);

        Head = new ModelRenderer(this);
        Head.setRotationPoint(0.0F, 7.3333F, -5.2917F);
        Head.addBox("Head", -3.5F, -3.8333F, -8.2083F, 7, 7, 8, 0.0F, 32, 27);

        FrontRightLeg = new ModelRenderer(this);
        FrontRightLeg.setRotationPoint(-3.0F, 13.5F, -6.0F);
        FrontRightLeg.addBox("FrontRightLeg", -2.5F, 0.5F, -2.5F, 5, 5, 5, 0.0F, 20, 49);

        FrontLeftLeg = new ModelRenderer(this);
        FrontLeftLeg.setRotationPoint(3.0F, 13.5F, -6.0F);
        FrontLeftLeg.addBox("FrontLeftLeg", -2.5F, 0.5F, -2.5F, 5, 5, 5, 0.0F, 20, 49);

        BackRightLeg = new ModelRenderer(this);
        BackRightLeg.setRotationPoint(-3.0F, 13.5F, 6.0F);
        BackRightLeg.addBox("BackRightLeg", -2.5F, 0.5F, -2.5F, 5, 5, 5, 0.0F, 20, 49);

        BackLeftLeg = new ModelRenderer(this);
        BackLeftLeg.setRotationPoint(3.0F, 13.5F, 6.0F);
        BackLeftLeg.addBox("BackLeftLeg", -2.5F, 0.5F, -2.5F, 5, 5, 5, 0.0F, 20, 49);
    }

    @Override
    public void setLivingAnimations(GoldenRamEntity entityIn, float limbSwing, float limbSwingAmount, float partialTick)
    {
        this.Head.rotationPointY = 6.0F + entityIn.getHeadRotationPointY(partialTick) * 9.0F;
        this.headRotationAngleX = entityIn.getHeadRotationAngleX(partialTick);
    }

    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder iVertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
    {
        Body.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
        Head.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
        FrontRightLeg.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
        FrontLeftLeg.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
        BackRightLeg.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
        BackLeftLeg.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
    }

    @Override
    public void setRotationAngles(GoldenRamEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        this.Head.rotateAngleX = headPitch * ((float) Math.PI / 180F);
        this.Head.rotateAngleY = netHeadYaw * ((float) Math.PI / 180F);
        this.BackRightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.BackLeftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        this.FrontRightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        this.FrontLeftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;

        this.Head.rotateAngleX = headRotationAngleX;
    }
}