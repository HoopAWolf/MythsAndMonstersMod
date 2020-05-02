package com.hoopawolf.mwaw.entities.model;

import com.hoopawolf.mwaw.entities.DendroidEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DendroidModel extends EntityModel<DendroidEntity>
{
    private final ModelRenderer Jaw;
    private final ModelRenderer TopJaw;
    private final ModelRenderer BottomJaw;
    private final ModelRenderer RightLeg;
    private final ModelRenderer LeftLeg;
    private final ModelRenderer FrontLeg;
    private final ModelRenderer Eye;
    private final ModelRenderer Hip;

    public DendroidModel()
    {
        textureWidth = 128;
        textureHeight = 128;

        Jaw = new ModelRenderer(this);
        Jaw.setRotationPoint(0.0F, 13.0F, 1.0F);

        TopJaw = new ModelRenderer(this);
        TopJaw.setRotationPoint(0.0F, -7.0F, 7.5F);
        Jaw.addChild(TopJaw);
        TopJaw.addBox("TopJaw", -8.0F, -6.0F, -17.5F, 16, 6, 17, 0.0F, 0, 0);

        BottomJaw = new ModelRenderer(this);
        BottomJaw.setRotationPoint(0.0F, -7.0F, 7.5F);
        Jaw.addChild(BottomJaw);
        BottomJaw.addBox("BottomJaw", -8.0F, -0.05F, -17.5F, 16, 6, 17, 0.0F, 0, 23);

        RightLeg = new ModelRenderer(this);
        RightLeg.setRotationPoint(-5.0F, 16.0F, 0.0F);
        RightLeg.addBox("RightLeg", -3.0F, -2.0F, 0.0F, 5, 4, 7, 0.0F, 49, 23);
        RightLeg.addBox("RightLeg", -2.0F, 0.0F, 6.0F, 3, 8, 4, 0.0F, 0, 23);

        LeftLeg = new ModelRenderer(this);
        LeftLeg.setRotationPoint(5.0F, 16.0F, 0.0F);
        LeftLeg.addBox("LeftLeg", -2.0F, -2.0F, 0.0F, 5, 4, 7, 0.0F, 49, 0);
        LeftLeg.addBox("LeftLeg", -1.0F, 0.0F, 6.0F, 3, 8, 4, 0.0F, 24, 58);

        FrontLeg = new ModelRenderer(this);
        FrontLeg.setRotationPoint(0.0F, 16.0F, -0.5F);
        FrontLeg.addBox("FrontLeg", -3.0F, -2.0F, -5.5F, 6, 4, 6, 0.0F, 0, 58);
        FrontLeg.addBox("FrontLeg", -2.0F, 0.0F, -7.5F, 4, 8, 4, 0.0F, 0, 0);

        Eye = new ModelRenderer(this);
        Eye.setRotationPoint(0.0F, -8.0F, -1.0F);
        Jaw.addChild(Eye);
        Eye.addBox("Eye", -4.0F, -4.0F, -5.0F, 8, 8, 10, 0.0F, 44, 46);

        Hip = new ModelRenderer(this);
        Hip.setRotationPoint(0.0F, 13.0F, 1.0F);
        Hip.addBox("Hip", -6.0F, -1.0F, -5.0F, 12, 2, 10, 0.0F, 0, 46);
    }

    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder iVertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
    {
        Jaw.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
        RightLeg.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
        LeftLeg.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
        FrontLeg.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
        Hip.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
    }

    public void setRotationAngles(DendroidEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        float f3 = -(MathHelper.cos(limbSwing * 0.6662F * 2.0F + 0.0F) * 0.2F) * limbSwingAmount;
        float f5 = -(MathHelper.cos(limbSwing * 0.6662F + ((float) Math.PI / 2F)) * 1.8F) * limbSwingAmount;
        float f7 = -(MathHelper.sin(limbSwing * 0.6662F + ((float) Math.PI / 1.5F)) * 1.8F) * limbSwingAmount;

        this.TopJaw.rotateAngleX = 0.0F;
        this.BottomJaw.rotateAngleX = 0.0F;
        this.FrontLeg.rotateAngleX = 0.0F;
        this.RightLeg.rotateAngleY = 0.0F;
        this.LeftLeg.rotateAngleY = 0.0F;

        if (entityIn.isShooting())
        {
            this.TopJaw.rotateAngleX = -0.40F;
            this.BottomJaw.rotateAngleX = 0.1F;
        }

        this.Jaw.rotateAngleY = netHeadYaw * ((float) Math.PI / 180F);
        this.Jaw.rotateAngleX = headPitch * ((float) Math.PI / 180F);
        this.Eye.rotateAngleY = netHeadYaw * ((float) Math.PI / 270F);

        this.FrontLeg.rotateAngleX += f3;
        this.RightLeg.rotateAngleY += f5;
        this.LeftLeg.rotateAngleY += -f7;

    }
}