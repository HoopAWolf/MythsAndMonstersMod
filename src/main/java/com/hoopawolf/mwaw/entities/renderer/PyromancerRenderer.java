package com.hoopawolf.mwaw.entities.renderer;

import com.hoopawolf.mwaw.entities.PyromancerEntity;
import com.hoopawolf.mwaw.entities.model.PyromancerModel;
import com.hoopawolf.mwaw.ref.Reference;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class PyromancerRenderer extends MobRenderer<PyromancerEntity, PyromancerModel>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/pyromancer.png");

    public PyromancerRenderer(EntityRendererManager _manager)
    {
        super(_manager, new PyromancerModel(), 0.5f);
        //this.addLayer(new PyromancerGlowLayer(this));
    }

    @Override
    protected int getBlockLight(PyromancerEntity entityIn, BlockPos partialTicks)
    {
        return (int) (15.0F * (entityIn.getHealth() / entityIn.getMaxHealth()));
    }

    @Override
    public ResourceLocation getEntityTexture(PyromancerEntity _entity)
    {
        return TEXTURE;
    }
}