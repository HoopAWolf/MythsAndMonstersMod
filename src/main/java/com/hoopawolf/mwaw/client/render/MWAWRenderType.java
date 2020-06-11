package com.hoopawolf.mwaw.client.render;

import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class MWAWRenderType extends RenderState
{
    public MWAWRenderType(String nameIn, Runnable setupTaskIn, Runnable clearTaskIn)
    {
        super(nameIn, setupTaskIn, clearTaskIn);
    }

    public static RenderType getTextureSwirl(ResourceLocation locationIn, float uIn, float vIn)
    {
        return RenderType.makeType("texture_offsetting", DefaultVertexFormats.ENTITY, 7, 256, false, true, RenderType.State.getBuilder().texture(new RenderState.TextureState(locationIn, false, false)).texturing(new RenderState.OffsetTexturingState(uIn, vIn)).fog(RenderState.NO_FOG).transparency(NO_TRANSPARENCY).diffuseLighting(DIFFUSE_LIGHTING_DISABLED).alpha(DEFAULT_ALPHA).cull(CULL_DISABLED).lightmap(LIGHTMAP_DISABLED).overlay(OVERLAY_ENABLED).build(false));
    }
}
