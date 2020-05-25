package com.hoopawolf.mwaw.entities.renderer.projectiles;

import com.hoopawolf.mwaw.entities.projectiles.GoldenArrowEntity;
import com.hoopawolf.mwaw.ref.Reference;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;


public class GoldenArrowRenderer extends ArrowRenderer<GoldenArrowEntity>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/goldenarrow.png");

    public GoldenArrowRenderer(EntityRendererManager renderManagerIn)
    {
        super(renderManagerIn);
    }

    @Override
    public ResourceLocation getEntityTexture(GoldenArrowEntity _entity)
    {
        return TEXTURE;
    }

}