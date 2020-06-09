package com.hoopawolf.mwaw.client.particles;

import net.minecraft.client.particle.*;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class YellowEnchantmentParticle extends SpriteTexturedParticle
{
    private YellowEnchantmentParticle(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn)
    {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        this.motionX = this.motionX * (double) 0.01F + xSpeedIn;
        this.motionY = this.motionY * (double) 0.01F + ySpeedIn;
        this.motionZ = this.motionZ * (double) 0.01F + zSpeedIn;
        this.posX += (this.rand.nextFloat() - this.rand.nextFloat()) * 0.05F;
        this.posY += (this.rand.nextFloat() - this.rand.nextFloat()) * 0.05F;
        this.posZ += (this.rand.nextFloat() - this.rand.nextFloat()) * 0.05F;
        this.maxAge = (int) (8.0D / (Math.random() * 0.8D + 0.2D)) + 4;
        this.particleScale = 0.1F * (this.rand.nextFloat() * 0.5F + 0.2F);
        float f = this.rand.nextFloat() * 0.6F + 0.4F;
        this.particleGreen = 0.9F * f;
        this.particleBlue = 0.0F;
        this.particleAlpha = 0.5F;
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
        public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            YellowEnchantmentParticle flameparticle = new YellowEnchantmentParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            flameparticle.selectSpriteRandomly(this.spriteSet);
            return flameparticle;
        }
    }
}