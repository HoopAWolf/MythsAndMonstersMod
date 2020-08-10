package com.hoopawolf.mwaw.entities;

import com.hoopawolf.mwaw.entities.ai.navigation.MWAWMovementController;
import com.hoopawolf.mwaw.entities.ai.navigation.MWAWPathNavigateGround;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.BlazeEntity;
import net.minecraft.entity.monster.PillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

//Spirt BOMBBB, shoot fire, fly, fire charge rain. land cannot do spirit bomb, summon fire spirits(like sloth), explosive fire charge
public class PyromancerEntity extends CreatureEntity implements IRangedAttackMob
{
    private static final DataParameter<Boolean> SHOOTING = EntityDataManager.createKey(DendroidEntity.class, DataSerializers.BOOLEAN);
    float shootRenderTimer;

    public PyromancerEntity(EntityType<? extends PyromancerEntity> type, World worldIn)
    {
        super(type, worldIn);
        shootRenderTimer = 0.0F;
        this.moveController = new MWAWMovementController(this, 30);
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
        this.goalSelector.addGoal(4, new RangedAttackGoal(this, 1.0D, 40, 50, 10.0F));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 4.0F));
        this.goalSelector.addGoal(8, new LookAtGoal(this, CreatureEntity.class, 4.0F));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, CreatureEntity.class, 10, true, false, (p_213621_0_) ->
        {
            return !(p_213621_0_ instanceof PillagerEntity) && !(p_213621_0_ instanceof PyromancerEntity) && !(p_213621_0_ instanceof BlazeEntity); //TODO future cult member
        }));
    }

    @Override
    protected void registerAttributes()
    {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.35D);
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
    }

    @Override
    protected PathNavigator createNavigator(World world)
    {
        return new MWAWPathNavigateGround(this, world);
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
    public float getBrightness()
    {
        return 1.0F;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_BLAZE_DEATH;
    }

    @Override
    public void tick()
    {
        super.tick();
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
            if (source.getTrueSource() instanceof BlazeEntity)
            {
                return false;
            } else if (source.damageType.equals(DamageSource.ON_FIRE.damageType) || source.damageType.equals(DamageSource.IN_FIRE.damageType))
            {
                return false;
            }

            return super.attackEntityFrom(source, amount);
        }
    }

    @Override
    public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor)
    {
        double d0 = this.getDistanceSq(target);
        float f = MathHelper.sqrt(MathHelper.sqrt(d0)) * 0.5F;
        double d1 = target.getPosX() - this.getPosX();
        double d2 = target.getPosYHeight(0.5D) - this.getPosYHeight(0.5D);
        double d3 = target.getPosZ() - this.getPosZ();

        SmallFireballEntity smallfireballentity = new SmallFireballEntity(this.world, this, d1 + this.getRNG().nextGaussian() * (double) f, d2, d3 + this.getRNG().nextGaussian() * (double) f);
        smallfireballentity.setPosition(smallfireballentity.getPosX(), this.getPosYHeight(0.5D) + 0.5D, smallfireballentity.getPosZ());
        this.world.addEntity(smallfireballentity);
        this.playSound(SoundEvents.ENTITY_BLAZE_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
    }
}
