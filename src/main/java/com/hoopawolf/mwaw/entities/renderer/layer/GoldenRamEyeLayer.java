package com.hoopawolf.mwaw.entities.renderer.layer;

import com.hoopawolf.mwaw.entities.GoldenRamEntity;
import com.hoopawolf.mwaw.entities.model.GoldenRamModel;
import com.hoopawolf.mwaw.ref.Reference;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.AbstractEyesLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GoldenRamEyeLayer extends AbstractEyesLayer<GoldenRamEntity, GoldenRamModel>
{
    private static final RenderType RENDER_TYPE = RenderType.getEyes(new ResourceLocation(Reference.MOD_ID, "textures/entity/goldenrameye.png"));
    private static final RenderType RENDER_ANGRY_TYPE = RenderType.getEyes(new ResourceLocation(Reference.MOD_ID, "textures/entity/goldenrameyeangry.png"));
    private static RenderType RENDER_TYPE_FINAL;

    public GoldenRamEyeLayer(IEntityRenderer<GoldenRamEntity, GoldenRamModel> rendererIn)
    {
        super(rendererIn);
        RENDER_TYPE_FINAL = RENDER_TYPE;
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, GoldenRamEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
    {
        super.render(matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);

        if (entitylivingbaseIn.isAngry())
            RENDER_TYPE_FINAL = RENDER_ANGRY_TYPE;
        else
            RENDER_TYPE_FINAL = RENDER_TYPE;
    }

    @Override
    public RenderType getRenderType()
    {
        return RENDER_TYPE_FINAL;
    }
}