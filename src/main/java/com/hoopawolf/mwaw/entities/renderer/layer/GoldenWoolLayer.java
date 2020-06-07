package com.hoopawolf.mwaw.entities.renderer.layer;

import com.hoopawolf.mwaw.entities.GoldenRamEntity;
import com.hoopawolf.mwaw.entities.model.GoldenRamModel;
import com.hoopawolf.mwaw.entities.model.layer.GoldenWoolModel;
import com.hoopawolf.mwaw.ref.Reference;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GoldenWoolLayer extends LayerRenderer<GoldenRamEntity, GoldenRamModel>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/goldenramwool.png");
    private final GoldenWoolModel sheepModel = new GoldenWoolModel();

    public GoldenWoolLayer(IEntityRenderer<GoldenRamEntity, GoldenRamModel> rendererIn)
    {
        super(rendererIn);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, GoldenRamEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
    {
        if (!entitylivingbaseIn.getSheared() && !entitylivingbaseIn.isInvisible())
        {
            renderCopyCutoutModel(this.getEntityModel(), this.sheepModel, TEXTURE, matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, 1.0F, 1.0F, 1.0F);
        }
    }
}