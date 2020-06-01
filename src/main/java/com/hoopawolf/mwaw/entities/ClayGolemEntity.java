package com.hoopawolf.mwaw.entities;

import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class ClayGolemEntity extends CreatureEntity implements IMob
{

    public ClayGolemEntity(EntityType<? extends ClayGolemEntity> type, World worldIn)
    {
        super(type, worldIn);

        this.stepHeight = 1.0F;
    }

    @Override
    protected void registerData()
    {
        super.registerData();
    }

    @Override
    protected void registerGoals()
    {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue(), true));
        this.goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue(), 32.0F));
        this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 4.0F));
        this.goalSelector.addGoal(8, new LookAtGoal(this, CreatureEntity.class, 4.0F));
        this.goalSelector.addGoal(9, new WaterAvoidingRandomWalkingGoal(this, this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue()));

        this.targetSelector.addGoal(2, new NearestAttackableTargetGolemGoal(this, PlayerEntity.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGolemGoal(this, CreatureEntity.class, 10, true, false, (p_213621_0_) ->
        {
            return !(p_213621_0_ instanceof ClayGolemEntity);
        }));
    }

    @Override
    protected void registerAttributes()
    {
        super.registerAttributes();
        this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);

        this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.35D);
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(250.0D);
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0D);
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
        return SoundEvents.BLOCK_HONEY_BLOCK_STEP;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.BLOCK_HONEY_BLOCK_BREAK;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.BLOCK_HONEY_BLOCK_HIT;
    }

    @Override
    protected void updateAITasks()
    {

    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (source.damageType.equals(DamageSource.ON_FIRE.damageType))
        {

        }

        return super.attackEntityFrom(source, amount);
    }

    class NearestAttackableTargetGolemGoal<T extends LivingEntity> extends NearestAttackableTargetGoal
    {

        public NearestAttackableTargetGolemGoal(MobEntity goalOwnerIn, Class targetClassIn, boolean checkSight)
        {
            super(goalOwnerIn, targetClassIn, checkSight);
        }

        public NearestAttackableTargetGolemGoal(MobEntity goalOwnerIn, Class<T> targetClassIn, int targetChanceIn, boolean checkSight, boolean nearbyOnlyIn, @Nullable Predicate<LivingEntity> targetPredicate)
        {
            super(goalOwnerIn, targetClassIn, targetChanceIn, checkSight, nearbyOnlyIn, targetPredicate);
        }

        @Override
        public boolean shouldExecute()
        {
            return getAttackTarget() == null && super.shouldExecute();
        }
    }
}
