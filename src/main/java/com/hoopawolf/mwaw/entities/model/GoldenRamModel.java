package com.hoopawolf.mwaw.entities.model;

import com.hoopawolf.mwaw.entities.GoldenRamEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GoldenRamModel extends EntityModel<GoldenRamEntity>
{
    private final ModelRenderer Body;
    private final ModelRenderer Head;
    private final ModelRenderer FrontRightLeg;
    private final ModelRenderer FrontLeftLeg;
    private final ModelRenderer BackRightLeg;
    private final ModelRenderer BackLeftLeg;
    private float headRotationAngleX;

    public GoldenRamModel()
    {
        textureWidth = 128;
        textureHeight = 128;

        Body = new ModelRenderer(this);
        Body.setRotationPoint(0.0F, 10.0F, -0.75F);
        //Body.addBox("Body", -5.5F, -4.0F, -9.25F, 11, 8, 19, 0.0F, 0, 0);
        Body.addBox("Body", -4.0F, -3.0F, -8.25F, 8, 6, 16, 0.0F, 0, 27);

        Head = new ModelRenderer(this);
        Head.setRotationPoint(0.0F, 7.3333F, -5.2917F);
        Head.addBox("Head", 3.0F, -0.3333F, -10.7083F, 3, 3, 3, 0.0F, 0, 27);
        Head.addBox("Head", 3.0F, -4.3333F, -7.7083F, 3, 7, 7, 0.0F, 0, 49);
        Head.addBox("Head", -6.0F, -0.3333F, -10.7083F, 3, 3, 3, 0.0F, 0, 33);
        Head.addBox("Head", -6.0F, -4.3333F, -7.7083F, 3, 7, 7, 0.0F, 41, 42);
        Head.addBox("Head", -3.0F, -3.3333F, -9.7083F, 6, 6, 9, 0.0F, 41, 0);
        //Head.addBox("Head", -3.5F, -3.8333F, -8.2083F, 7, 7, 8, 0.0F, 32, 27);

        FrontRightLeg = new ModelRenderer(this);
        FrontRightLeg.setRotationPoint(-3.0F, 13.5F, -6.0F);
        // FrontRightLeg.addBox("FrontRightLeg", -2.5F, 0.5F, -2.5F, 5, 5, 5, 0.0F, 20, 49);
        FrontRightLeg.addBox("FrontRightLeg", -2.0F, -0.5F, -2.0F, 4, 11, 4, 0.0F, 0, 0);

        FrontLeftLeg = new ModelRenderer(this);
        FrontLeftLeg.setRotationPoint(3.0F, 13.5F, -6.0F);
        //FrontLeftLeg.addBox("FrontLeftLeg", -2.5F, 0.5F, -2.5F, 5, 5, 5, 0.0F, 20, 49);
        FrontLeftLeg.addBox("FrontLeftLeg", -2.0F, -0.5F, -2.0F, 4, 11, 4, 0.0F, 0, 0);

        BackRightLeg = new ModelRenderer(this);
        BackRightLeg.setRotationPoint(-3.0F, 13.5F, 6.0F);
        // BackRightLeg.addBox("BackRightLeg", -2.5F, 0.5F, -2.5F, 5, 5, 5, 0.0F, 20, 49);
        BackRightLeg.addBox("BackRightLeg", -2.0F, -0.5F, -2.0F, 4, 11, 4, 0.0F, 0, 0);

        BackLeftLeg = new ModelRenderer(this);
        BackLeftLeg.setRotationPoint(3.0F, 13.5F, 6.0F);
        //BackLeftLeg.addBox("BackLeftLeg", -2.5F, 0.5F, -2.5F, 5, 5, 5, 0.0F, 20, 49);
        BackLeftLeg.addBox("BackLeftLeg", -2.0F, -0.5F, -2.0F, 4, 11, 4, 0.0F, 0, 0);
    }

    @Override
    public void setLivingAnimations(GoldenRamEntity entityIn, float limbSwing, float limbSwingAmount, float partialTick)
    {
        this.Body.rotateAngleX = 0.0F;
        this.Head.rotationPointY = 6.0F + entityIn.getHeadRotationPointY(partialTick) * 9.0F;
        this.headRotationAngleX = entityIn.getHeadRotationAngleX(partialTick);

        this.Head.rotateAngleZ = entityIn.getShakeAngle(partialTick, 0.0F);
        this.Body.rotateAngleZ = entityIn.getShakeAngle(partialTick, -0.16F);

        float f6 = entityIn.getRearingAmount(partialTick);
        float f7 = 1.0F - f6;

        this.Head.rotationPointY = f6 + (1.0F - f6) * this.Head.rotationPointY;
        this.Body.rotateAngleX = f6 * (-(float) Math.PI / 4F) + f7 * this.Body.rotateAngleX;
        this.FrontLeftLeg.rotationPointY = 2.0F * f6 + 14.0F * f7;
        this.FrontRightLeg.rotationPointY = 2.0F * f6 + 14.0F * f7;
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
