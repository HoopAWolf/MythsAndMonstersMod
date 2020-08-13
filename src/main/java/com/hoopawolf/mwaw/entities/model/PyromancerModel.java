package com.hoopawolf.mwaw.entities.model;

import com.hoopawolf.mwaw.entities.PyromancerEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class PyromancerModel extends EntityModel<PyromancerEntity>
{
    public final ModelRenderer Head;
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
        this.Head.rotateAngleX = (float) Math.toRadians(entityIn.getHeadRotation().getX());
        this.Head.rotateAngleY = (float) Math.toRadians(entityIn.getHeadRotation().getY());
        this.Head.rotateAngleZ = (float) Math.toRadians(entityIn.getHeadRotation().getZ());

        this.Body.rotateAngleX = (float) Math.toRadians(entityIn.getBodyRotation().getX());
        this.Body.rotateAngleY = (float) Math.toRadians(entityIn.getBodyRotation().getY());
        this.Body.rotateAngleZ = (float) Math.toRadians(entityIn.getBodyRotation().getZ());

        this.RightArm.rotateAngleX = (float) Math.toRadians(entityIn.getRightArmRotation().getX());
        this.RightArm.rotateAngleY = (float) Math.toRadians(entityIn.getRightArmRotation().getY());
        this.RightArm.rotateAngleZ = (float) Math.toRadians(entityIn.getRightArmRotation().getZ());

        this.LeftArm.rotateAngleX = (float) Math.toRadians(entityIn.getLeftArmRotation().getX());
        this.LeftArm.rotateAngleY = (float) Math.toRadians(entityIn.getLeftArmRotation().getY());
        this.LeftArm.rotateAngleZ = (float) Math.toRadians(entityIn.getLeftArmRotation().getZ());

        this.RightLeg.rotateAngleX = (float) Math.toRadians(entityIn.getRightLegRotation().getX());
        this.RightLeg.rotateAngleY = (float) Math.toRadians(entityIn.getRightLegRotation().getY());
        this.RightLeg.rotateAngleZ = (float) Math.toRadians(entityIn.getRightLegRotation().getZ());

        this.LeftLeg.rotateAngleX = (float) Math.toRadians(entityIn.getLeftLegRotation().getX());
        this.LeftLeg.rotateAngleY = (float) Math.toRadians(entityIn.getLeftLegRotation().getY());
        this.LeftLeg.rotateAngleZ = (float) Math.toRadians(entityIn.getLeftLegRotation().getZ());

        this.Head.rotateAngleY = Head.rotateAngleY + (netHeadYaw * ((float) Math.PI / 180F));
        this.Head.rotateAngleX = Head.rotateAngleX + (headPitch * ((float) Math.PI / 180F));

        if (!entityIn.isFlying())
        {
            this.RightArm.rotateAngleX = RightArm.rotateAngleX + MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount * 0.5F;
            this.LeftArm.rotateAngleX = LeftArm.rotateAngleX + MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
            this.RightLeg.rotateAngleX = RightLeg.rotateAngleX + MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
            this.LeftLeg.rotateAngleX = LeftLeg.rotateAngleX + MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        } else
        {
            this.RightArm.rotateAngleY = RightArm.rotateAngleY + (float) Math.toRadians(35);
            this.RightArm.rotateAngleZ = RightArm.rotateAngleZ + (float) Math.toRadians(12.5);
            this.LeftArm.rotateAngleY = LeftArm.rotateAngleY + (float) Math.toRadians(-35);
            this.LeftArm.rotateAngleZ = LeftArm.rotateAngleZ + (float) Math.toRadians(-12.5);

            this.RightLeg.rotateAngleX = RightLeg.rotateAngleX + (float) Math.toRadians(15);
            this.RightLeg.rotateAngleY = RightLeg.rotateAngleY + (float) Math.toRadians(10);
            this.LeftLeg.rotateAngleX = LeftLeg.rotateAngleX + (float) Math.toRadians(15);
            this.LeftLeg.rotateAngleY = LeftLeg.rotateAngleY + (float) Math.toRadians(-10);
        }

        entityIn.animation.animationTick(entityIn.getDataManager(), entityIn.getAnimationSpeed());
    }
}