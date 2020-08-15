package com.hoopawolf.mwaw.entities.projectiles;

import com.hoopawolf.mwaw.util.EntityRegistryHandler;
import com.hoopawolf.mwaw.util.ItemBlockRegistryHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public class SapEntity extends ProjectileItemEntity
{
    public SapEntity(EntityType<? extends SapEntity> p_i50159_1_, World p_i50159_2_)
    {
        super(p_i50159_1_, p_i50159_2_);
    }

    public SapEntity(World worldIn, LivingEntity throwerIn)
    {
        super(EntityRegistryHandler.SAP_ENTITY.get(), throwerIn, worldIn);
    }

    public SapEntity(World worldIn, double x, double y, double z)
    {
        super(EntityRegistryHandler.SAP_ENTITY.get(), x, y, z, worldIn);
    }

    @Override
    protected Item getDefaultItem()
    {
        return ItemBlockRegistryHandler.SAP.get();
    }

    private IParticleData makeParticle()
    {
        ItemStack itemstack = this.func_213882_k();
        return itemstack.isEmpty() ? ParticleTypes.ITEM_SLIME : new ItemParticleData(ParticleTypes.ITEM, itemstack);
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
            if (entity instanceof LivingEntity)
            {
                EffectInstance effectinstance = new EffectInstance(Effects.SLOWNESS, 200);
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