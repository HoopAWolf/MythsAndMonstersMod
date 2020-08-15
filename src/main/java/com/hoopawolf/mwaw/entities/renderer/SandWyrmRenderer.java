package com.hoopawolf.mwaw.entities.renderer;

import com.hoopawolf.mwaw.entities.SandWyrmEntity;
import com.hoopawolf.mwaw.entities.model.SandWyrmModel;
import com.hoopawolf.mwaw.ref.Reference;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SandWyrmRenderer extends MobRenderer<SandWyrmEntity, SandWyrmModel>
{
    private static final ResourceLocation[] TEXTURE = {
            new ResourceLocation(Reference.MOD_ID, "textures/entity/sandwyrm.png"),
            new ResourceLocation(Reference.MOD_ID, "textures/entity/redsandwyrm.png")
    };

    public SandWyrmRenderer(EntityRendererManager _manager)
    {
        super(_manager, new SandWyrmModel(), 1.0f);
    }

    @Override
    protected int getBlockLight(SandWyrmEntity entityIn, BlockPos partialTicks)
    {
        return ((entityIn.world.getDayTime() > 1000 && entityIn.world.getDayTime() < 13000) ? entityIn.world.getLightFor(LightType.SKY, new BlockPos(entityIn.getPosition().getX(), entityIn.getPosition().getY() + 1, entityIn.getPosition().getZ())) : super.getBlockLight(entityIn, partialTicks));
    }

    @Override
    protected float getDeathMaxRotation(SandWyrmEntity entityLivingBaseIn)
    {
        return 180.0F;
    }

    @Override
    public ResourceLocation getEntityTexture(SandWyrmEntity _entity)
    {
        return TEXTURE[_entity.getSandWyrmType()];
    }
}