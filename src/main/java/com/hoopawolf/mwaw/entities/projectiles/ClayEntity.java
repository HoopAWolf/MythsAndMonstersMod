package com.hoopawolf.mwaw.entities.projectiles;

import com.hoopawolf.mwaw.entities.ClayGolemEntity;
import com.hoopawolf.mwaw.util.EntityRegistryHandler;
import com.hoopawolf.mwaw.util.PotionRegistryHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public class ClayEntity extends ProjectileItemEntity
{
    public ClayEntity(EntityType<? extends ClayEntity> p_i50159_1_, World p_i50159_2_)
    {
        super(p_i50159_1_, p_i50159_2_);
    }

    public ClayEntity(World worldIn, LivingEntity throwerIn)
    {
        super(EntityRegistryHandler.CLAY_ENTITY.get(), throwerIn, worldIn);
    }

    public ClayEntity(World worldIn, double x, double y, double z)
    {
        super(EntityRegistryHandler.CLAY_ENTITY.get(), x, y, z, worldIn);
    }

    @Override
    protected Item getDefaultItem()
    {
        return Items.CLAY_BALL;
    }

    private IParticleData makeParticle()
    {
        ItemStack itemstack = this.func_213882_k();
        return itemstack.isEmpty() ? ParticleTypes.MYCELIUM : new ItemParticleData(ParticleTypes.ITEM, itemstack);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handleStatusUpdate(byte id)
    {
        if (id == 3)
        {
            IParticleData iparticledata = this.makeParticle();

            for (int i = 0; i < 8; ++i)
            {
                this.world.addParticle(iparticledata, this.getPosX(), this.getPosY(), this.getPosZ(), 0.0D, 0.0D, 0.0D);
            }
        }

    }

    @Override
    protected void onImpact(RayTraceResult result)
    {
        if (result.getType() == RayTraceResult.Type.ENTITY)
        {
            Entity entity = ((EntityRayTraceResult) result).getEntity();
            entity.attackEntityFrom(DamageSource.causeThrownDamage(this, this.func_234616_v_()), 1.0F);
            if (entity instanceof LivingEntity && !(entity instanceof ClayGolemEntity) && !((LivingEntity) entity).isPotionActive(PotionRegistryHandler.CLAY_SLOW_EFFECT.get()))
            {
                EffectInstance effectinstance = new EffectInstance(PotionRegistryHandler.CLAY_SLOW_EFFECT.get(), 2000);
                ((LivingEntity) entity).addPotionEffect(effectinstance);
            }
        }

        if (!this.world.isRemote)
        {
            this.world.setEntityState(this, (byte) 3);
            this.remove();
        }
    }

    @Override
    public IPacket<?> createSpawnPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}