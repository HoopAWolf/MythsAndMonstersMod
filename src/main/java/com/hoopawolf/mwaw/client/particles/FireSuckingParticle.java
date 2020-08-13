package com.hoopawolf.mwaw.client.particles;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FireSuckingParticle extends SuckingParticle
{
    protected FireSuckingParticle(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, double spreadIn)
    {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, spreadIn);
    }

    protected FireSuckingParticle(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn)
    {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        this.multipleParticleScaleBy(3.0F);
        this.setSize(0.25F, 0.25F);
        this.maxAge = this.rand.nextInt(10);
        particleAlpha = 1.0F;
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
            FireSuckingParticle yellowenchantmentparticle = new FireSuckingParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spread);
            yellowenchantmentparticle.selectSpriteRandomly(this.spriteSet);
            return yellowenchantmentparticle;
        }

        @Override
        public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            FireSuckingParticle yellowenchantmentparticle = new FireSuckingParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            yellowenchantmentparticle.selectSpriteRandomly(this.spriteSet);
            return yellowenchantmentparticle;
        }
    }
}
