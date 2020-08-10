package com.hoopawolf.mwaw.entities;

import com.hoopawolf.mwaw.entities.ai.MWAWMeleeAttackGoal;
import com.hoopawolf.mwaw.entities.ai.RangedAttackWithStrafeGoal;
import com.hoopawolf.mwaw.entities.ai.navigation.MWAWMovementController;
import com.hoopawolf.mwaw.entities.ai.navigation.MWAWPathNavigateGround;
import com.hoopawolf.mwaw.entities.projectiles.ClayEntity;
import com.hoopawolf.mwaw.network.MWAWPacketHandler;
import com.hoopawolf.mwaw.network.packets.client.SpawnParticleMessage;
import com.hoopawolf.mwaw.util.EntityRegistryHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.potion.Effects;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class ClayGolemEntity extends CreatureEntity implements IMob, IRangedAttackMob
{
    private static final DataParameter<Boolean> HARDEN_FORM = EntityDataManager.createKey(ClayGolemEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Float> BURN_TIME = EntityDataManager.createKey(ClayGolemEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Boolean> IS_MINION = EntityDataManager.createKey(ClayGolemEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> CLAYGOLEM_PHASE = EntityDataManager.createKey(ClayGolemEntity.class, DataSerializers.VARINT);

    private MWAWMeleeAttackGoal meleeGoal;
    private RangedAttackWithStrafeGoal rangedGoal;

    private boolean resized;
    private int attackTimer;
    private boolean spawned;

    public ClayGolemEntity(EntityType<? extends ClayGolemEntity> type, World worldIn)
    {
        super(type, worldIn);

        this.stepHeight = 1.0F;
        resized = false;
        spawned = false;

        this.moveController = new MWAWMovementController(this, 7);
    }

    @Override
    protected void registerData()
    {
        super.registerData();
        this.dataManager.register(HARDEN_FORM, false);
        this.dataManager.register(IS_MINION, false);
        this.dataManager.register(BURN_TIME, 0.0F);
        this.dataManager.register(CLAYGOLEM_PHASE, 5);
    }

    @Override
    protected void registerGoals()
    {
        meleeGoal = new MWAWMeleeAttackGoal(this, 1.0D, true);
        rangedGoal = new RangedAttackWithStrafeGoal(this, 1.0D, 40, 50, 15.0F);

        this.goalSelector.addGoal(1, meleeGoal);
        this.goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 1.0D, 32.0F));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 4.0F));
        this.goalSelector.addGoal(8, new LookAtGoal(this, CreatureEntity.class, 4.0F));

        this.targetSelector.addGoal(2, new NearestAttackableTargetGolemGoal(this, PlayerEntity.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGolemGoal(this, CreatureEntity.class, 10, true, false, (p_213621_0_) ->
        {
            return !(p_213621_0_ instanceof ClayGolemEntity);
        }));
    }

    @Override
    protected PathNavigator createNavigator(World world)
    {
        return new MWAWPathNavigateGround(this, world);
    }

    @Override
    protected void registerAttributes()
    {
        super.registerAttributes();
        this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);

        this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(015D);
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(250.0D);
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0D);
    }

    @Override
    public void writeAdditional(CompoundNBT compound)
    {
        super.writeAdditional(compound);
        compound.putBoolean("IsMinion", this.isMinion());
        compound.putBoolean("IsHarden", this.isHardenForm());
        compound.putInt("GolemPhase", this.getPhase());
    }

    @Override
    public void readAdditional(CompoundNBT compound)
    {
        super.readAdditional(compound);
        this.setMinion(compound.getBoolean("IsMinion"));
        this.setHardenForm(compound.getBoolean("IsHarden"));
        this.setPhase(compound.getInt("GolemPhase"));
    }

    @Override
    public float getRenderScale()
    {
        return this.isMinion() ? 0.5F : 1.0F;
    }

    public boolean isHardenForm()
    {
        return this.dataManager.get(HARDEN_FORM);
    }

    public void setHardenForm(boolean _isHardenForm)
    {
        if (!world.isRemote && _isHardenForm)
        {
            this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue() * 0.7D);
        }

        this.dataManager.set(HARDEN_FORM, _isHardenForm);
    }

    public boolean isMinion()
    {
        return this.dataManager.get(IS_MINION);
    }

    public void setMinion(boolean _isMinion)
    {
        if (_isMinion)
        {
            if (!world.isRemote)
            {
                this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.0D);
                this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.08D);
                this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(1.0D);

                this.goalSelector.removeGoal(meleeGoal);
                this.goalSelector.removeGoal(rangedGoal);

                this.goalSelector.addGoal(1, rangedGoal);
            }
        }

        this.dataManager.set(IS_MINION, _isMinion);
    }

    public float getBurnTime()
    {
        return this.dataManager.get(BURN_TIME);
    }

    public void setBurnTime(float _burntime)
    {
        this.dataManager.set(BURN_TIME, _burntime);
    }

    public int getPhase()
    {
        return this.dataManager.get(CLAYGOLEM_PHASE);
    }

    public void setPhase(int _phase)
    {
        this.dataManager.set(CLAYGOLEM_PHASE, _phase);
    }

    public int getAttackTimer()
    {
        return this.attackTimer;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handleStatusUpdate(byte id)
    {
        if (id == 4)
        {
            this.attackTimer = 10;
            this.playSound(isHardenForm() ? SoundEvents.BLOCK_STONE_PLACE : SoundEvents.BLOCK_HONEY_BLOCK_PLACE, 1.0F, 1.0F);
        } else
        {
            super.handleStatusUpdate(id);
        }
    }

    @Override
    public int getMaxSpawnedInChunk()
    {
        return 1;
    }

    @Override
    public boolean onLivingFall(float distance, float damageMultiplier)
    {
        return false;
    }

    @Override
    protected boolean isDespawnPeaceful()
    {
        return true;
    }

    @Override
    protected int decreaseAirSupply(int air)
    {
        return air;
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
        this.playSound(this.getStepSound(), 0.15F, 1.0F);
    }

    protected SoundEvent getStepSound()
    {
        return isHardenForm() ? SoundEvents.BLOCK_STONE_STEP : SoundEvents.BLOCK_HONEY_BLOCK_STEP;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return isHardenForm() ? SoundEvents.BLOCK_STONE_BREAK : SoundEvents.BLOCK_HONEY_BLOCK_BREAK;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return isHardenForm() ? SoundEvents.BLOCK_STONE_HIT : SoundEvents.BLOCK_HONEY_BLOCK_HIT;
    }

    @Override
    public void tick()
    {
        super.tick();

        if (this.attackTimer > 0)
        {
            --this.attackTimer;
        }

        if (isMinion() && !resized)
        {
            this.recalculateSize();
            resized = true;
        }

        if (!world.isRemote)
        {
            if (!isHardenForm() && getBurnTime() > 1.0F)
            {
                setHardenForm(true);
            }

            if (this.getMotion().getY() > 0)
            {
                this.setMotion(this.getMotion().getX(), this.getMotion().getY() * 0.1F, this.getMotion().getZ());
            }

            if (getHealth() != getMaxHealth() && this.world.getBlockState(this.getPositionUnderneath()) == Blocks.CLAY.getDefaultState())
            {
                if (ticksExisted % 10 == 0)
                {
                    heal(1.0F);
                    Vec3d _vec = new Vec3d(this.getPosX() - (double) 0.3F, this.getPosYHeight(1.0D), this.getPosZ() + (double) 0.3F);
                    SpawnParticleMessage spawnParticleMessage = new SpawnParticleMessage(_vec, new Vec3d(0, -0.5D, 0), 4, 5, getWidth());
                    MWAWPacketHandler.packetHandler.sendToDimension(this.dimension, spawnParticleMessage);
                }
            }

            if (!isMinion())
            {
                if (!spawned && (getPhase() == 5 && getHealth() <= 200 || getPhase() == 4 && getHealth() <= 150 || getPhase() == 3 && getHealth() <= 100 || getPhase() == 2 && getHealth() <= 50 || getPhase() == 1 && getHealth() <= 0))
                {
                    spawned = true;
                    setPhase(getPhase() - 1);
                    this.playSound(SoundEvents.BLOCK_HONEY_BLOCK_BREAK, 4.0F, 10.0F);

                    for (int i = 0; i < 5; ++i)
                    {
                        ClayGolemEntity golemMinion = EntityRegistryHandler.CLAY_GOLEM_ENTITY.get().create(world);
                        golemMinion.setMinion(true);
                        golemMinion.setLocationAndAngles(this.getPosX(), this.getPosY() + 1.5D, this.getPosZ(), 0.0F, 0.0F);
                        golemMinion.setMotion(rand.nextDouble() - rand.nextDouble(), rand.nextDouble(), rand.nextDouble() - rand.nextDouble());
                        golemMinion.setAttackTarget(this.getAttackTarget());
                        world.addEntity(golemMinion);
                    }
                }

                if (getHealth() <= 50.0F)
                {
                    if (rand.nextInt(100) < 10)
                    {
                        for (int x = -1; x <= 1; ++x)
                        {
                            for (int z = -1; z <= 1; ++z)
                            {
                                BlockPos blockPos = new BlockPos(getPositionUnderneath().getX() + x, getPositionUnderneath().getY(), getPositionUnderneath().getZ() + z);

                                if (this.world.getBlockState(blockPos).isIn(BlockTags.ENDERMAN_HOLDABLE))
                                {
                                    this.world.setBlockState(blockPos, Blocks.CLAY.getDefaultState());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void updateAITasks()
    {
        if (this.getAttackTarget() != null && !this.getAttackTarget().isAlive())
        {
            this.setAttackTarget(null);
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

            if (spawned)
            {
                spawned = false;
            }

            if (source.getTrueSource() instanceof ClayGolemEntity)
                return false;

            if (source.damageType.equals(DamageSource.ON_FIRE.damageType))
            {
                if (!isHardenForm())
                {
                    this.setFire(100);
                    setBurnTime(getBurnTime() + 0.1F);
                } else
                {
                    this.extinguish();
                    return false;
                }
            } else if (isHardenForm() && source.damageType.equals(DamageSource.IN_FIRE.damageType))
            {
                return false;
            }

            return isHardenForm() ? super.attackEntityFrom(source, amount * 0.5F) : super.attackEntityFrom(source, amount);
        }
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn)
    {
        this.attackTimer = 10;
        this.world.setEntityState(this, (byte) 4);

        return super.attackEntityAsMob(entityIn);
    }

    @Override
    public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor)
    {
        this.attackTimer = 10;
        this.world.setEntityState(this, (byte) 4);
        ClayEntity clayentity = new ClayEntity(this.world, this);
        double d0 = target.getPosYEye() - (double) 1.1F;
        double d1 = target.getPosX() - this.getPosX();
        double d2 = d0 - clayentity.getPosY();
        double d3 = target.getPosZ() - this.getPosZ();
        float f = MathHelper.sqrt(d1 * d1 + d3 * d3) * 0.2F;
        clayentity.shoot(d1, d2 + (double) f, d3, 1.6F, 12.0F);
        this.playSound(SoundEvents.BLOCK_HONEY_BLOCK_PLACE, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.world.addEntity(clayentity);
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
