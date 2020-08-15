package com.hoopawolf.mwaw.entities.renderer;

import com.hoopawolf.mwaw.entities.FireSpiritEntity;
import com.hoopawolf.mwaw.ref.Reference;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.ShulkerBulletModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class FireSpiritRenderer extends EntityRenderer<FireSpiritEntity>
{
    private static final ResourceLocation SHULKER_SPARK_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/firespirit.png");
    private static final RenderType field_229123_e_ = RenderType.getEntityTranslucent(SHULKER_SPARK_TEXTURE);
    private final ShulkerBulletModel<FireSpiritEntity> model = new ShulkerBulletModel<>();

    public FireSpiritRenderer(EntityRendererManager manager)
    {
        super(manager);
    }

    @Override
    protected int getBlockLight(FireSpiritEntity entityIn, BlockPos partialTicks)
    {
        return 15;
    }

    @Override
    public void render(FireSpiritEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn)
    {
        matrixStackIn.push();
        float f = MathHelper.rotLerp(entityIn.prevRotationYaw, entityIn.rotationYaw, partialTicks);
        float f1 = MathHelper.lerp(partialTicks, entityIn.prevRotationPitch, entityIn.rotationPitch);
        float f2 = (float) entityIn.ticksExisted + partialTicks;
        matrixStackIn.translate(0.0D, 0.15F, 0.0D);
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(MathHelper.sin(f2 * 0.1F) * 180.0F));
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(MathHelper.cos(f2 * 0.1F) * 180.0F));
        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(MathHelper.sin(f2 * 0.15F) * 360.0F));
        matrixStackIn.scale(-0.5F, -0.5F, 0.5F);
        this.model.setRotationAngles(entityIn, 0.0F, 0.0F, 0.0F, f, f1);
        IVertexBuilder ivertexbuilder = bufferIn.getBuffer(this.model.getRenderType(SHULKER_SPARK_TEXTURE));
        this.model.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStackIn.scale(1.5F, 1.5F, 1.5F);
        IVertexBuilder ivertexbuilder1 = bufferIn.getBuffer(field_229123_e_);
        this.model.render(matrixStackIn, ivertexbuilder1, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.15F);
        matrixStackIn.pop();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    public ResourceLocation getEntityTexture(FireSpiritEntity entity)
    {
        return SHULKER_SPARK_TEXTURE;
    }
}