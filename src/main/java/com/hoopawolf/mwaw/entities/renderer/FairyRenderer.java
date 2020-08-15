package com.hoopawolf.mwaw.entities.renderer;

import com.hoopawolf.mwaw.entities.FairyEntity;
import com.hoopawolf.mwaw.entities.WolpertingerEntity;
import com.hoopawolf.mwaw.entities.model.FairyModel;
import com.hoopawolf.mwaw.ref.Reference;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FairyRenderer extends MobRenderer<FairyEntity, FairyModel>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/fairy.png");
    private static final ResourceLocation ANGRY_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/angryfairy.png");

    public FairyRenderer(EntityRendererManager _manager)
    {
        super(_manager, new FairyModel(), 0.3f);
    }

    @Override
    protected int getBlockLight(FairyEntity entityIn, BlockPos partialTicks)
    {
        return 15;
    }

    @Override
    public ResourceLocation getEntityTexture(FairyEntity _entity)
    {
        return _entity.isAngry() ? ANGRY_TEXTURE : TEXTURE;
    }

    @Override
    public Vector3d getRenderOffset(FairyEntity entityIn, float partialTicks)
    {
        return entityIn.isResting() ? new Vector3d(0.0D, -0.2D, 0.0D) : ((entityIn.isPassenger() && entityIn.getRidingEntity() instanceof WolpertingerEntity) ? new Vector3d(entityIn.getRidingEntity().getForward().getX() * 0.7F, -0.9D, entityIn.getRidingEntity().getForward().getZ() * 0.7F) : super.getRenderOffset(entityIn, partialTicks));
    }

    @Override
    protected void preRenderCallback(FairyEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime)
    {
        matrixStackIn.scale(0.4F, 0.4F, 0.4F);
    }
}


