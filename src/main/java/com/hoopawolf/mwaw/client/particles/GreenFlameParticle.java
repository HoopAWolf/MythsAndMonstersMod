package com.hoopawolf.mwaw.client.particles;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GreenFlameParticle extends SpriteTexturedParticle
{
    private GreenFlameParticle(ClientWorld worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn)
    {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        this.motionX = this.motionX * (double) 0.01F + xSpeedIn;
        this.motionY = this.motionY * (double) 0.01F + ySpeedIn;
        this.motionZ = this.motionZ * (double) 0.01F + zSpeedIn;
        this.posX += (this.rand.nextFloat() - this.rand.nextFloat()) * 0.05F;
        this.posY += (this.rand.nextFloat() - this.rand.nextFloat()) * 0.05F;
        this.posZ += (this.rand.nextFloat() - this.rand.nextFloat()) * 0.05F;
        this.maxAge = (int) (8.0D / (Math.random() * 0.8D + 0.2D)) + 4;
        float f = this.rand.nextFloat() * 0.6F + 0.4F;
        this.particleRed = 0.4F;
        this.particleGreen = 0.9F * f;
        this.particleBlue = 0.4F;
    }

    @Override
    public IParticleRenderType getRenderType()
    {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void move(double x, double y, double z)
    {
        this.setBoundingBox(this.getBoundingBox().offset(x, y, z));
        this.resetPositionToBB();
    }

    @Override
    public float getScale(float scaleFactor)
    {
        float f = ((float) this.age + scaleFactor) / (float) this.maxAge;
        return this.particleScale * (1.0F - f * f * 0.5F);
    }

    @Override
    public int getBrightnessForRender(float partialTick)
    {
        float f = ((float) this.age + partialTick) / (float) this.maxAge;
        f = MathHelper.clamp(f, 0.0F, 1.0F);
        int i = super.getBrightnessForRender(partialTick);
        int j = i & 255;
        int k = i >> 16 & 255;
        j = j + (int) (f * 15.0F * 16.0F);
        if (j > 240)
        {
            j = 240;
        }

        return j | k << 16;
    }

    @Override
    public void tick()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.age++ >= this.maxAge)
        {
            this.setExpired();
        } else
        {
            this.move(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.96F;
            this.motionY *= 0.96F;
            this.motionZ *= 0.96F;
            if (this.onGround)
            {
                this.motionX *= 0.7F;
                this.motionZ *= 0.7F;
            }

        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType>
    {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite p_i50823_1_)
        {
            this.spriteSet = p_i50823_1_;
        }

        @Override
        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            GreenFlameParticle flameparticle = new GreenFlameParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            flameparticle.selectSpriteRandomly(this.spriteSet);
            return flameparticle;
        }
    }
}
