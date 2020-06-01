package com.hoopawolf.mwaw.entities.renderer;

import com.hoopawolf.mwaw.entities.ClayGolemEntity;
import com.hoopawolf.mwaw.entities.model.ClayGolemModel;
import com.hoopawolf.mwaw.ref.Reference;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClayGolemRenderer extends MobRenderer<ClayGolemEntity, ClayGolemModel>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/claygolem.png");

    public ClayGolemRenderer(EntityRendererManager _manager)
    {
        super(_manager, new ClayGolemModel(), 1.0f);
    }

    @Override
    public ResourceLocation getEntityTexture(ClayGolemEntity _entity)
    {
        return TEXTURE;
    }
}