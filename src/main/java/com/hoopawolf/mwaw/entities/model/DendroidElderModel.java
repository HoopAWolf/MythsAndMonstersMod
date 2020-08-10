package com.hoopawolf.mwaw.entities.model;

import com.hoopawolf.mwaw.entities.DendroidElderEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class DendroidElderModel extends EntityModel<DendroidElderEntity>
{
    private final ModelRenderer bb_main;
    private final ModelRenderer UpperBody;
    private final ModelRenderer Head;
    private final ModelRenderer RightArm;
    private final ModelRenderer RightArmJoint;
    private final ModelRenderer RightClaw;
    private final ModelRenderer LeftArm;
    private final ModelRenderer Mushroom;
    private final ModelRenderer LeftArmJoint;
    private final ModelRenderer LeftClaw;
    private final ModelRenderer RightLeg;
    private final ModelRenderer RightFoot;
    private final ModelRenderer LeftLeg;
    private final ModelRenderer LeftFoot;

    public DendroidElderModel()
    {
        textureWidth = 128;
        textureHeight = 128;

        UpperBody = new ModelRenderer(this);
        UpperBody.setRotationPoint(-0.3333F, 1.1667F, -0.5F);
        UpperBody.setTextureOffset(74, 74).addBox(-6.6667F, -28.1667F, 6.5F, 6.0F, 17.0F, 6.0F, 0.0F, false);
        UpperBody.setTextureOffset(0, 0).addBox(-8.6667F, -21.1667F, -3.5F, 18.0F, 13.0F, 10.0F, 0.0F, false);
        UpperBody.setTextureOffset(0, 39).addBox(-7.6667F, -9.1667F, -2.5F, 16.0F, 9.0F, 7.0F, 0.0F, false);

        bb_main = new ModelRenderer(this);
        bb_main.setRotationPoint(0.3333F, 22.8333F, -0.5F);
        UpperBody.addChild(bb_main);
        bb_main.setTextureOffset(36, 45).addBox(-4.0F, -40.0F, -6.0F, 8.0F, 8.0F, 10.0F, 0.0F, false);

        Head = new ModelRenderer(this);
        Head.setRotationPoint(0.3333F, -22.9167F, -1.25F);
        UpperBody.addChild(Head);
        Head.setTextureOffset(0, 23).addBox(-12.0F, -15.25F, -1.25F, 24.0F, 16.0F, 0.0F, 0.0F, false);
        Head.setTextureOffset(0, 41).addBox(0.0F, -16.25F, -5.25F, 0.0F, 15.0F, 14.0F, 0.0F, false);
        Head.setTextureOffset(72, 52).addBox(-4.0F, -7.25F, -6.25F, 8.0F, 9.0F, 8.0F, 0.0F, false);

        RightArm = new ModelRenderer(this);
        RightArm.setRotationPoint(-8.6667F, -19.5417F, 1.8333F);
        UpperBody.addChild(RightArm);
        RightArm.setTextureOffset(12, 94).addBox(-6.0F, -2.625F, -1.3333F, 6.0F, 13.0F, 5.0F, 0.0F, false);

        RightArmJoint = new ModelRenderer(this);
        RightArmJoint.setRotationPoint(-3.0F, 9.375F, 0.6667F);
        RightArm.addChild(RightArmJoint);
        RightArmJoint.setTextureOffset(42, 83).addBox(-2.0F, 1.0F, -2.0F, 4.0F, 18.0F, 5.0F, 0.0F, false);

        RightClaw = new ModelRenderer(this);
        RightClaw.setRotationPoint(0.0F, 12.0F, 0.0F);
        RightArmJoint.addChild(RightClaw);
        RightClaw.setTextureOffset(0, 70).addBox(-4.0F, -8.0F, -5.0F, 2.0F, 28.0F, 4.0F, 0.0F, false);
        RightClaw.setTextureOffset(20, 62).addBox(-4.0F, -9.0F, 0.0F, 3.0F, 24.0F, 8.0F, 0.0F, false);

        LeftArm = new ModelRenderer(this);
        LeftArm.setRotationPoint(9.3333F, -19.5417F, 1.25F);
        UpperBody.addChild(LeftArm);
        LeftArm.setTextureOffset(96, 40).addBox(0.0F, -2.625F, -0.75F, 6.0F, 13.0F, 5.0F, 0.0F, false);

        Mushroom = new ModelRenderer(this);
        Mushroom.setRotationPoint(-9.0F, 42.375F, -1.75F);
        LeftArm.addChild(Mushroom);
        Mushroom.setTextureOffset(92, 69).addBox(6.0F, -52.0F, 5.0F, 10.0F, 7.0F, 0.0F, 0.0F, false);
        Mushroom.setTextureOffset(92, 59).addBox(11.0F, -52.0F, 0.0F, 0.0F, 7.0F, 10.0F, 0.0F, false);

        LeftArmJoint = new ModelRenderer(this);
        LeftArmJoint.setRotationPoint(3.0F, 10.375F, 2.25F);
        LeftArm.addChild(LeftArmJoint);
        LeftArmJoint.setTextureOffset(42, 83).addBox(-2.0F, 0.0F, -3.0F, 4.0F, 18.0F, 5.0F, 0.0F, false);

        LeftClaw = new ModelRenderer(this);
        LeftClaw.setRotationPoint(1.0F, 11.0F, 0.0F);
        LeftArmJoint.addChild(LeftClaw);
        LeftClaw.setTextureOffset(20, 62).addBox(1.0F, -9.0F, -1.0F, 3.0F, 24.0F, 8.0F, 0.0F, false);
        LeftClaw.setTextureOffset(0, 70).addBox(1.0F, -8.0F, -6.0F, 2.0F, 28.0F, 4.0F, 0.0F, false);

        RightLeg = new ModelRenderer(this);
        RightLeg.setRotationPoint(-4.5F, 1.25F, 0.0F);
        RightLeg.setTextureOffset(92, 92).addBox(-3.5F, -0.25F, -3.0F, 6.0F, 11.0F, 6.0F, 0.0F, false);

        RightFoot = new ModelRenderer(this);
        RightFoot.setRotationPoint(-0.5F, 10.75F, 1.0F);
        RightLeg.addChild(RightFoot);
        RightFoot.setTextureOffset(42, 63).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 12.0F, 8.0F, 0.0F, false);

        LeftLeg = new ModelRenderer(this);
        LeftLeg.setRotationPoint(5.5F, 1.25F, 0.0F);
        LeftLeg.setTextureOffset(92, 92).addBox(-3.5F, -0.25F, -3.0F, 6.0F, 11.0F, 6.0F, 0.0F, false);

        LeftFoot = new ModelRenderer(this);
        LeftFoot.setRotationPoint(-0.5F, 10.75F, 1.0F);
        LeftLeg.addChild(LeftFoot);
        LeftFoot.setTextureOffset(42, 63).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 12.0F, 8.0F, 0.0F, false);
    }

    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder iVertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
    {
        UpperBody.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
        RightLeg.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
        LeftLeg.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
    }

    @Override
    public void setRotationAngles(DendroidElderEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        this.Head.rotateAngleX = (float) Math.toRadians(entityIn.getHeadRotation().getX());
        this.Head.rotateAngleY = (float) Math.toRadians(entityIn.getHeadRotation().getY());
        this.Head.rotateAngleZ = (float) Math.toRadians(entityIn.getHeadRotation().getZ());

        this.UpperBody.rotateAngleX = (float) Math.toRadians(entityIn.getBodyRotation().getX());
        this.UpperBody.rotateAngleY = (float) Math.toRadians(entityIn.getBodyRotation().getY());
        this.UpperBody.rotateAngleZ = (float) Math.toRadians(entityIn.getBodyRotation().getZ());

        this.RightArm.rotateAngleX = (float) Math.toRadians(entityIn.getRightArmRotation().getX());
        this.RightArm.rotateAngleY = (float) Math.toRadians(entityIn.getRightArmRotation().getY());
        this.RightArm.rotateAngleZ = (float) Math.toRadians(entityIn.getRightArmRotation().getZ());
        this.RightArmJoint.rotateAngleX = (float) Math.toRadians(entityIn.getRightJointRotation().getX());
        this.RightArmJoint.rotateAngleY = (float) Math.toRadians(entityIn.getRightJointRotation().getY());
        this.RightArmJoint.rotateAngleZ = (float) Math.toRadians(entityIn.getRightJointRotation().getZ());

        this.LeftArm.rotateAngleX = (float) Math.toRadians(entityIn.getLeftArmRotation().getX());
        this.LeftArm.rotateAngleY = (float) Math.toRadians(entityIn.getLeftArmRotation().getY());
        this.LeftArm.rotateAngleZ = (float) Math.toRadians(entityIn.getLeftArmRotation().getZ());
        this.LeftArmJoint.rotateAngleX = (float) Math.toRadians(entityIn.getLeftJointRotation().getX());
        this.LeftArmJoint.rotateAngleY = (float) Math.toRadians(entityIn.getLeftJointRotation().getY());
        this.LeftArmJoint.rotateAngleZ = (float) Math.toRadians(entityIn.getLeftJointRotation().getZ());

        this.RightLeg.rotateAngleX = (float) Math.toRadians(entityIn.getRightLegRotation().getX());
        this.RightLeg.rotateAngleY = (float) Math.toRadians(entityIn.getRightLegRotation().getY());
        this.RightLeg.rotateAngleZ = (float) Math.toRadians(entityIn.getRightLegRotation().getZ());
        this.RightFoot.rotateAngleX = (float) Math.toRadians(entityIn.getRightFootRotation().getX());
        this.RightFoot.rotateAngleY = (float) Math.toRadians(entityIn.getRightFootRotation().getY());
        this.RightFoot.rotateAngleZ = (float) Math.toRadians(entityIn.getRightFootRotation().getZ());

        this.LeftLeg.rotateAngleX = (float) Math.toRadians(entityIn.getLeftLegRotation().getX());
        this.LeftLeg.rotateAngleY = (float) Math.toRadians(entityIn.getLeftLegRotation().getY());
        this.LeftLeg.rotateAngleZ = (float) Math.toRadians(entityIn.getLeftLegRotation().getZ());
        this.LeftFoot.rotateAngleX = (float) Math.toRadians(entityIn.getLeftFootRotation().getX());
        this.LeftFoot.rotateAngleY = (float) Math.toRadians(entityIn.getLeftFootRotation().getY());
        this.LeftFoot.rotateAngleZ = (float) Math.toRadians(entityIn.getLeftFootRotation().getZ());

        if (entityIn.getState() == 0)
        {
            this.Head.rotateAngleY = Head.rotateAngleY + netHeadYaw * ((float) Math.PI / 180F);
            this.Head.rotateAngleX = Head.rotateAngleX + headPitch * ((float) Math.PI / 180F);

            this.RightArm.rotateAngleX = RightArm.rotateAngleX + MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount * 0.5F;
            this.LeftArm.rotateAngleX = LeftArm.rotateAngleX + MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
            if (!entityIn.isDefensiveMode())
            {
                this.RightArmJoint.rotateAngleX = RightArmJoint.rotateAngleX + MathHelper.clamp(MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount, -0.5F, 0);
            }
            this.LeftArmJoint.rotateAngleX = LeftArmJoint.rotateAngleX + MathHelper.clamp(MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount, -0.5F, 0);
            this.RightLeg.rotateAngleX = RightLeg.rotateAngleX + MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
            this.LeftLeg.rotateAngleX = LeftLeg.rotateAngleX + MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
            this.RightFoot.rotateAngleX = RightFoot.rotateAngleX + MathHelper.clamp(MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount, 0, 0.5F);
            this.LeftFoot.rotateAngleX = LeftFoot.rotateAngleX + MathHelper.clamp(MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount, 0, 0.5F);
        }

        entityIn.animation.animationTick(entityIn.getDataManager(), entityIn.getAnimationSpeed());
    }
}