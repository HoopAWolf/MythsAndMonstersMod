package com.hoopawolf.mwaw.client.particles;

import com.hoopawolf.mwaw.entities.PyromancerEntity;
import com.hoopawolf.mwaw.entities.helper.EntityHelper;
import net.minecraft.client.particle.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FireParticle extends SpriteTexturedParticle
{
    private boolean setSized;
    private float sizeMultiplyer;

    private FireParticle(World p_i51046_1_, double p_i51046_2_, double p_i51046_4_, double p_i51046_6_, double p_i51046_8_, double p_i51046_10_, double p_i51046_12_, boolean p_i51046_14_)
    {
        super(p_i51046_1_, p_i51046_2_, p_i51046_4_, p_i51046_6_);
        this.multipleParticleScaleBy(3.0F);
        this.setSize(0.25F, 0.25F);
        this.maxAge = this.rand.nextInt(10);
        particleAlpha = 1.0F;
        this.particleGravity = 0.01F;
        this.motionX = p_i51046_8_;
        this.motionY = p_i51046_10_ + (double) (this.rand.nextFloat() / 500.0F);
        this.motionZ = p_i51046_12_;
        setSized = false;
        sizeMultiplyer = 1.0F;
    }

    @Override
    protected int getBrightnessForRender(float partialTick)
    {
        int i = super.getBrightnessForRender(partialTick);
        int j = 240;
        int k = i >> 16 & 255;
        return 240 | k << 16;
    }

    @Override
    public float getScale(float scaleFactor)
    {
        if (!setSized)
        {
            for (LivingEntity entity : EntityHelper.getEntitiesNearbyWithPos(world, getBoundingBox(), new BlockPos(this.posX, this.posY, this.posZ), LivingEntity.class, 2, 5, 2, 5))
            {
                if (entity instanceof PyromancerEntity)
                {
                    sizeMultiplyer = entity.getHealth() / entity.getMaxHealth();
                    setSized = true;
                    break;
                }
            }
        }

        return this.particleScale * sizeMultiplyer;
    }

    @Override
    public void tick()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.age++ < this.maxAge && !(this.particleAlpha <= 0.0F))
        {
            this.motionX += this.rand.nextFloat() / 5000.0F * (float) (this.rand.nextBoolean() ? 1 : -1);
            this.motionZ += this.rand.nextFloat() / 5000.0F * (float) (this.rand.nextBoolean() ? 1 : -1);
            this.motionY += this.particleGravity;
            this.move(this.motionX, this.motionY, this.motionZ);
            if (this.age >= this.maxAge - 60 && this.particleAlpha > 0.01F)
            {
                this.particleAlpha -= 0.015F;
            }

        } else
        {
            this.setExpired();
        }
    }

    @Override
    public IParticleRenderType getRenderType()
    {
        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class FireSmokeFactory implements IParticleFactory<BasicParticleType>
    {
        private final IAnimatedSprite spriteSet;

        public FireSmokeFactory(IAnimatedSprite p_i51180_1_)
        {
            this.spriteSet = p_i51180_1_;
        }

        @Override
        public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            FireParticle campfireparticle = new FireParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, false);
            campfireparticle.selectSpriteRandomly(this.spriteSet);
            return campfireparticle;
        }
    }
}
