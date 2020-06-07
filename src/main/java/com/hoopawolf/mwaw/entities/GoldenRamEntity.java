package com.hoopawolf.mwaw.entities;

import com.hoopawolf.mwaw.util.ItemBlockRegistryHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class GoldenRamEntity extends CreatureEntity implements net.minecraftforge.common.IShearable
{

    private static final DataParameter<Boolean> SHEARED = EntityDataManager.createKey(GoldenRamEntity.class, DataSerializers.BOOLEAN);
    private int sheepTimer;
    private EatGrassGoal eatGrassGoal;

    public GoldenRamEntity(EntityType<? extends GoldenRamEntity> type, World worldIn)
    {
        super(type, worldIn);
    }

    @Override
    protected void registerGoals()
    {
        this.eatGrassGoal = new EatGrassGoal(this);
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(5, this.eatGrassGoal);
        this.goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue()));
        this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
    }

    @Override
    protected void updateAITasks()
    {
        this.sheepTimer = this.eatGrassGoal.getEatingGrassTimer();
        super.updateAITasks();
    }

    @Override
    public void livingTick()
    {
        if (this.world.isRemote)
        {
            this.sheepTimer = Math.max(0, this.sheepTimer - 1);
        }

        super.livingTick();
    }

    @Override
    protected void registerAttributes()
    {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(70.0D);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
    }

    @Override
    protected void registerData()
    {
        super.registerData();
        this.dataManager.register(SHEARED, false);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handleStatusUpdate(byte id)
    {
        if (id == 10)
        {
            this.sheepTimer = 40;
        } else
        {
            super.handleStatusUpdate(id);
        }

    }

    @OnlyIn(Dist.CLIENT)
    public float getHeadRotationPointY(float p_70894_1_)
    {
        if (this.sheepTimer <= 0)
        {
            return 0.0F;
        } else if (this.sheepTimer >= 4 && this.sheepTimer <= 36)
        {
            return 1.0F;
        } else
        {
            return this.sheepTimer < 4 ? ((float) this.sheepTimer - p_70894_1_) / 4.0F : -((float) (this.sheepTimer - 40) - p_70894_1_) / 4.0F;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public float getHeadRotationAngleX(float p_70890_1_)
    {
        if (this.sheepTimer > 4 && this.sheepTimer <= 36)
        {
            float f = ((float) (this.sheepTimer - 4) - p_70890_1_) / 32.0F;
            return ((float) Math.PI / 5F) + 0.21991149F * MathHelper.sin(f * 28.7F);
        } else
        {
            return this.sheepTimer > 0 ? ((float) Math.PI / 5F) : this.rotationPitch * ((float) Math.PI / 180F);
        }
    }

    @Override
    public void writeAdditional(CompoundNBT compound)
    {
        super.writeAdditional(compound);
        compound.putBoolean("Sheared", this.getSheared());
    }

    @Override
    public void readAdditional(CompoundNBT compound)
    {
        super.readAdditional(compound);
        this.setSheared(compound.getBoolean("Sheared"));
    }

    protected SoundEvent getAmbientSound()
    {
        return SoundEvents.ENTITY_SHEEP_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_SHEEP_HURT;
    }

    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_SHEEP_DEATH;
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn)
    {
        this.playSound(SoundEvents.ENTITY_SHEEP_STEP, 0.15F, 1.0F);
    }

    /**
     * returns true if a sheeps wool has been sheared
     */
    public boolean getSheared()
    {
        return this.dataManager.get(SHEARED);
    }

    /**
     * make a sheep sheared if set to true
     */
    public void setSheared(boolean sheared)
    {
        this.dataManager.set(SHEARED, sheared);
    }

    @Override
    public void eatGrassBonus()
    {
        this.setSheared(false);
    }

    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn)
    {
        return 0.95F * sizeIn.height;
    }

    @Override
    public boolean isShearable(ItemStack item, net.minecraft.world.IWorldReader world, BlockPos pos)
    {
        return !this.getSheared();
    }

    @Override
    public java.util.List<ItemStack> onSheared(ItemStack item, net.minecraft.world.IWorld world, BlockPos pos, int fortune)
    {
        java.util.List<ItemStack> ret = new java.util.ArrayList<>();

        if (!this.world.isRemote)
        {
            this.setSheared(true);
            ret.add(new ItemStack(ItemBlockRegistryHandler.GOLDEN_BOW.get()));//TODO CHANGE TO GOLDEN FLECE LAMO
        }

        this.playSound(SoundEvents.ENTITY_SHEEP_SHEAR, 1.0F, 1.0F);

        return ret;
    }
}
