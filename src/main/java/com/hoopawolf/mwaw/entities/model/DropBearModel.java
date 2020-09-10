package com.hoopawolf.mwaw.entities.model;

import com.hoopawolf.mwaw.entities.DropBearEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

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
    public void setRotationAngles(DropBearEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        Body.rotateAngleX = -0.2618F;
        Body.rotateAngleY = 0.0F;
        Body.rotateAngleZ = 0.0F;
        Face.rotateAngleX = 0.2618F;
        Face.rotateAngleY = 0.0F;
        Face.rotateAngleZ = 0.0F;
        FrontRightLeg.rotateAngleX = 0.2618F;
        FrontRightLeg.rotateAngleY = 0.0F;
        FrontRightLeg.rotateAngleZ = 0.0F;
        FrontLeftLeg.rotateAngleX = 0.2618F;
        FrontLeftLeg.rotateAngleY = 0.0F;
        FrontLeftLeg.rotateAngleZ = 0.0F;
        BackLeftLeg.rotateAngleX = 0.2618F;
        BackLeftLeg.rotateAngleY = 0.0F;
        BackLeftLeg.rotateAngleZ = 0.0F;
        BackRightLeg.rotateAngleX = 0.2618F;
        BackRightLeg.rotateAngleY = 0.0F;
        BackRightLeg.rotateAngleZ = 0.0F;

        if (!entityIn.isHugging())
        {
            this.Face.rotateAngleX = this.Face.rotateAngleX + (headPitch * ((float) Math.PI / 180F)) + (float) Math.toRadians(entityIn.getHeadRotation().getX());
            this.Face.rotateAngleY = (netHeadYaw * (((float) Math.PI / 180F))) + (float) Math.toRadians(entityIn.getHeadRotation().getY());
        } else
        {
            this.Face.rotateAngleX = this.Face.rotateAngleX + (float) Math.toRadians(entityIn.getHeadRotation().getX());
            this.Face.rotateAngleY = (float) Math.toRadians(entityIn.getHeadRotation().getY());
        }

        this.Face.rotateAngleZ = (float) Math.toRadians(entityIn.getHeadRotation().getZ());

        this.Body.rotateAngleX = this.Body.rotateAngleX + (float) Math.toRadians(entityIn.getBodyRotation().getX());
        this.Body.rotateAngleY = (float) Math.toRadians(entityIn.getBodyRotation().getY());
        this.Body.rotateAngleZ = (float) Math.toRadians(entityIn.getBodyRotation().getZ());

        this.FrontRightLeg.rotateAngleX = (entityIn.grabbedTarget() ? 0 : MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount) + this.FrontRightLeg.rotateAngleX + (float) Math.toRadians(entityIn.getRightArmRotation().getX());
        this.FrontRightLeg.rotateAngleY = (float) Math.toRadians(entityIn.getRightArmRotation().getY());
        this.FrontRightLeg.rotateAngleZ = (float) Math.toRadians(entityIn.getRightArmRotation().getZ());

        this.FrontLeftLeg.rotateAngleX = (entityIn.grabbedTarget() ? 0 : MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount) + this.FrontLeftLeg.rotateAngleX + (float) Math.toRadians(entityIn.getLeftArmRotation().getX());
        this.FrontLeftLeg.rotateAngleY = (float) Math.toRadians(entityIn.getLeftArmRotation().getY());
        this.FrontLeftLeg.rotateAngleZ = (float) Math.toRadians(entityIn.getLeftArmRotation().getZ());

        this.BackRightLeg.rotateAngleX = (entityIn.grabbedTarget() ? 0 : MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount) + this.BackRightLeg.rotateAngleX + (float) Math.toRadians(entityIn.getRightLegRotation().getX());
        this.BackRightLeg.rotateAngleY = (float) Math.toRadians(entityIn.getRightLegRotation().getY());
        this.BackRightLeg.rotateAngleZ = (float) Math.toRadians(entityIn.getRightLegRotation().getZ());

        this.BackLeftLeg.rotateAngleX = (entityIn.grabbedTarget() ? 0 : MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount) + this.BackLeftLeg.rotateAngleX + (float) Math.toRadians(entityIn.getLeftLegRotation().getX());
        this.BackLeftLeg.rotateAngleY = (float) Math.toRadians(entityIn.getLeftLegRotation().getY());
        this.BackLeftLeg.rotateAngleZ = (float) Math.toRadians(entityIn.getLeftLegRotation().getZ());

        entityIn.animation.animationTick(entityIn.getDataManager(), entityIn.getAnimationSpeed());
    }

    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        Body.render(matrixStack, buffer, packedLight, packedOverlay);
    }
}