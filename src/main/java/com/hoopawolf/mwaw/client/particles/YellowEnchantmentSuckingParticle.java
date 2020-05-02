package com.hoopawolf.mwaw.client.particles;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class YellowEnchantmentSuckingParticle extends SuckingParticle
{
    protected YellowEnchantmentSuckingParticle(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, double spreadIn)
    {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, spreadIn);
    }

    protected YellowEnchantmentSuckingParticle(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn)
    {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
    }

    @OnlyIn(Dist.CLIENT)
    public static class YellowEnchantmentSucking implements IParticleFactory<BasicParticleType>
    {
        private final IAnimatedSprite spriteSet;

        public YellowEnchantmentSucking(IAnimatedSprite p_i50441_1_)
        {
            this.spriteSet = p_i50441_1_;
        }

        public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, double spread)
        {
            YellowEnchantmentSuckingParticle yellowenchantmentparticle = new YellowEnchantmentSuckingParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spread);
            yellowenchantmentparticle.selectSpriteRandomly(this.spriteSet);
            return yellowenchantmentparticle;
        }

        @Override
        public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            YellowEnchantmentSuckingParticle yellowenchantmentparticle = new YellowEnchantmentSuckingParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            yellowenchantmentparticle.selectSpriteRandomly(this.spriteSet);
            return yellowenchantmentparticle;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class NautilusFactory implements IParticleFactory<BasicParticleType>
    {
        private final IAnimatedSprite spriteSet;

        public NautilusFactory(IAnimatedSprite p_i50442_1_)
        {
            this.spriteSet = p_i50442_1_;
        }

        public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, double spread)
        {
            YellowEnchantmentSuckingParticle yellowenchantmentparticle = new YellowEnchantmentSuckingParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spread);
            yellowenchantmentparticle.selectSpriteRandomly(this.spriteSet);
            return yellowenchantmentparticle;
        }

        public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            YellowEnchantmentSuckingParticle yellowenchantmentparticle = new YellowEnchantmentSuckingParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            yellowenchantmentparticle.selectSpriteRandomly(this.spriteSet);
            return yellowenchantmentparticle;
        }
    }
}
