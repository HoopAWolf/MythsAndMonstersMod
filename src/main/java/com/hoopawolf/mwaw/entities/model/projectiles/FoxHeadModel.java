package com.hoopawolf.mwaw.entities.model.projectiles;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class FoxHeadModel<T extends Entity> extends SegmentedModel<T>
{

    private final ModelRenderer Head;

    public FoxHeadModel()
    {
        textureWidth = 128;
        textureHeight = 128;

        Head = new ModelRenderer(this);
        Head.setRotationPoint(0.0F, 0.0F, 0.0F);
        Head.addBox("Head", -4.0F, -5.5F, -3.625F, 2, 2, 1, 0.0F, 27, 5);
        Head.addBox("Head", 2.0F, -5.5F, -3.625F, 2, 2, 1, 0.0F, 25, 17);
        Head.addBox("Head", -4.0F, -3.5F, -5.625F, 8, 6, 6, 0.0F, 38, 0);
        Head.addBox("Head", -2.0F, 0.5F, -8.625F, 4, 2, 3, 0.0F, 27, 0);
    }

    @Override
    public Iterable<ModelRenderer> getParts()
    {
        return ImmutableList.of(this.Head);
    }


    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        this.Head.rotateAngleX = headPitch * ((float) Math.PI / 180F);
        this.Head.rotateAngleY = netHeadYaw * ((float) Math.PI / 180F);

    }
}
