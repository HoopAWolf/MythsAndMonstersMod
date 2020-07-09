package com.hoopawolf.mwaw.client.particles;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NatureAuraSuckingParticle extends SuckingParticle
{
    protected NatureAuraSuckingParticle(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, double spreadIn)
    {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, spreadIn);
    }

    protected NatureAuraSuckingParticle(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn)
    {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        float f = this.rand.nextFloat() * 0.6F + 0.4F;
        this.particleRed = 0.4F;
        this.particleGreen = 0.9F * f;
        this.particleBlue = 0.4F;
    }

    @OnlyIn(Dist.CLIENT)
    public static class NatureAuraSucking implements IParticleFactory<BasicParticleType>
    {
        private final IAnimatedSprite spriteSet;

        public NatureAuraSucking(IAnimatedSprite p_i50441_1_)
        {
            this.spriteSet = p_i50441_1_;
        }

        public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, double spread)
        {
            NatureAuraSuckingParticle particle = new NatureAuraSuckingParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spread);
            particle.selectSpriteRandomly(this.spriteSet);
            return particle;
        }

        @Override
        public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            NatureAuraSuckingParticle particle = new NatureAuraSuckingParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            particle.selectSpriteRandomly(this.spriteSet);
            return particle;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType>
    {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite p_i50442_1_)
        {
            this.spriteSet = p_i50442_1_;
        }

        public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, double spread)
        {
            NatureAuraSuckingParticle particle = new NatureAuraSuckingParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spread);
            particle.selectSpriteRandomly(this.spriteSet);
            return particle;
        }

        @Override
        public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            NatureAuraSuckingParticle particle = new NatureAuraSuckingParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            particle.selectSpriteRandomly(this.spriteSet);
            return particle;
        }
    }
}
