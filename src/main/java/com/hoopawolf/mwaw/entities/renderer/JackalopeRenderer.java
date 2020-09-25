package com.hoopawolf.mwaw.entities.renderer;

import com.hoopawolf.mwaw.entities.JackalopeEntity;
import com.hoopawolf.mwaw.entities.model.JackalopeModel;
import com.hoopawolf.mwaw.ref.Reference;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class JackalopeRenderer extends MobRenderer<JackalopeEntity, JackalopeModel>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/jackalope.png");

    public JackalopeRenderer(EntityRendererManager renderManagerIn)
    {
        super(renderManagerIn, new JackalopeModel(), 0.7F);
    }

//    @Override
//    public void render(JackalopeEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn)
//    {
//        if(entityIn.isEscaping())
//        {
//            matrixStackIn.push();
//            matrixStackIn.translate(0.0D, entityIn.getEscapingTimer() * 2.0D, 0.0D);
//            super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
//            matrixStackIn.pop();
//        }
//        else
//        {
//            super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
//        }
//    }

    @Override
    public ResourceLocation getEntityTexture(JackalopeEntity entity)
    {
        return TEXTURE;
    }
}