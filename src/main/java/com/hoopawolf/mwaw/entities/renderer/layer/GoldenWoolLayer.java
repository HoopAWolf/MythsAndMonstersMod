package com.hoopawolf.mwaw.entities.renderer.layer;

import com.hoopawolf.mwaw.client.render.MWAWRenderType;
import com.hoopawolf.mwaw.entities.GoldenRamEntity;
import com.hoopawolf.mwaw.entities.model.GoldenRamModel;
import com.hoopawolf.mwaw.entities.model.layer.GoldenWoolModel;
import com.hoopawolf.mwaw.ref.Reference;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
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
            float f = (float) entitylivingbaseIn.ticksExisted + partialTicks;
            EntityModel<GoldenRamEntity> entitymodel = this.func_225635_b_();
            entitymodel.setLivingAnimations(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks);
            this.getEntityModel().copyModelAttributesTo(entitymodel);
            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(MWAWRenderType.getTextureSwirl(this.func_225633_a_(), this.func_225634_a_(f), f * 0.005F));
            entitymodel.setRotationAngles(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            entitymodel.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

//            renderCopyCutoutModel(this.getEntityModel(), this.sheepModel, TEXTURE, matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, 1.0F, 1.0F, 1.0F);
        }
    }

    protected float func_225634_a_(float p_225634_1_)
    {
        return p_225634_1_ * 0.005F;
    }

    protected ResourceLocation func_225633_a_()
    {
        return TEXTURE;
    }

    protected EntityModel<GoldenRamEntity> func_225635_b_()
    {
        return this.sheepModel;
    }
}