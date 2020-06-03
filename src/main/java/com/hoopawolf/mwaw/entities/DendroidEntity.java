package com.hoopawolf.mwaw.entities;

import com.hoopawolf.mwaw.entities.ai.RangedAttackWithStrafeGoal;
import com.hoopawolf.mwaw.entities.projectiles.SapEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class DendroidEntity extends CreatureEntity implements IRangedAttackMob
{
    private static final DataParameter<Boolean> SHOOTING = EntityDataManager.createKey(DendroidEntity.class, DataSerializers.BOOLEAN);
    float shootRenderTimer;

    public DendroidEntity(EntityType<? extends DendroidEntity> type, World worldIn)
    {
        super(type, worldIn);
        shootRenderTimer = 0.0F;
    }

    @Override
    protected void registerData()
    {
        super.registerData();
        this.dataManager.register(SHOOTING, false);
    }

    @Override
    protected void registerGoals()
    {
        this.goalSelector.addGoal(4, new RangedAttackWithStrafeGoal(this, this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue(), 40, 50, 10.0F));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue()));
        this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 4.0F));
        this.goalSelector.addGoal(8, new LookAtGoal(this, CreatureEntity.class, 4.0F));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, CreatureEntity.class, 10, true, false, (p_213621_0_) ->
        {
            return !(p_213621_0_ instanceof DendroidEntity);
        }));   //TODO FOR WHEN DENDROID ELDER GETS ADDED ALSO
    }

    @Override
    protected void registerAttributes()
    {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.35D);
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
    }

    public boolean isShooting()
    {
        return this.dataManager.get(SHOOTING);
    }

    public void setIsShooting(boolean _isShooting)
    {
        if (_isShooting)
            shootRenderTimer = 10.0F;

        this.dataManager.set(SHOOTING, _isShooting);
    }

    @Override
    protected boolean isDespawnPeaceful()
    {
        return true;
    }

    @Override
    public CreatureAttribute getCreatureAttribute()
    {
        return CreatureAttribute.UNDEFINED;
    }

    @Override
    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn)
    {
        return worldIn.canSeeSky(getPosition());
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn)
    {
        this.playSound(this.getStepSound(), 0.15F, 5.0F);
    }

    protected SoundEvent getStepSound()
    {
        return SoundEvents.BLOCK_WOOD_STEP;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR;
    }

    @Override
    protected void updateAITasks()
    {
        if (isShooting())
        {
            if (shootRenderTimer > 0)
                --shootRenderTimer;
            else
            {
                setIsShooting(false);
            }
        }

    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (source.getTrueSource() instanceof DendroidEntity)
            return false;

        if (source.damageType.equals(DamageSource.ON_FIRE.damageType))
        {
            this.setFireTimer(100);
        } else if (source.damageType.equals(DamageSource.DROWN.damageType) || source.damageType.equals(DamageSource.CACTUS.damageType))
        {
            return false;
        } else if (source.getTrueSource() instanceof LivingEntity && ((LivingEntity) source.getTrueSource()).getHeldItemMainhand().getItem() instanceof AxeItem)
        {
            return super.attackEntityFrom(source, amount * 2.0F);
        }

        return super.attackEntityFrom(source, amount);
    }

    @Override
    public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor)
    {
        SapEntity sapentity = new SapEntity(this.world, this);
        double d0 = target.getPosYEye() - (double) 1.1F;
        double d1 = target.getPosX() - this.getPosX();
        double d2 = d0 - sapentity.getPosY();
        double d3 = target.getPosZ() - this.getPosZ();
        float f = MathHelper.sqrt(d1 * d1 + d3 * d3) * 0.2F;
        sapentity.shoot(d1, d2 + (double) f, d3, 1.6F, 12.0F);
        this.playSound(SoundEvents.ENTITY_SLIME_JUMP, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.world.addEntity(sapentity);
    }
}