package com.hoopawolf.mwaw.entities.renderer;

import com.hoopawolf.mwaw.entities.DendroidEntity;
import com.hoopawolf.mwaw.entities.model.DendroidModel;
import com.hoopawolf.mwaw.entities.renderer.layer.DendroidEyeLayer;
import com.hoopawolf.mwaw.ref.Reference;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;


public class DendroidRenderer extends MobRenderer<DendroidEntity, DendroidModel>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/dendroid.png");

    public DendroidRenderer(EntityRendererManager _manager)
    {
        super(_manager, new DendroidModel(), 1.0f);
        this.addLayer(new DendroidEyeLayer(this));
    }


    @Override
    public ResourceLocation getEntityTexture(DendroidEntity _entity)
    {
        return TEXTURE;
    }
}
