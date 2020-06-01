package com.hoopawolf.mwaw.entities.renderer;

import com.hoopawolf.mwaw.entities.WolpertingerEntity;
import com.hoopawolf.mwaw.entities.model.WolpertingerModel;
import com.hoopawolf.mwaw.ref.Reference;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WolpertingerRenderer extends MobRenderer<WolpertingerEntity, WolpertingerModel>
{
    private static final ResourceLocation[] TEXTURE = {
            new ResourceLocation(Reference.MOD_ID, "textures/entity/wolpertinger.png"),
            new ResourceLocation(Reference.MOD_ID, "textures/entity/wolpertinger2.png"),
            new ResourceLocation(Reference.MOD_ID, "textures/entity/wolpertinger3.png"),
            new ResourceLocation(Reference.MOD_ID, "textures/entity/wolpertinger4.png")
    };

    public WolpertingerRenderer(EntityRendererManager _manager)
    {
        super(_manager, new WolpertingerModel(), 0.3F);
    }

    @Override
    public ResourceLocation getEntityTexture(WolpertingerEntity _entity)
    {
        return TEXTURE[_entity.getWolpertingerType()];
    }
}
