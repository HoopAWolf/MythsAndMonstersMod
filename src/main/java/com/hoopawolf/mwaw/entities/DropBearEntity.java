package com.hoopawolf.mwaw.entities;

import com.hoopawolf.mwaw.client.animation.AnimationHelper;
import com.hoopawolf.mwaw.entities.ai.navigation.MWAWMovementController;
import com.hoopawolf.mwaw.entities.ai.navigation.MWAWPathNavigateGround;
import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Rotations;
import net.minecraft.world.World;

public class DropBearEntity extends CreatureEntity implements IMob
{
    private static final DataParameter<Boolean> TIRED = EntityDataManager.createKey(SandWyrmEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Rotations> HEAD_ROTATION = EntityDataManager.createKey(PyromancerEntity.class, DataSerializers.ROTATIONS);
    private static final DataParameter<Rotations> BODY_ROTATION = EntityDataManager.createKey(PyromancerEntity.class, DataSerializers.ROTATIONS);
    private static final DataParameter<Rotations> RIGHT_ARM_ROTATION = EntityDataManager.createKey(PyromancerEntity.class, DataSerializers.ROTATIONS);
    private static final DataParameter<Rotations> LEFT_ARM_ROTATION = EntityDataManager.createKey(PyromancerEntity.class, DataSerializers.ROTATIONS);
    private static final DataParameter<Rotations> RIGHT_LEG_ROTATION = EntityDataManager.createKey(PyromancerEntity.class, DataSerializers.ROTATIONS);
    private static final DataParameter<Rotations> LEFT_LEG_ROTATION = EntityDataManager.createKey(PyromancerEntity.class, DataSerializers.ROTATIONS);
    public final AnimationHelper animation = new AnimationHelper();

    public DropBearEntity(EntityType<? extends DropBearEntity> type, World worldIn)
    {
        super(type, worldIn);
        this.setTired(false);

        animation.registerData(HEAD_ROTATION);
        animation.registerData(BODY_ROTATION);
        animation.registerData(RIGHT_ARM_ROTATION);
        animation.registerData(LEFT_ARM_ROTATION);
        animation.registerData(LEFT_LEG_ROTATION);
        animation.registerData(RIGHT_LEG_ROTATION);

        this.moveController = new MWAWMovementController(this, 30);
    }

    public static AttributeModifierMap.MutableAttribute func_234321_m_()
    {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 140.0D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 7.0D)
                .createMutableAttribute(Attributes.FOLLOW_RANGE, 48.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.2D);
    }

    @Override
    protected void registerData()
    {
        super.registerData();

        this.dataManager.register(TIRED, false);
    }

    @Override
    protected PathNavigator createNavigator(World world)
    {
        return new MWAWPathNavigateGround(this, world);
    }

    @Override
    protected void registerGoals()
    {
        this.targetSelector.addGoal(2, (new HurtByTargetGoal(this)));

        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(11, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(12, new LookAtGoal(this, VillagerEntity.class, 24.0F));
    }

    @Override
    protected boolean isDespawnPeaceful()
    {
        return true;
    }

    public boolean isTired()
    {
        return this.dataManager.get(TIRED);
    }

    public void setTired(boolean tired)
    {
        this.dataManager.set(TIRED, tired);
    }

    @Override
    public void tick()
    {
        super.tick();
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (!net.minecraftforge.common.ForgeHooks.onLivingAttack(this, source, amount)) return false;
        if (this.isInvulnerableTo(source))
        {
            return false;
        } else if (this.world.isRemote)
        {
            return false;
        } else if (this.getHealth() <= 0.0F)
        {
            return false;
        } else if (source.isFireDamage() && this.isPotionActive(Effects.FIRE_RESISTANCE))
        {
            return false;
        } else
        {
            if (source.damageType.equals(DamageSource.FALL.damageType))
            {
                return false;
            }

            return super.attackEntityFrom(source, amount);
        }
    }

    @Override
    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos)
    {
    }

    @Override
    public int getMaxSpawnedInChunk()
    {
        return 1;
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return null;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_PANDA_HURT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_HUSK_DEATH;
    }

    private BlockState getBlockAbove(int _highness)
    {
        for (int i = _highness; i > 0; --i)
        {
            if (!this.world.isAirBlock(new BlockPos(this.getPosX(), this.getPosY() + (1 + i), this.getPosZ())))
            {
                return this.world.getBlockState(new BlockPos(this.getPosX(), this.getPosY() + (1 + i), this.getPosZ()));
            }
        }

        return this.world.getBlockState(new BlockPos(this.getPosX(), this.getPosY() + 1, this.getPosZ()));
    }
}
