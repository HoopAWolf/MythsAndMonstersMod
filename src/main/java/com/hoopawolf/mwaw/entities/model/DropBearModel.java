package com.hoopawolf.mwaw.entities.model;

import com.hoopawolf.mwaw.entities.DropBearEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class DropBearModel extends EntityModel<DropBearEntity>
{
    private final ModelRenderer Body;
    private final ModelRenderer Face;
    private final ModelRenderer FrontRightLeg;
    private final ModelRenderer FrontLeftLeg;
    private final ModelRenderer BackLeftLeg;
    private final ModelRenderer BackRightLeg;

    public DropBearModel()
    {
        textureWidth = 64;
        textureHeight = 64;

        Body = new ModelRenderer(this);
        Body.setRotationPoint(0.0F, 15.5F, -0.25F);
        Body.setTextureOffset(0, 0).addBox(-5.0F, -3.6363F, -4.7147F, 10.0F, 6.0F, 11.0F, 0.0F, false);

        Face = new ModelRenderer(this);
        Face.setRotationPoint(0.0F, -3.5113F, -3.3397F);
        Body.addChild(Face);
        Face.setTextureOffset(0, 0).addBox(-2.0F, -2.125F, -7.375F, 4.0F, 5.0F, 1.0F, 0.0F, false);
        Face.setTextureOffset(0, 17).addBox(-4.0F, -4.125F, -6.375F, 8.0F, 8.0F, 6.0F, 0.0F, false);
        Face.setTextureOffset(36, 24).addBox(-7.0F, -7.125F, -3.375F, 6.0F, 6.0F, 1.0F, 0.0F, false);
        Face.setTextureOffset(28, 17).addBox(1.0F, -7.125F, -3.375F, 6.0F, 6.0F, 1.0F, 0.0F, false);

        FrontRightLeg = new ModelRenderer(this);
        FrontRightLeg.setRotationPoint(-5.0F, 0.9659F, -2.4912F);
        Body.addChild(FrontRightLeg);
        FrontRightLeg.setTextureOffset(0, 31).addBox(-2.0F, -0.5F, -2.0F, 4.0F, 9.0F, 4.0F, 0.0F, false);

        FrontLeftLeg = new ModelRenderer(this);
        FrontLeftLeg.setRotationPoint(5.0F, 0.9659F, -2.4912F);
        Body.addChild(FrontLeftLeg);
        FrontLeftLeg.setTextureOffset(24, 27).addBox(-2.0F, -0.5F, -2.0F, 4.0F, 9.0F, 4.0F, 0.0F, false);

        BackLeftLeg = new ModelRenderer(this);
        BackLeftLeg.setRotationPoint(4.0F, 1.0341F, 3.9912F);
        Body.addChild(BackLeftLeg);
        BackLeftLeg.setTextureOffset(31, 0).addBox(-2.0F, -0.5F, -2.0F, 4.0F, 7.0F, 4.0F, 0.0F, false);

        BackRightLeg = new ModelRenderer(this);
        BackRightLeg.setRotationPoint(-5.0F, 1.0341F, 3.9912F);
        Body.addChild(BackRightLeg);
        BackRightLeg.setTextureOffset(36, 36).addBox(-1.0F, -0.5F, -2.0F, 4.0F, 7.0F, 4.0F, 0.0F, false);
    }

    @Override
    public void setRotationAngles(DropBearEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        Body.rotateAngleX = -0.2618F;
        Face.rotateAngleX = 0.2618F;
        FrontRightLeg.rotateAngleX = 0.2618F;
        FrontLeftLeg.rotateAngleX = 0.2618F;
        BackLeftLeg.rotateAngleX = 0.2618F;
        BackRightLeg.rotateAngleX = 0.2618F;
    }

    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        Body.render(matrixStack, buffer, packedLight, packedOverlay);
    }
}