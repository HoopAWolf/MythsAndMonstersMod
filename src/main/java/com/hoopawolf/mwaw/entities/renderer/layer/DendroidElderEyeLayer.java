package com.hoopawolf.mwaw.entities.renderer.layer;

import com.hoopawolf.mwaw.entities.DendroidElderEntity;
import com.hoopawolf.mwaw.entities.model.DendroidElderModel;
import com.hoopawolf.mwaw.ref.Reference;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.AbstractEyesLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DendroidElderEyeLayer extends AbstractEyesLayer<DendroidElderEntity, DendroidElderModel>
{
    private static final RenderType RENDER_TYPE = RenderType.getEyes(new ResourceLocation(Reference.MOD_ID, "textures/entity/dendroid_elder_eye.png"));

    public DendroidElderEyeLayer(IEntityRenderer<DendroidElderEntity, DendroidElderModel> rendererIn)
    {
        super(rendererIn);
    }

    @Override
    public RenderType getRenderType()
    {
        return RENDER_TYPE;
    }
}