package com.hoopawolf.mwaw.client.particles;

import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;

public class OrbitingParticle extends SpriteTexturedParticle
{
    private final double coordX;
    private final double coordY;
    private final double coordZ;
    private final double
            orbitSpeed;
    private double spread;
    private double orbitAngle;

    protected OrbitingParticle(ClientWorld worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, double spreadIn)
    {
        this(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        spread = 1.0D;
    }

    protected OrbitingParticle(ClientWorld worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn)
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
        this.canCollide = false;
        this.maxAge = (int) (Math.random() * 10.0D) + 40;
        this.particleGravity = 0.0F;
        orbitAngle = 10D * 10D * Math.PI;
        orbitSpeed = (world.rand.nextDouble() * 0.3217D + 0.1954D) * (rand.nextBoolean() ? 1 : -1);

        this.posX = coordX + Math.cos(orbitAngle) * spread;
        this.posY = this.prevPosY;
        this.posZ = coordZ + Math.sin(orbitAngle) * spread;
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
            posX = coordX + Math.cos(orbitAngle) * spread;
            posY += motionY;
            posZ = coordZ + Math.sin(orbitAngle) * spread;

            orbitAngle += orbitSpeed;
        }
    }
}
