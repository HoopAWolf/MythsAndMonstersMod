package com.hoopawolf.mwaw.entities.model;

import com.hoopawolf.mwaw.entities.ClayGolemEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClayGolemModel extends EntityModel<ClayGolemEntity>
{
    private final ModelRenderer bb_main;
    private final ModelRenderer Body;
    private final ModelRenderer RightArm;
    private final ModelRenderer LeftArm;
    private final ModelRenderer RightLeg;
    private final ModelRenderer LeftLeg;

    public ClayGolemModel()
    {
        textureWidth = 128;
        textureHeight = 128;

        bb_main = new ModelRenderer(this);
        bb_main.setRotationPoint(0.0F, -11.0F, -11.0F);
        bb_main.addBox("bb_main", -5.0F, -4.0F, -7.0F, 10, 8, 8, 0.0F, 0, 89);
        bb_main.addBox("bb_main", -7.0F, -6.0F, -8.0F, 14, 5, 9, 0.0F, 76, 0);
        bb_main.addBox("bb_main", -2.0F, -1.0F, -10.0F, 4, 6, 3, 0.0F, 0, 0);

        Body = new ModelRenderer(this);
        Body.setRotationPoint(0.0F, 24.0F, 0.0F);
        Body.addBox("Body", -11.0F, -28.0F, -7.0F, 23, 11, 12, 0.0F, 0, 34);
        Body.addBox("Body", -15.0F, -44.0F, -10.0F, 30, 18, 16, 0.0F, 0, 0);

        RightArm = new ModelRenderer(this);
        RightArm.setRotationPoint(15.0F, -12.0F, -0.5F);
        RightArm.addBox("RightArm", 0.0F, -3.0F, -4.5F, 7, 23, 9, 0.0F, 32, 57);

        LeftArm = new ModelRenderer(this);
        LeftArm.setRotationPoint(-15.0F, -10.0F, -0.5F);
        LeftArm.addBox("LeftArm", -7.0F, -5.0F, -4.5F, 7, 23, 9, 0.0F, 0, 57);

        RightLeg = new ModelRenderer(this);
        RightLeg.setRotationPoint(8.0F, 8.0F, 0.0F);
        RightLeg.addBox("RightLeg", -4.0F, -1.0F, -5.0F, 8, 17, 10, 0.0F, 64, 64);

        LeftLeg = new ModelRenderer(this);
        LeftLeg.setRotationPoint(-7.0F, 8.0F, 0.0F);
        LeftLeg.addBox("LeftLeg", -4.0F, -1.0F, -5.0F, 8, 17, 10, 0.0F, 70, 34);
    }

    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder iVertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
    {
        bb_main.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
        Body.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
        RightLeg.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
        LeftLeg.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
        RightArm.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
        LeftArm.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
    }

    @Override
    public void setLivingAnimations(ClayGolemEntity entityIn, float limbSwing, float limbSwingAmount, float partialTick)
    {
        int i = entityIn.getAttackTimer();
        if (i > 0)
        {
            this.RightArm.rotateAngleX = -4.0618F - (-2.0F + 1.5F * this.triangleWave((float) i - partialTick, 10.0F));
            this.LeftArm.rotateAngleX = -4.0618F - (-2.0F + 1.5F * this.triangleWave((float) i - partialTick, 10.0F));
        } else
        {
            this.RightArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount * 0.5F;
            this.LeftArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
        }
    }

    @Override
    public void setRotationAngles(ClayGolemEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        this.bb_main.rotateAngleY = netHeadYaw * ((float) Math.PI / 450F);
        this.bb_main.rotateAngleX = headPitch * ((float) Math.PI / 450F);

        this.RightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.LeftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
    }

    private float triangleWave(float p_78172_1_, float p_78172_2_)
    {
        return (Math.abs(p_78172_1_ % p_78172_2_ - p_78172_2_ * 0.5F) - p_78172_2_ * 0.25F) / (p_78172_2_ * 0.25F);
    }
}
