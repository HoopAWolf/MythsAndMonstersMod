package com.hoopawolf.mwaw.client.particles;

import net.minecraft.client.particle.*;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class OrbitingParticle extends SpriteTexturedParticle
{
    private final double coordX;
    private final double coordY;
    private final double coordZ;
    private double spread;
    private double
            orbitSpeed,
            orbitAngle;

    protected OrbitingParticle(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, double spreadIn)
    {
        this(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        spread = 1.0D;
    }

    protected OrbitingParticle(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn)
    {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn);
        spread = 0.75D; //TODO will be here until i find a better way

        this.motionX = xSpeedIn;
        this.motionY = ySpeedIn;
        this.motionZ = zSpeedIn;
        this.coordX = xCoordIn;
        this.coordY = yCoordIn;
        this.coordZ = zCoordIn;
        this.prevPosX = coordX + Math.cos(orbitAngle) * spread;
        this.prevPosY = yCoordIn + ySpeedIn;
        this.prevPosZ = coordZ + Math.sin(orbitAngle) * spread;
        this.particleScale = 0.1F * (this.rand.nextFloat() * 0.5F + 0.2F);
        float f = this.rand.nextFloat() * 0.6F + 0.4F;
        this.particleRed = 0.9F * f;
        this.particleGreen = 0.9F * f;
        this.particleBlue = 0.0F;
        this.particleAlpha = 0.5F;
        this.canCollide = false;
        this.maxAge = (int) (Math.random() * 10.0D) + 40;
        this.particleGravity = 0.0F;
        orbitAngle = 10D * 10D * Math.PI;
        orbitSpeed = (world.rand.nextDouble() * 0.3217D + 0.1954D) * (rand.nextBoolean() ? 1 : -1);

        this.posX = coordX + Math.cos(orbitAngle) * spread;
        this.posY = this.prevPosY;
        this.posZ = coordZ + Math.sin(orbitAngle) * spread;
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
            posX = coordX + Math.cos(orbitAngle) * spread;
            posY += motionY;
            posZ = coordZ + Math.sin(orbitAngle) * spread;

            orbitAngle += orbitSpeed;
        }
    }
}
