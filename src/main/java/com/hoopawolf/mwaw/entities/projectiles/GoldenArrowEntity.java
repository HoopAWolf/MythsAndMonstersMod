package com.hoopawolf.mwaw.entities.projectiles;

import com.hoopawolf.mwaw.util.EntityRegistryHandler;
import com.hoopawolf.mwaw.util.RegistryHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public class GoldenArrowEntity extends AbstractArrowEntity
{
    public GoldenArrowEntity(EntityType<? extends AbstractArrowEntity> p_i50172_1_, World p_i50172_2_)
    {
        super(p_i50172_1_, p_i50172_2_);
    }

    public GoldenArrowEntity(World worldIn, double x, double y, double z)
    {
        super(EntityRegistryHandler.GOLDEN_ARROW_ENTITY.get(), x, y, z, worldIn);
    }

    public GoldenArrowEntity(World worldIn, LivingEntity shooter)
    {
        super(EntityRegistryHandler.GOLDEN_ARROW_ENTITY.get(), shooter, worldIn);
    }

    @Override
    public void tick()
    {
        super.tick();
        if (this.world.isRemote)
        {
            if (this.inGround && this.timeInGround != 0 && this.timeInGround >= 600)
            {
                this.world.setEntityState(this, (byte) 0);
            }
        }
    }

    @Override
    public void writeAdditional(CompoundNBT compound)
    {
        super.writeAdditional(compound);

    }

    @Override
    public void readAdditional(CompoundNBT compound)
    {
        super.readAdditional(compound);

    }

    @Override //TODO ADD SOME SPECIAL EFFECTS MAYBE
    protected void arrowHit(LivingEntity living)
    {
        super.arrowHit(living);
    }

    @Override
    protected ItemStack getArrowStack()
    {
        return new ItemStack(RegistryHandler.GOLDEN_ARROW.get());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handleStatusUpdate(byte id)
    {
        super.handleStatusUpdate(id);
    }

    @Override
    public IPacket<?> createSpawnPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
