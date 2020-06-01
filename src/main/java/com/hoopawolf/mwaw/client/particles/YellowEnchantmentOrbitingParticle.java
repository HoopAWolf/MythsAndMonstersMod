package com.hoopawolf.mwaw.client.particles;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class YellowEnchantmentOrbitingParticle extends OrbitingParticle
{

    private YellowEnchantmentOrbitingParticle(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, double spreadIn)
    {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, spreadIn);
    }

    private YellowEnchantmentOrbitingParticle(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn)
    {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        float f = this.rand.nextFloat() * 0.6F + 0.4F;
        this.particleRed = 0.9F * f;
        this.particleGreen = 0.9F * f;
        this.particleBlue = 0.0F;
        this.particleAlpha = 0.5F;
    }

    @OnlyIn(Dist.CLIENT)
    public static class YellowEnchantmentOrbiting implements IParticleFactory<BasicParticleType>
    {
        private final IAnimatedSprite spriteSet;

        public YellowEnchantmentOrbiting(IAnimatedSprite p_i50441_1_)
        {
            this.spriteSet = p_i50441_1_;
        }

        public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, double spread)
        {
            YellowEnchantmentOrbitingParticle yellowenchantmentparticle = new YellowEnchantmentOrbitingParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spread);
            yellowenchantmentparticle.selectSpriteRandomly(this.spriteSet);
            return yellowenchantmentparticle;
        }

        @Override
        public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            YellowEnchantmentOrbitingParticle yellowenchantmentparticle = new YellowEnchantmentOrbitingParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            yellowenchantmentparticle.selectSpriteRandomly(this.spriteSet);
            return yellowenchantmentparticle;
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
            YellowEnchantmentOrbitingParticle yellowenchantmentparticle = new YellowEnchantmentOrbitingParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spread);
            yellowenchantmentparticle.selectSpriteRandomly(this.spriteSet);
            return yellowenchantmentparticle;
        }

        @Override
        public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            YellowEnchantmentOrbitingParticle yellowenchantmentparticle = new YellowEnchantmentOrbitingParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            yellowenchantmentparticle.selectSpriteRandomly(this.spriteSet);
            return yellowenchantmentparticle;
        }
    }
}
