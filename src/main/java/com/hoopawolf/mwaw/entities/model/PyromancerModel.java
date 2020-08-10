package com.hoopawolf.mwaw.entities.model;

import com.hoopawolf.mwaw.entities.PyromancerEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class PyromancerModel extends EntityModel<PyromancerEntity>
{
    private final ModelRenderer Head;
    private final ModelRenderer RightArm;
    private final ModelRenderer LeftArm;
    private final ModelRenderer RightLeg;
    private final ModelRenderer LeftLeg;
    private final ModelRenderer Body;
    private final ModelRenderer Cape;

    public PyromancerModel()
    {
        textureWidth = 128;
        textureHeight = 128;

        Head = new ModelRenderer(this);
        Head.setRotationPoint(0.0F, -7.9167F, 1.0F);
        Head.setTextureOffset(0, 0).addBox(-5.0F, -13.0833F, -5.0F, 10.0F, 13.0F, 10.0F, 0.0F, false);
        Head.setTextureOffset(0, 23).addBox(-4.0F, -10.0833F, -4.0F, 8.0F, 10.0F, 8.0F, 0.0F, false);
        Head.setTextureOffset(58, 58).addBox(-7.0F, -19.0833F, -1.0F, 2.0F, 17.0F, 2.0F, 0.0F, false);
        Head.setTextureOffset(58, 19).addBox(5.0F, -19.0833F, -1.0F, 2.0F, 17.0F, 2.0F, 0.0F, false);
        Head.setTextureOffset(12, 53).addBox(-1.0F, -19.0833F, 5.0F, 2.0F, 17.0F, 2.0F, 0.0F, false);
        Head.setTextureOffset(58, 38).addBox(-1.0F, -19.0833F, -7.0F, 2.0F, 17.0F, 2.0F, 0.0F, false);

        RightArm = new ModelRenderer(this);
        RightArm.setRotationPoint(-5.25F, -5.0F, 1.25F);
        RightArm.setTextureOffset(0, 41).addBox(-4.75F, -4.0F, -3.25F, 5.0F, 6.0F, 6.0F, 0.0F, false);
        RightArm.setTextureOffset(34, 51).addBox(-3.25F, -3.0F, -1.25F, 3.0F, 16.0F, 3.0F, 0.0F, false);

        LeftArm = new ModelRenderer(this);
        LeftArm.setRotationPoint(5.25F, -5.0F, 1.25F);
        LeftArm.setTextureOffset(34, 19).addBox(-0.25F, -4.0F, -3.25F, 5.0F, 6.0F, 6.0F, 0.0F, false);
        LeftArm.setTextureOffset(22, 48).addBox(0.25F, -3.0F, -1.25F, 3.0F, 16.0F, 3.0F, 0.0F, false);

        RightLeg = new ModelRenderer(this);
        RightLeg.setRotationPoint(-2.5F, 8.0F, 1.5F);
        RightLeg.setTextureOffset(0, 53).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 16.0F, 3.0F, 0.0F, false);

        LeftLeg = new ModelRenderer(this);
        LeftLeg.setRotationPoint(2.5F, 8.0F, 1.5F);
        LeftLeg.setTextureOffset(46, 51).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 16.0F, 3.0F, 0.0F, false);

        Body = new ModelRenderer(this);
        Body.setRotationPoint(0.0F, 0.0F, 0.75F);
        Body.setTextureOffset(32, 32).addBox(-5.0F, -8.0F, -0.75F, 10.0F, 16.0F, 3.0F, 0.0F, false);

        Cape = new ModelRenderer(this);
        Cape.setRotationPoint(0.0F, -7.0F, 2.75F);
        Body.addChild(Cape);
        Cape.setTextureOffset(40, 0).addBox(-5.0F, -1.0F, -0.5F, 10.0F, 18.0F, 1.0F, 0.0F, false);
    }

    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        Head.render(matrixStack, buffer, packedLight, packedOverlay);
        RightArm.render(matrixStack, buffer, packedLight, packedOverlay);
        LeftArm.render(matrixStack, buffer, packedLight, packedOverlay);
        RightLeg.render(matrixStack, buffer, packedLight, packedOverlay);
        LeftLeg.render(matrixStack, buffer, packedLight, packedOverlay);
        Body.render(matrixStack, buffer, packedLight, packedOverlay);
    }

    @Override
    public void setRotationAngles(PyromancerEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        this.Head.rotateAngleY = netHeadYaw * ((float) Math.PI / 180F);
        this.Head.rotateAngleX = headPitch * ((float) Math.PI / 180F);

        this.RightArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount * 0.5F;
        this.LeftArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
        this.RightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.LeftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;

    }
}