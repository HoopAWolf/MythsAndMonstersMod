package com.hoopawolf.mwaw.entities.renderer;

import com.hoopawolf.mwaw.entities.DendroidElderEntity;
import com.hoopawolf.mwaw.entities.model.DendroidElderModel;
import com.hoopawolf.mwaw.entities.renderer.layer.DendroidElderEyeLayer;
import com.hoopawolf.mwaw.ref.Reference;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class DendroidElderRenderer extends MobRenderer<DendroidElderEntity, DendroidElderModel>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/dendroidelder.png");
    private final Random rnd = new Random();

    public DendroidElderRenderer(EntityRendererManager _manager)
    {
        super(_manager, new DendroidElderModel(), 1.0f);
        this.addLayer(new DendroidElderEyeLayer(this));
    }

    @Override
    public Vector3d getRenderOffset(DendroidElderEntity entityIn, float partialTicks)
    {
        return (entityIn.getState() == 1 && entityIn.getAbsorbTimer() > 0) ? ((entityIn.getAbsorbTimer() > entityIn.getAbsorbTimerMax() * 0.90F) ? new Vector3d(this.rnd.nextGaussian() * 0.03D, 0.0D, this.rnd.nextGaussian() * 0.03D) :
                new Vector3d(this.rnd.nextGaussian() * 0.005D, 0.0D, this.rnd.nextGaussian() * 0.005D))
                : Vector3d.ZERO;
    }

    @Override
    public ResourceLocation getEntityTexture(DendroidElderEntity _entity)
    {
        return TEXTURE;
    }
}

