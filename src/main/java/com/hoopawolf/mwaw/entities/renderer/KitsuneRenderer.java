package com.hoopawolf.mwaw.entities.renderer;

import com.hoopawolf.mwaw.entities.KitsuneEntity;
import com.hoopawolf.mwaw.entities.model.KitsuneModel;
import com.hoopawolf.mwaw.entities.renderer.layer.KitsuneHeldItemLayer;
import com.hoopawolf.mwaw.ref.Reference;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KitsuneRenderer extends MobRenderer<KitsuneEntity, KitsuneModel>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/kitsune.png");
    private static final ResourceLocation VILLAGER_TEXTURE = new ResourceLocation("textures/entity/villager/villager.png");

    public KitsuneRenderer(EntityRendererManager _manager)
    {
        super(_manager, new KitsuneModel(), 0.6f);
        this.addLayer(new KitsuneHeldItemLayer(this));
    }

    @Override
    public ResourceLocation getEntityTexture(KitsuneEntity _entity)
    {
        return (_entity.isVillagerForm()) ? VILLAGER_TEXTURE : TEXTURE;
    }
}
