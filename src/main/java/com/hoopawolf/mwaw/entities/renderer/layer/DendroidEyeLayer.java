package com.hoopawolf.mwaw.entities.renderer.layer;

import com.hoopawolf.mwaw.entities.DendroidEntity;
import com.hoopawolf.mwaw.entities.model.DendroidModel;
import com.hoopawolf.mwaw.ref.Reference;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.AbstractEyesLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DendroidEyeLayer extends AbstractEyesLayer<DendroidEntity, DendroidModel>
{
    private static final RenderType RENDER_TYPE = RenderType.getEyes(new ResourceLocation(Reference.MOD_ID, "textures/entity/dendroid_eye.png"));

    public DendroidEyeLayer(IEntityRenderer<DendroidEntity, DendroidModel> rendererIn)
    {
        super(rendererIn);
    }

    @Override
    public RenderType getRenderType()
    {
        return RENDER_TYPE;
    }
}