package com.hoopawolf.mwaw.entities.model;

import com.hoopawolf.mwaw.entities.SandWyrmEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;

@OnlyIn(Dist.CLIENT)
public class SandWyrmModel extends EntityModel<SandWyrmEntity>
{
    private final ModelRenderer Head;
    private final ModelRenderer Mouth;
    private final ModelRenderer TuskRight;
    private final ModelRenderer TuskLeft;
    private final ModelRenderer Body1;
    private final ModelRenderer Body2;
    private final ModelRenderer Tail1;
    private final ModelRenderer Tail2;
    private final ModelRenderer Tail3;
    private final ModelRenderer EndTail;
    private final ModelRenderer LeftFrontLeg;
    private final ModelRenderer LeftBackLeg;
    private final ModelRenderer RightFrontLeg;
    private final ModelRenderer RightBackLeg;

    private final ArrayList<ModelRenderer> modelParts = new ArrayList<ModelRenderer>();

    public SandWyrmModel()
    {
        textureWidth = 123;
        textureHeight = 121;

        Head = new ModelRenderer(this);
        Head.setRotationPoint(0.0F, 14.0F, -20.0F);
        Head.addBox("Head", -7.0F, -3.0F, -9.0F, 14, 9, 5, 0.0F, 73, 91);
        Head.addBox("Head", -9.0F, -6.0F, -21.0F, 18, 5, 21, 0.0F, 0, 58);

        Mouth = new ModelRenderer(this);
        Mouth.setRotationPoint(0.0F, 4.0F, -7.0F);
        Mouth.rotateAngleX = -0.2618F;
        Mouth.rotateAngleY = 0.0F;
        Mouth.rotateAngleZ = 0.0F;
        Head.addChild(Mouth);
        Mouth.addBox("Mouth", -6.0F, -1.1603F, -12.5484F, 12, 3, 12, 0.0F, 0, 84);

        TuskRight = new ModelRenderer(this);
        TuskRight.setRotationPoint(7.0F, 1.0F, 9.0F);
        Head.addChild(TuskRight);
        TuskRight.addBox("TuskRight", -15.0F, -2.0F, -32.0F, 2, 4, 17, 0.0F, 31, 84);

        TuskLeft = new ModelRenderer(this);
        TuskLeft.setRotationPoint(-7.0F, 1.0F, 8.0F);
        Head.addChild(TuskLeft);
        TuskLeft.addBox("TuskLeft", 13.0F, -2.0F, -31.0F, 2, 4, 17, 0.0F, 52, 91);

        Body1 = new ModelRenderer(this);
        Body1.setRotationPoint(0.0F, 14.0F, -20.0F);
        Body1.addBox("Body1", -12.0F, -4.0F, -4.0F, 24, 11, 19, 0.0F, 0, 0);
        modelParts.add(Body1);

        LeftFrontLeg = new ModelRenderer(this);
        LeftFrontLeg.setRotationPoint(14.0F, 1.0F, -0.5F);
        Body1.addChild(LeftFrontLeg);
        LeftFrontLeg.addBox("LeftFrontLeg", -2.0F, -1.0F, -2.5F, 5, 10, 7, 0.0F, 0, 99);

        RightFrontLeg = new ModelRenderer(this);
        RightFrontLeg.setRotationPoint(-14.0F, 1.0F, -0.5F);
        Body1.addChild(RightFrontLeg);
        RightFrontLeg.addBox("RightFrontLeg", -3.0F, -1.0F, -2.5F, 5, 10, 7, 0.0F, 0, 99);

        Body2 = new ModelRenderer(this);
        Body2.setRotationPoint(0.0F, 2.0F, 13.0F);
        Body1.addChild(Body2);
        Body2.addBox("Body2", -11.0F, -5.0F, 2.0F, 22, 10, 18, 0.0F, 0, 30);
        modelParts.add(Body2);

        LeftBackLeg = new ModelRenderer(this);
        LeftBackLeg.setRotationPoint(-11.0F, 2.0F, -41.5F);
        Body2.addChild(LeftBackLeg);
        LeftBackLeg.addBox("LeftBackLeg", 22.0F, -1.0F, 53.5F, 5, 7, 7, 0.0F, 24, 105);

        RightBackLeg = new ModelRenderer(this);
        RightBackLeg.setRotationPoint(11.0F, 2.0F, -41.5F);
        Body2.addChild(RightBackLeg);
        RightBackLeg.addBox("RightBackLeg", -27.0F, -1.0F, 53.5F, 5, 7, 7, 0.0F, 24, 105);

        Tail1 = new ModelRenderer(this);
        Tail1.setRotationPoint(0.0F, 0.0F, 18.0F);
        Body2.addChild(Tail1);
        Tail1.addBox("Tail1", -9.0F, -4.0F, 2.0F, 18, 8, 9, 0.0F, 67, 0);
        modelParts.add(Tail1);

        Tail2 = new ModelRenderer(this);
        Tail2.setRotationPoint(0.0F, 0.0F, 9.0F);
        Tail1.addChild(Tail2);
        Tail2.addBox("Tail2", -9.0F, -4.0F, 2.0F, 18, 7, 10, 0.0F, 57, 58);
        modelParts.add(Tail2);

        Tail3 = new ModelRenderer(this);
        Tail3.setRotationPoint(0.0F, 0.0F, 10.0F);
        Tail2.addChild(Tail3);
        Tail3.addBox("Tail3", -8.0F, -4.0F, 2.0F, 16, 6, 10, 0.0F, 68, 75);
        modelParts.add(Tail3);

        EndTail = new ModelRenderer(this);
        EndTail.setRotationPoint(0.0F, 0.0F, 9.0F);
        Tail3.addChild(EndTail);
        EndTail.addBox("EndTail", -14.0F, -1.0F, -1.0F, 28, 0, 16, 0.0F, 46, 30);
        modelParts.add(EndTail);
    }

    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder iVertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
    {
        Head.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
        Body1.render(matrixStack, iVertexBuilder, packedLightIn, packedOverlayIn);
    }

    @Override
    public void setLivingAnimations(SandWyrmEntity entityIn, float limbSwing, float limbSwingAmount, float partialTick)
    {
        int i = entityIn.getAttackTimer();
        if (i > 0)
        {
            this.Mouth.rotateAngleX = 0.2618F - (-2.0F + 1.5F * this.triangleWave((float) i - partialTick, 10.0F));
        } else
        {
            this.Mouth.rotateAngleX = -0.2618F;
        }
    }

    @Override
    public void setRotationAngles(SandWyrmEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        float rotation;

        switch (entityIn.getRotation())
        {
            case 1:
                rotation = -0.7F;
                break;
            case 2:
                rotation = 0.7F;
                break;
            default:
                rotation = 0.0F;
                break;
        }

        Head.rotateAngleY = netHeadYaw * ((float) Math.PI / 180F);
        Head.rotateAngleX = (headPitch * ((float) Math.PI / 180F)) + rotation;

        for (int i = 0; i < entityIn.getAllRotateX().length; ++i)
        {
            this.modelParts.get(i).rotateAngleX = entityIn.getAllRotateX()[i] + ((i == 0) ? rotation : 0.0F);
        }

        this.LeftFrontLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 2.4F * limbSwingAmount;
        this.RightFrontLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.4F * limbSwingAmount;
    }

    private float triangleWave(float p_78172_1_, float p_78172_2_)
    {
        return (Math.abs(p_78172_1_ % p_78172_2_ - p_78172_2_ * 0.5F) - p_78172_2_ * 0.25F) / (p_78172_2_ * 0.25F);
    }
}