package com.hoopawolf.mwaw.entities.renderer.projectiles;

import com.hoopawolf.mwaw.client.render.MWAWRenderType;
import com.hoopawolf.mwaw.entities.projectiles.SpiritBombEntity;
import com.hoopawolf.mwaw.ref.Reference;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;

public class SpiritBombRenderer extends EntityRenderer<SpiritBombEntity>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/spiritbomb.png");
    private static final float field_229047_f_ = (float) Math.sin((Math.PI / 4D));
    private final ModelRenderer field_229049_h_;

    public SpiritBombRenderer(EntityRendererManager renderManagerIn)
    {
        super(renderManagerIn);
        this.shadowSize = 0.5F;
        this.field_229049_h_ = new ModelRenderer(64, 32, 0, 0);
        this.field_229049_h_.addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F);
    }

    public static float func_229051_a_(SpiritBombEntity p_229051_0_, float p_229051_1_)
    {
        float f = (float) p_229051_0_.innerRotation + p_229051_1_;
        float f1 = MathHelper.sin(f * 0.2F) / 2.0F + 0.5F;
        f1 = (f1 * f1 + f1) * 0.4F;
        return f1 - 1.4F;
    }

    @Override
    public void render(SpiritBombEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn)
    {
        matrixStackIn.push();
        float f = func_229051_a_(entityIn, partialTicks);
        float _tick = (float) entityIn.ticksExisted + partialTicks;
        float f1 = ((float) entityIn.innerRotation + partialTicks) * 3.0F;
        IVertexBuilder ivertexbuilder = bufferIn.getBuffer(MWAWRenderType.getTextureSwirl(this.func_225633_a_(), this.func_225634_a_(_tick), this.func_225634_a_(_tick)));
        float size = 5.0F * entityIn.getCharge();
        matrixStackIn.translate(0.0F, 1.5F, 0.0F);
        matrixStackIn.scale(size, size, size);
        int i = OverlayTexture.NO_OVERLAY;
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(f1));
        matrixStackIn.rotate(new Quaternion(new Vector3f(field_229047_f_, 0.0F, field_229047_f_), 60.0F, true));
        this.field_229049_h_.render(matrixStackIn, ivertexbuilder, packedLightIn, i);
        matrixStackIn.pop();

        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    protected float func_225634_a_(float p_225634_1_)
    {
        return p_225634_1_ * 0.001F;
    }

    protected ResourceLocation func_225633_a_()
    {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getEntityTexture(SpiritBombEntity entity)
    {
        return TEXTURE;
    }

}
