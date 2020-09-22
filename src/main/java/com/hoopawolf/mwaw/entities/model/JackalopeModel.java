package com.hoopawolf.mwaw.entities.model;

import com.hoopawolf.mwaw.entities.JackalopeEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class JackalopeModel extends EntityModel<JackalopeEntity>
{
    private final ModelRenderer Body;
    private final ModelRenderer Head;
    private final ModelRenderer RightEar;
    private final ModelRenderer LeftEar;
    private final ModelRenderer FrontRightLeg;
    private final ModelRenderer FrontLeftLeg;
    private final ModelRenderer BackRightLeg;
    private final ModelRenderer BackLeftLeg;

    public JackalopeModel()
    {
        textureWidth = 64;
        textureHeight = 64;

        Body = new ModelRenderer(this);
        Body.setRotationPoint(0.0F, 14.0F, -4.0F);
        Body.setTextureOffset(31, 11).addBox(-2.0F, -2.8191F, 8.5F, 4.0F, 4.0F, 2.0F, 0.0F, false);
        Body.setTextureOffset(0, 0).addBox(-3.0F, -3.0F, -0.342F, 6.0F, 5.0F, 9.0F, 0.0F, false);

        Head = new ModelRenderer(this);
        Head.setRotationPoint(0.0F, -2.75F, -1.5F);
        Body.addChild(Head);
        Head.setTextureOffset(0, 20).addBox(-2.0F, -8.25F, -5.5F, 0.0F, 5.0F, 6.0F, 0.0F, false);
        Head.setTextureOffset(19, 8).addBox(2.0F, -8.25F, -5.5F, 0.0F, 5.0F, 6.0F, 0.0F, false);
        Head.setTextureOffset(18, 20).addBox(-2.5F, -3.25F, -4.5F, 5.0F, 4.0F, 6.0F, 0.0F, false);
        Head.setTextureOffset(0, 14).addBox(-0.5F, -1.25F, -5.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);

        RightEar = new ModelRenderer(this);
        RightEar.setRotationPoint(-1.5F, -2.75F, 0.0F);
        Head.addChild(RightEar);
        RightEar.setTextureOffset(21, 0).addBox(-1.7588F, -6.4158F, 0.4623F, 3.0F, 7.0F, 1.0F, 0.0F, false);

        LeftEar = new ModelRenderer(this);
        LeftEar.setRotationPoint(1.5F, -2.75F, 0.0F);
        Head.addChild(LeftEar);
        LeftEar.setTextureOffset(0, 0).addBox(-1.2412F, -6.4158F, 0.4623F, 3.0F, 7.0F, 1.0F, 0.0F, false);

        FrontRightLeg = new ModelRenderer(this);
        FrontRightLeg.setRotationPoint(-3.0F, 0.5F, -1.0F);
        Body.addChild(FrontRightLeg);
        FrontRightLeg.setTextureOffset(30, 0).addBox(-1.0F, 0.85F, 1.0F, 2.0F, 9.0F, 2.0F, 0.0F, false);

        FrontLeftLeg = new ModelRenderer(this);
        FrontLeftLeg.setRotationPoint(3.0F, 0.5F, -1.0F);
        Body.addChild(FrontLeftLeg);
        FrontLeftLeg.setTextureOffset(0, 31).addBox(-1.0F, 0.85F, 1.0F, 2.0F, 9.0F, 2.0F, 0.0F, false);

        BackRightLeg = new ModelRenderer(this);
        BackRightLeg.setRotationPoint(-2.5F, 1.6206F, 5.816F);
        Body.addChild(BackRightLeg);
        BackRightLeg.setTextureOffset(9, 28).addBox(-1.5F, -0.5F, -0.5F, 3.0F, 7.0F, 3.0F, 0.0F, false);

        BackLeftLeg = new ModelRenderer(this);
        BackLeftLeg.setRotationPoint(2.5F, 1.6206F, 5.816F);
        Body.addChild(BackLeftLeg);
        BackLeftLeg.setTextureOffset(21, 30).addBox(-1.5F, -0.5F, -0.5F, 3.0F, 7.0F, 3.0F, 0.0F, false);
    }

    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        if (this.isChild)
        {
            matrixStack.push();
            matrixStack.scale(0.56666666F, 0.56666666F, 0.56666666F);
            matrixStack.translate(0.0D, 1.35D, 0.15D);
            Body.render(matrixStack, buffer, packedLight, packedOverlay);
            matrixStack.pop();
        } else
        {
            Body.render(matrixStack, buffer, packedLight, packedOverlay);
        }
    }

    @Override
    public void setRotationAngles(JackalopeEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        this.Body.rotateAngleX = -0.3491F;

        this.Head.rotateAngleX = 0.3491F + (headPitch * ((float) Math.PI / 180F));
        this.Head.rotateAngleY = netHeadYaw * ((float) Math.PI / 180F);

        this.RightEar.rotateAngleX = 0.0873F;
        this.RightEar.rotateAngleY = 0.2618F;
        this.RightEar.rotateAngleZ = -0.3491F;

        this.LeftEar.rotateAngleX = 0.0873F;
        this.LeftEar.rotateAngleY = -0.2618F;
        this.LeftEar.rotateAngleZ = 0.3491F;

        this.FrontRightLeg.rotateAngleX = 0.3491F;
        this.FrontLeftLeg.rotateAngleX = 0.3491F;
        this.BackRightLeg.rotateAngleX = 0.3491F;
        this.BackLeftLeg.rotateAngleX = 0.3491F;
    }
}
