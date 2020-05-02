package com.hoopawolf.mwaw.client.particles;

import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class SuckingParticle extends SpriteTexturedParticle
{
    private double spread;

    protected SuckingParticle(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, double spreadIn)
    {
        this(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        spread = 1.0D;
    }

    protected SuckingParticle(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn)
    {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn);
        spread = 3.75D; //TODO will be here until i find a better way

        double coordX = (worldIn.rand.nextInt(100) < 50) ? xCoordIn - spread : xCoordIn + spread;
        double coordY = (worldIn.rand.nextInt(100) < 50) ? yCoordIn - spread : yCoordIn + spread;
        double coordZ = (worldIn.rand.nextInt(100) < 50) ? zCoordIn - spread : zCoordIn + spread;

        this.motionX = MathHelper.signum(xCoordIn - coordX) * xSpeedIn;
        this.motionY = MathHelper.signum(yCoordIn - coordY) * ySpeedIn;
        this.motionZ = MathHelper.signum(zCoordIn - coordZ) * zSpeedIn;
        this.prevPosX = coordX;
        this.prevPosY = coordY;
        this.prevPosZ = coordZ;
        this.posX = coordX;
        this.posY = coordY;
        this.posZ = coordZ;
        this.particleScale = 0.1F * (this.rand.nextFloat() * 0.5F + 0.2F);
        float f = this.rand.nextFloat() * 0.6F + 0.4F;
        this.particleRed = 0.9F * f;
        this.particleGreen = 0.9F * f;
        this.particleBlue = 0.0F;
        this.setAlphaF(0.5F);
        this.canCollide = false;
        this.maxAge = (int) (Math.random() * 10.0D) + 80;
        this.particleGravity = 0.0F;
    }

    public IParticleRenderType getRenderType()
    {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public void move(double x, double y, double z)
    {
        this.setBoundingBox(this.getBoundingBox().offset(x, y, z));
        this.resetPositionToBB();
    }

    public int getBrightnessForRender(float partialTick)
    {
        int i = super.getBrightnessForRender(partialTick);
        float f = (float) this.age / (float) this.maxAge;
        f = f * f;
        f = f * f;
        int j = i & 255;
        int k = i >> 16 & 255;
        k = k + (int) (f * 15.0F * 16.0F);
        if (k > 240)
        {
            k = 240;
        }

        return j | k << 16;
    }

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
            posX += motionX;
            posY += motionY;
            posZ += motionZ;
        }
    }
}

