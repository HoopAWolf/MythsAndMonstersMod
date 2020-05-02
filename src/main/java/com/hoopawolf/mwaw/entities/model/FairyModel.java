package com.hoopawolf.mwaw.entities.model;

import com.hoopawolf.mwaw.entities.FairyEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FairyModel extends EntityModel<FairyEntity>
{
    private final ModelRenderer Head;
    private final ModelRenderer Body;
    private final ModelRenderer LegRight;
    private final ModelRenderer LegLeft;
    private final ModelRenderer ArmRight;
    private final ModelRenderer ArmLeft;
    private final ModelRenderer WingRight;
    private final ModelRenderer WingLeft;

    public FairyModel()
    {
        textureWidth = 64;
        textureHeight = 64;

        Head = new ModelRenderer(this);
        Head.setRotationPoint(0.0F, 0.0F, 0.0F);
        Head.addBox("Head", -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, 0, 0);

        Body = new ModelRenderer(this);
        Body.setRotationPoint(0.0F, 4.0F, 0.0F);
        Body.addBox("Body", -4.0F, -4.0F, -2.0F, 8, 12, 4, 0.0F, 0, 16);

        LegRight = new ModelRenderer(this);
        LegRight.setRotationPoint(-2.0F, 12.0F, 0.0F);
        LegRight.addBox("LegRight", -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, 24, 34);

        LegLeft = new ModelRenderer(this);
        LegLeft.setRotationPoint(2.0F, 12.0F, 0.0F);
        LegLeft.addBox("LegLeft", -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, 24, 34);

        ArmRight = new ModelRenderer(this);
        ArmRight.setRotationPoint(-4.0F, 2.0F, 0.0F);
        ArmRight.addBox("ArmRight", -3.0F, -2.0F, -2.0F, 3, 12, 4, 0.0F, 0, 42);

        ArmLeft = new ModelRenderer(this);
        ArmLeft.setRotationPoint(4.0F, 3.0F, 0.0F);
        ArmLeft.addBox("ArmLeft", 0.0F, -3.0F, -2.0F, 3, 12, 4, 0.0F, 0, 42);

        WingRight = new ModelRenderer(this);
        WingRight.setRotationPoint(-1.0F, 5.0F, 0.0F);
        WingRight.rotateAngleX = 0.0f;
        WingRight.rotateAngleY = 0.1745F;
        WingRight.rotateAngleZ = 0.0f;
        WingRight.addBox("WingRight", -11.0F, -7.0F, 2.0F, 12, 10, 0, 0.0F, 0, 32);

        WingLeft = new ModelRenderer(this);
        WingLeft.setRotationPoint(1.0F, 5.0F, 0.0F);
        WingLeft.rotateAngleX = 0.0f;
        WingLeft.rotateAngleY = -0.1745F;
        WingLeft.rotateAngleZ = 0.0f;
        WingLeft.addBox("WingLeft", -1.0F, -7.0F, 2.0F, 12, 10, 0, 0.0F, 40, 22);
    }

    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder iVertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
    {
        Head.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
        Body.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
        LegRight.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
        LegLeft.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
        ArmRight.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
        ArmLeft.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
        WingRight.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
        WingLeft.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
    }

    @Override
    public void setRotationAngles(FairyEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        Head.setRotationPoint(0.0F, 0.0F, 0.0F);
        Body.setRotationPoint(0.0F, 4.0F, 0.0F);
        LegRight.setRotationPoint(-2.0F, 12.0F, 0.0F);
        LegLeft.setRotationPoint(2.0F, 12.0F, 0.0F);
        ArmRight.setRotationPoint(-4.0F, 3.0F, 0.0F);
        ArmLeft.setRotationPoint(4.0F, 3.0F, 0.0F);

        Head.rotateAngleY = netHeadYaw * ((float) Math.PI / 180F);
        Head.rotateAngleX = headPitch * ((float) Math.PI / 180F);

        this.WingRight.rotateAngleX = 0.0f;
        this.WingRight.rotateAngleY = 0.2745F;
        this.WingRight.rotateAngleZ = 0.0f;
        this.WingLeft.rotateAngleX = 0.0f;
        this.WingLeft.rotateAngleY = -0.2745F;
        this.WingLeft.rotateAngleZ = 0.0f;
        this.Body.rotateAngleX = 0.0F;
        this.ArmRight.rotateAngleX = 0.0F;
        this.ArmLeft.rotateAngleX = 0.0F;
        this.LegRight.rotateAngleX = 0.0F;
        this.LegRight.rotateAngleY = 0.0F;
        this.LegRight.rotateAngleZ = 0.0F;
        this.LegLeft.rotateAngleX = 0.0F;
        this.LegLeft.rotateAngleY = 0.0F;
        this.LegLeft.rotateAngleZ = 0.0F;

        if (entityIn.isResting() && entityIn.onGround)
        {
            this.ArmRight.rotateAngleX = -(float) Math.PI / 5F;
            this.ArmLeft.rotateAngleX = -(float) Math.PI / 5F;
            this.ArmRight.rotateAngleX += (-0.07F * MathHelper.sin(ageInTicks / -3F)) * 0.5F;
            this.ArmLeft.rotateAngleX += (0.07F * MathHelper.sin(ageInTicks / -3F)) * 0.5F;
            this.LegRight.rotateAngleX = -1.4137167F;
            this.LegRight.rotateAngleY = ((float) Math.PI / 10F);
            this.LegRight.rotateAngleZ = 0.07853982F;
            this.LegLeft.rotateAngleX = -1.4137167F;
            this.LegLeft.rotateAngleY = (-(float) Math.PI / 10F);
            this.LegLeft.rotateAngleZ = -0.07853982F;
        } else
        {
            if (entityIn.world.isAirBlock(new BlockPos(entityIn.getPosX(), entityIn.getPosY() - 0.5D, entityIn.getPosZ())))
            {
                if (!entityIn.isPassenger())
                {
                    WingRight.rotateAngleY = 0.47123894F + MathHelper.cos(ageInTicks * 1.5F) * (float) Math.PI * 0.05F;
                    WingLeft.rotateAngleY = -WingRight.rotateAngleY;
                    WingLeft.rotateAngleZ = -0.47123894F;
                    WingRight.rotateAngleZ = 0.47123894F;
                }

                this.Body.rotateAngleX = 0.5F;
                this.Body.rotationPointY = 2.2F;
                this.LegRight.rotationPointZ = 3.0F;
                this.LegLeft.rotationPointZ = 3.0F;
                this.LegRight.rotationPointY = 8.0F;
                this.LegLeft.rotationPointY = 8.0F;
                this.Head.rotationPointZ = -3.2F;
                this.ArmLeft.rotationPointY = 1.7F;
                this.ArmRight.rotationPointY = 0.5F;
                this.ArmLeft.rotationPointZ = -1.2F;
                this.ArmRight.rotationPointZ = -1.2F;

                this.LegRight.rotateAngleX = 0.2137167F;
                this.LegLeft.rotateAngleX = 0.2137167F;
                this.LegRight.rotateAngleZ = 0.07853982F;
                this.LegLeft.rotateAngleZ = -0.07853982F;
            }
        }
    }
}
