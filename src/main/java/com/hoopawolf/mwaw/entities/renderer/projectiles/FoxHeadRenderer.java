package com.hoopawolf.mwaw.entities.renderer.projectiles;

import com.hoopawolf.mwaw.entities.model.projectiles.FoxHeadModel;
import com.hoopawolf.mwaw.entities.projectiles.FoxHeadEntity;
import com.hoopawolf.mwaw.ref.Reference;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Random;


public class FoxHeadRenderer extends EntityRenderer<FoxHeadEntity>
{
    private static final RenderType KITSUNE_SPARK_TEXTURE_TRANSPARANCY = RenderType.getEntityTranslucent(new ResourceLocation(Reference.MOD_ID, "textures/entity/kitsune.png"));
    private final FoxHeadModel<FoxHeadEntity> model = new FoxHeadModel<>();
    private final Random rnd = new Random();

    public FoxHeadRenderer(EntityRendererManager manager)
    {
        super(manager);
    }

    @Override
    protected int getBlockLight(FoxHeadEntity entityIn, float partialTicks)
    {
        return 15;
    }

    @Override
    public void render(FoxHeadEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn)
    {
        matrixStackIn.push();
        float f = MathHelper.rotLerp(entityIn.prevRotationYaw, entityIn.rotationYaw, partialTicks);
        float f1 = MathHelper.lerp(partialTicks, entityIn.prevRotationPitch, entityIn.rotationPitch);
        matrixStackIn.translate(0.0D, 0.3F, 0.0D);
        matrixStackIn.translate(0.0D, 0.3F + (Math.sin(partialTicks) * 0.05F), 0.0D);
        matrixStackIn.scale(1.0F, 1.0F, 1.0F);
        matrixStackIn.rotate(new Quaternion(180.0F, 180.0F, 0.0F, true));
        this.model.setRotationAngles(entityIn, 0.0F, 0.0F, 0.0F, f, f1);
        IVertexBuilder ivertexbuilder1 = bufferIn.getBuffer(KITSUNE_SPARK_TEXTURE_TRANSPARANCY);
        this.model.render(matrixStackIn, ivertexbuilder1, packedLightIn, OverlayTexture.NO_OVERLAY, 0.5F, 1.0F, 0.5F, 0.7F * entityIn.getSpawnPercentage());
        matrixStackIn.pop();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    public Vec3d getRenderOffset(FoxHeadEntity entityIn, float partialTicks)
    {
        return new Vec3d(this.rnd.nextGaussian() * 0.02D, 0.0D, this.rnd.nextGaussian() * 0.02D);
    }

    @Override
    public ResourceLocation getEntityTexture(FoxHeadEntity entity)
    {
        return null;
    }
}
