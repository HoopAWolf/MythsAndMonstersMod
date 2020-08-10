package com.hoopawolf.mwaw.entities.renderer.layer;

import com.hoopawolf.mwaw.entities.PyromancerEntity;
import com.hoopawolf.mwaw.entities.model.PyromancerModel;
import com.hoopawolf.mwaw.ref.Reference;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.AbstractEyesLayer;
import net.minecraft.util.ResourceLocation;

public class PyromancerGlowLayer extends AbstractEyesLayer<PyromancerEntity, PyromancerModel>
{
    private static final RenderType RENDER_TYPE = RenderType.getEyes(new ResourceLocation(Reference.MOD_ID, "textures/entity/pyromancer_glow.png"));

    public PyromancerGlowLayer(IEntityRenderer<PyromancerEntity, PyromancerModel> rendererIn)
    {
        super(rendererIn);
    }

    @Override
    public RenderType getRenderType()
    {
        return RENDER_TYPE;
    }
}