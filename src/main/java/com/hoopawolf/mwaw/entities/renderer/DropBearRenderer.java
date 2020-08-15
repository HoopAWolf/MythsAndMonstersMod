package com.hoopawolf.mwaw.entities.renderer;

import com.hoopawolf.mwaw.entities.DropBearEntity;
import com.hoopawolf.mwaw.entities.model.DropBearModel;
import com.hoopawolf.mwaw.ref.Reference;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class DropBearRenderer extends MobRenderer<DropBearEntity, DropBearModel>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/dropbear.png");
    private static final ResourceLocation SLEEP_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/dropbearsleep.png");

    public DropBearRenderer(EntityRendererManager _manager)
    {
        super(_manager, new DropBearModel(), 0.3f);
    }

    @Override
    public ResourceLocation getEntityTexture(DropBearEntity _entity)
    {
        return _entity.isTired() ? SLEEP_TEXTURE : TEXTURE;
    }
}


