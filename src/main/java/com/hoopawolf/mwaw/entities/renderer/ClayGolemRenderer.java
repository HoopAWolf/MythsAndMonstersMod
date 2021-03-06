package com.hoopawolf.mwaw.entities.renderer;

import com.hoopawolf.mwaw.entities.ClayGolemEntity;
import com.hoopawolf.mwaw.entities.model.ClayGolemModel;
import com.hoopawolf.mwaw.ref.Reference;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClayGolemRenderer extends MobRenderer<ClayGolemEntity, ClayGolemModel>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/claygolem.png");
    private static final ResourceLocation HARDEN_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/claygolemharden.png");

    public ClayGolemRenderer(EntityRendererManager _manager)
    {
        super(_manager, new ClayGolemModel(), 1.0f);
    }

    @Override
    public void render(ClayGolemEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn)
    {
        if (entityIn.isMinion())
        {
            this.shadowSize = 0.5F;
            matrixStackIn.push();
            matrixStackIn.scale(0.3F, 0.3F, 0.3F);
            matrixStackIn.translate(0.0D, 0.28D, 0.0D);
            super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
            matrixStackIn.pop();
        } else
        {
            this.shadowSize = 1.0F;
            super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        }
    }

    @Override
    public ResourceLocation getEntityTexture(ClayGolemEntity _entity)
    {
        return _entity.isHardenForm() ? HARDEN_TEXTURE : TEXTURE;
    }
}