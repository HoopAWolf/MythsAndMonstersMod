package com.hoopawolf.mwaw.entities.model;

import com.hoopawolf.mwaw.entities.WolpertingerEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WolpertingerModel extends EntityModel<WolpertingerEntity>
{
    private final ModelRenderer Head;
    private final ModelRenderer LeftEar;
    private final ModelRenderer RightEar;
    private final ModelRenderer FrontRightLeg;
    private final ModelRenderer FrontLeftLeg;
    private final ModelRenderer RightLeg;
    private final ModelRenderer LeftLeg;
    private final ModelRenderer Body;
    private final ModelRenderer Tail;
    private final ModelRenderer RightWing;
    private final ModelRenderer LeftWing;

    private float jumpRotation;

    public WolpertingerModel()
    {
        textureWidth = 64;
        textureHeight = 64;

        Head = new ModelRenderer(this);
        Head.setRotationPoint(0.0F, 11.8333F, -6.8655F);
        Head.addBox("Head", 2.0F, -4.8333F, -1.1345F, 0, 4, 4, 0.0F, 0, 30);
        Head.addBox("Head", -2.0F, -4.8333F, -1.1345F, 0, 4, 4, 0.0F, 9, 21);
        Head.addBox("Head", -2.5F, -0.8333F, -3.1345F, 5, 4, 5, 0.0F, 0, 16);
        Head.addBox("Head", -0.5F, 0.6667F, -3.5201F, 1, 1, 1, 0.0F, 6, 0);

        LeftEar = new ModelRenderer(this);
        LeftEar.setRotationPoint(0.0F, 12.1667F, 6.8655F);
        Head.addChild(LeftEar);
        LeftEar.addBox("LeftEar", 2.5F, -13.0F, -7.0F, 1, 5, 2, 0.0F, 32, 0);

        RightEar = new ModelRenderer(this);
        RightEar.setRotationPoint(0.0F, 12.1667F, 6.8655F);
        Head.addChild(RightEar);
        RightEar.addBox("RightEar", -3.5F, -13.0F, -7.0F, 1, 5, 2, 0.0F, 31, 32);

        FrontRightLeg = new ModelRenderer(this);
        FrontRightLeg.setRotationPoint(-3.0F, 17.0F, -6.0F);
        FrontRightLeg.rotateAngleX = -0.1745F;
        FrontRightLeg.addBox("FrontRightLeg", -1.0F, 0.0F, -1.0F, 2, 7, 2, 0.0F, 23, 32);

        FrontLeftLeg = new ModelRenderer(this);
        FrontLeftLeg.setRotationPoint(3.0F, 17.0F, -6.0F);
        FrontLeftLeg.rotateAngleX = -0.1745F;
        FrontLeftLeg.addBox("FrontLeftLeg", -1.0F, 0.0F, -1.0F, 2, 7, 2, 0.0F, 0, 0);

        RightLeg = new ModelRenderer(this);
        RightLeg.setRotationPoint(-3.0F, 23.5F, 1.0F);
        RightLeg.addBox("RightLeg", -1.0F, -0.5F, -5.5F, 2, 1, 7, 0.0F, 24, 24);

        LeftLeg = new ModelRenderer(this);
        LeftLeg.setRotationPoint(3.0F, 23.5F, 1.0F);
        LeftLeg.addBox("LeftLeg", -1.0F, -0.5F, -5.5F, 2, 1, 7, 0.0F, 13, 22);

        Body = new ModelRenderer(this);
        Body.setRotationPoint(0.0F, 15.0F, -8.6667F);
        Body.rotateAngleX = -0.4363F;
        Body.addBox("Body", -3.0F, -4.0F, 2.6667F, 6, 6, 10, 0.0F, 0, 0);
        Body.addBox("Body", -4.0F, -1.0F, 8.6667F, 2, 4, 5, 0.0F, 0, 25);
        Body.addBox("Body", 2.0F, -1.0F, 8.6667F, 2, 4, 5, 0.0F, 9, 30);

        Tail = new ModelRenderer(this);
        Tail.setRotationPoint(0.0F, -2.5F, 12.6667F);
        Body.addChild(Tail);
        Tail.addBox("Tail", -2.0F, -3.5F, 0.0F, 4, 5, 2, 0.0F, 30, 14);

        RightWing = new ModelRenderer(this);
        RightWing.setRotationPoint(-3.0F, 13.5F, -4.0F);
        RightWing.rotateAngleY = 0.6109F;
        RightWing.rotateAngleZ = -1.0472F;
        RightWing.addBox("RightWing", -6.0F, -0.5F, 0.0F, 6, 1, 1, 0.0F, 24, 22);
        RightWing.addBox("RightWing", -6.0F, 0.0F, 1.0F, 5, 0, 6, 0.0F, 14, 16);

        LeftWing = new ModelRenderer(this);
        LeftWing.setRotationPoint(3.0F, 13.5F, -4.0F);
        LeftWing.rotateAngleY = -0.6109F;
        LeftWing.rotateAngleZ = 1.0472F;
        LeftWing.addBox("LeftWing", 0.0F, -0.5F, 0.0F, 6, 1, 1, 0.0F, 22, 7);
        LeftWing.addBox("LeftWing", 1.0F, 0.0F, 0.0F, 5, 0, 7, 0.0F, 15, 0);
    }

    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder iVertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
    {
        if (this.isChild)
        {
            matrixStack.push();
            matrixStack.scale(0.56666666F, 0.56666666F, 0.56666666F);
            matrixStack.translate(0.0D, 1.35D, 0.15D);
            Head.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
            matrixStack.pop();

            matrixStack.push();
            matrixStack.scale(0.4F, 0.4F, 0.4F);
            matrixStack.translate(0.0D, 2.25D, 0.0D);
            FrontRightLeg.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
            FrontLeftLeg.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
            RightLeg.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
            LeftLeg.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
            Body.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
            matrixStack.pop();

            matrixStack.push();
            matrixStack.scale(0.3F, 0.3F, 0.3F);
            matrixStack.translate(0.0D, 3.28D, 0.0D);
            RightWing.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
            LeftWing.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
            matrixStack.pop();
        } else
        {
            Head.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
            FrontRightLeg.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
            FrontLeftLeg.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
            RightLeg.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
            LeftLeg.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
            Body.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
            RightWing.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
            LeftWing.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
        }
    }

    @Override
    public void setRotationAngles(WolpertingerEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        float f = ageInTicks - (float) entityIn.ticksExisted;

        RightWing.rotateAngleY = 0.6109F;
        RightWing.rotateAngleZ = -1.0472F;
        LeftWing.rotateAngleY = -0.6109F;
        LeftWing.rotateAngleZ = 1.0472F;

        this.Head.rotateAngleX = headPitch * ((float) Math.PI / 180F);
        this.Head.rotateAngleY = netHeadYaw * ((float) Math.PI / 180F);

        this.jumpRotation = MathHelper.sin(entityIn.getJumpCompletion(f) * (float) Math.PI);

        if (!entityIn.isOnGround() && jumpRotation == 0)
        {
            RightWing.rotateAngleY = 0.47123894F + MathHelper.cos(ageInTicks * 1.5F) * (float) Math.PI * 0.05F;
            LeftWing.rotateAngleY = -RightWing.rotateAngleY;
            LeftWing.rotateAngleZ = -0.47123894F;
            RightWing.rotateAngleZ = 0.47123894F;

            this.LeftLeg.rotateAngleX = 0.8F * 50.0F * ((float) Math.PI / 180F);
            this.RightLeg.rotateAngleX = 0.8F * 50.0F * ((float) Math.PI / 180F);
        } else
        {
            if (jumpRotation < 0)
                this.Tail.rotateAngleZ = MathHelper.cos(ageInTicks * 1.5F) * (float) Math.PI * 0.05F;

            this.LeftLeg.rotateAngleX = this.jumpRotation * 50.0F * ((float) Math.PI / 180F);
            this.RightLeg.rotateAngleX = this.jumpRotation * 50.0F * ((float) Math.PI / 180F);
            this.FrontLeftLeg.rotateAngleX = (this.jumpRotation * -40.0F - 11.0F) * ((float) Math.PI / 180F);
            this.FrontRightLeg.rotateAngleX = (this.jumpRotation * -40.0F - 11.0F) * ((float) Math.PI / 180F);
        }
    }

    @Override
    public void setLivingAnimations(WolpertingerEntity entityIn, float limbSwing, float limbSwingAmount, float partialTick)
    {
        super.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTick);
        this.jumpRotation = MathHelper.sin(entityIn.getJumpCompletion(partialTick) * (float) Math.PI);
    }
}