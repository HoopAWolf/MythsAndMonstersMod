package com.hoopawolf.mwaw.entities.model;

import com.hoopawolf.mwaw.entities.KitsuneEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KitsuneModel extends EntityModel<KitsuneEntity>
{
    public final ModelRenderer Head;
    protected final ModelRenderer field_217152_f;
    protected final ModelRenderer villagerBody;
    protected final ModelRenderer field_217153_h;
    protected final ModelRenderer villagerArms;
    protected final ModelRenderer rightVillagerLeg;
    protected final ModelRenderer leftVillagerLeg;
    protected final ModelRenderer villagerNose;
    private final ModelRenderer Body;
    private final ModelRenderer RightTail;
    private final ModelRenderer LeftTail;
    private final ModelRenderer CenterTail;
    private final ModelRenderer FrontRightLeg;
    private final ModelRenderer FrontLeftLeg;
    private final ModelRenderer BackLeftLeg;
    private final ModelRenderer BackRightLeg;
    //Villager
    protected ModelRenderer villagerHead;
    protected ModelRenderer field_217151_b;

    public KitsuneModel()
    {
        textureWidth = 128;
        textureHeight = 128;

        Body = new ModelRenderer(this);
        Body.setRotationPoint(0.0F, 24.0F, 0.0F);
        Body.addBox("Body", -4.0F, -13.0F, -8.0F, 8, 6, 11, 0.0F, 0, 0);
        Body.addBox("Body", -5.0F, -14.0F, -8.5F, 10, 6, 5, 0.0F, 0, 17);

        RightTail = new ModelRenderer(this);
        RightTail.setRotationPoint(-3.0F, 14.5F, 3.0F);
        RightTail.rotateAngleX = 0.5236F;
        RightTail.rotateAngleZ = -0.3491F;
        RightTail.addBox("RightTail", -2.316F, -4.2033F, 0.7943F, 4, 5, 9, 0.0F, 0, 28);

        LeftTail = new ModelRenderer(this);
        LeftTail.setRotationPoint(2.0F, 14.5F, 3.0F);
        LeftTail.rotateAngleX = 0.6109F;
        LeftTail.rotateAngleZ = 0.5236F;
        LeftTail.addBox("LeftTail", -2.0F, -4.2006F, 1.2807F, 4, 5, 9, 0.0F, 21, 21);

        CenterTail = new ModelRenderer(this);
        CenterTail.setRotationPoint(0.0F, 12.5F, 3.0F);
        CenterTail.rotateAngleX = 0.8727F;
        CenterTail.addBox("CenterTail", -2.0F, -1.5F, 0.0F, 4, 5, 9, 0.0F, 17, 35);

        Head = new ModelRenderer(this);
        Head.setRotationPoint(0.0F, 11.5F, -8.375F);
        Head.addBox("Head", -4.0F, -5.5F, -3.625F, 2, 2, 1, 0.0F, 27, 5);
        Head.addBox("Head", 2.0F, -5.5F, -3.625F, 2, 2, 1, 0.0F, 25, 17);
        Head.addBox("Head", -4.0F, -3.5F, -5.625F, 8, 6, 6, 0.0F, 38, 0);
        Head.addBox("Head", -2.0F, 0.5F, -8.625F, 4, 2, 3, 0.0F, 27, 0);

        FrontRightLeg = new ModelRenderer(this);
        FrontRightLeg.setRotationPoint(-2.0F, 16.5F, -7.0F);
        FrontRightLeg.addBox("FrontRightLeg", -1.0F, 0.5F, -1.0F, 2, 7, 2, 0.0F, 38, 12);

        FrontLeftLeg = new ModelRenderer(this);
        FrontLeftLeg.setRotationPoint(2.0F, 16.5F, -7.0F);
        FrontLeftLeg.addBox("FrontLeftLeg", -1.0F, 0.5F, -1.0F, 2, 7, 2, 0.0F, 34, 35);

        BackLeftLeg = new ModelRenderer(this);
        BackLeftLeg.setRotationPoint(2.0F, 16.5F, 2.0F);
        BackLeftLeg.addBox("BackLeftLeg", -1.0F, 0.5F, -1.0F, 2, 7, 2, 0.0F, 0, 28);

        BackRightLeg = new ModelRenderer(this);
        BackRightLeg.setRotationPoint(-2.0F, 16.5F, 2.0F);
        BackRightLeg.addBox("BackRightLeg", -1.0F, 0.5F, -1.0F, 2, 7, 2, 0.0F, 0, 0);

        //villager
        this.villagerHead = (new ModelRenderer(this)).setTextureSize(64, 64);
        this.villagerHead.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.villagerHead.setTextureOffset(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, 0.0F);
        this.field_217151_b = (new ModelRenderer(this)).setTextureSize(64, 64);
        this.field_217151_b.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.field_217151_b.setTextureOffset(32, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, 0.0F + 0.5F);
        this.villagerHead.addChild(this.field_217151_b);
        this.field_217152_f = (new ModelRenderer(this)).setTextureSize(64, 64);
        this.field_217152_f.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.field_217152_f.setTextureOffset(30, 47).addBox(-8.0F, -8.0F, -6.0F, 16.0F, 16.0F, 1.0F, 64);
        this.field_217152_f.rotateAngleX = (-(float) Math.PI / 2F);
        this.field_217151_b.addChild(this.field_217152_f);
        this.villagerNose = (new ModelRenderer(this)).setTextureSize(64, 64);
        this.villagerNose.setRotationPoint(0.0F, -2.0F, 0.0F);
        this.villagerNose.setTextureOffset(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F, 0.0F);
        this.villagerHead.addChild(this.villagerNose);
        this.villagerBody = (new ModelRenderer(this)).setTextureSize(64, 64);
        this.villagerBody.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.villagerBody.setTextureOffset(16, 20).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, 0.0F);
        this.field_217153_h = (new ModelRenderer(this)).setTextureSize(64, 64);
        this.field_217153_h.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.field_217153_h.setTextureOffset(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 18.0F, 6.0F, 0.0F + 0.5F);
        this.villagerBody.addChild(this.field_217153_h);
        this.villagerArms = (new ModelRenderer(this)).setTextureSize(64, 64);
        this.villagerArms.setRotationPoint(0.0F, 2.0F, 0.0F);
        this.villagerArms.setTextureOffset(44, 22).addBox(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.0F);
        this.villagerArms.setTextureOffset(44, 22).addBox(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.0F, true);
        this.villagerArms.setTextureOffset(40, 38).addBox(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F, 0.0F);
        this.rightVillagerLeg = (new ModelRenderer(this, 0, 22)).setTextureSize(64, 64);
        this.rightVillagerLeg.setRotationPoint(-2.0F, 12.0F, 0.0F);
        this.rightVillagerLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F);
        this.leftVillagerLeg = (new ModelRenderer(this, 0, 22)).setTextureSize(64, 64);
        this.leftVillagerLeg.mirror = true;
        this.leftVillagerLeg.setRotationPoint(2.0F, 12.0F, 0.0F);
        this.leftVillagerLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F);
    }

    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder iVertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
    {
        Body.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
        RightTail.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, 0.6F);
        LeftTail.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, 0.3F);
        CenterTail.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, 0.6F);
        Head.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
        FrontRightLeg.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
        FrontLeftLeg.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
        BackLeftLeg.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
        BackRightLeg.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);

        //villager
        villagerHead.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
        villagerBody.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
        villagerArms.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
        rightVillagerLeg.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
        leftVillagerLeg.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
    }

    @Override
    public void setLivingAnimations(KitsuneEntity entityIn, float limbSwing, float limbSwingAmount, float partialTick)
    {
        if (entityIn.isVillagerForm())
        {
            showFoxModel(false, entityIn.getFoxPhase());
            showVillagerModel(true);
        } else
        {
            showFoxModel(true, entityIn.getFoxPhase());
            showVillagerModel(false);
        }
    }

    @Override
    public void setRotationAngles(KitsuneEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        if (entityIn.isShouting())
        {
            this.Head.rotateAngleX = -45.0F;
        } else
        {
            this.Head.rotateAngleX = headPitch * ((float) Math.PI / 180F);
            this.Head.rotateAngleY = netHeadYaw * ((float) Math.PI / 180F);
        }

        this.FrontRightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.FrontLeftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        this.BackRightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        this.BackLeftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;

        if (Entity.horizontalMag(entityIn.getMotion()) > 0.0001D)
        {
            this.CenterTail.rotateAngleX = -0.5727F;
            this.CenterTail.rotateAngleY = 0.0F;
            this.CenterTail.rotateAngleZ = 0.0F;

            this.LeftTail.rotateAngleX = -0.5109F;
            this.LeftTail.rotateAngleY = 0.0F;
            this.LeftTail.rotateAngleZ = 0.5236F;

            this.RightTail.rotateAngleX = -0.4236F;
            this.RightTail.rotateAngleY = 0.0F;
            this.RightTail.rotateAngleZ = -0.3491F;
        } else
        {
            this.CenterTail.rotateAngleX = 0.8727F + MathHelper.cos(ageInTicks * 0.06F) * (float) Math.PI * 0.01F;
            this.CenterTail.rotateAngleY = MathHelper.cos(ageInTicks * -0.06F) * (float) Math.PI * 0.01F;
            this.CenterTail.rotateAngleZ = MathHelper.cos(ageInTicks * -0.06F) * (float) Math.PI * 0.01F;

            this.LeftTail.rotateAngleX = 0.6109F - MathHelper.cos(ageInTicks * -0.06F) * (float) Math.PI * 0.01F;
            this.LeftTail.rotateAngleY = MathHelper.cos(ageInTicks * -0.06F) * (float) Math.PI * 0.01F;
            this.LeftTail.rotateAngleZ = 0.5236F + MathHelper.cos(ageInTicks * -0.06F) * (float) Math.PI * 0.01F;

            this.RightTail.rotateAngleX = 0.5236F + MathHelper.cos(ageInTicks * 0.06F) * (float) Math.PI * 0.01F;
            this.RightTail.rotateAngleY = MathHelper.cos(ageInTicks * 0.06F) * (float) Math.PI * 0.01F;
            this.RightTail.rotateAngleZ = -0.3491F - MathHelper.cos(ageInTicks * -0.06F) * (float) Math.PI * 0.01F;
        }

        //villager
        this.villagerHead.rotateAngleY = netHeadYaw * ((float) Math.PI / 180F);
        this.villagerHead.rotateAngleX = headPitch * ((float) Math.PI / 180F);


        boolean flag = entityIn.getShakeHeadTicks() > 0;

        if (flag)
        {
            this.villagerHead.rotateAngleZ = 0.3F * MathHelper.sin(0.45F * ageInTicks);
            this.villagerHead.rotateAngleX = 0.4F;
        } else
        {
            this.villagerHead.rotateAngleZ = 0.0F;
        }

        this.villagerArms.rotationPointY = 3.0F;
        this.villagerArms.rotationPointZ = -1.0F;
        this.villagerArms.rotateAngleX = -0.75F;
        this.rightVillagerLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
        this.leftVillagerLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount * 0.5F;
        this.rightVillagerLeg.rotateAngleY = 0.0F;
        this.leftVillagerLeg.rotateAngleY = 0.0F;
    }

    private void showFoxModel(boolean _isShowing, int foxPhase)
    {
        Body.showModel = _isShowing;
        RightTail.showModel = (foxPhase > 2) && _isShowing;
        LeftTail.showModel = (foxPhase > 1) && _isShowing;
        CenterTail.showModel = (foxPhase > 0) && _isShowing;
        Head.showModel = _isShowing;
        FrontRightLeg.showModel = _isShowing;
        FrontLeftLeg.showModel = _isShowing;
        BackLeftLeg.showModel = _isShowing;
        BackRightLeg.showModel = _isShowing;
    }

    private void showVillagerModel(boolean _isShowing)
    {
        villagerHead.showModel = _isShowing;
        villagerBody.showModel = _isShowing;
        villagerArms.showModel = _isShowing;
        rightVillagerLeg.showModel = _isShowing;
        leftVillagerLeg.showModel = _isShowing;
    }
}