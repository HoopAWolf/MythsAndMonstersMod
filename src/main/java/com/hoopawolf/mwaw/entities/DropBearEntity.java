package com.hoopawolf.mwaw.entities;

import com.hoopawolf.mwaw.client.animation.AnimationHelper;
import com.hoopawolf.mwaw.entities.ai.controller.MWAWMovementController;
import com.hoopawolf.mwaw.entities.ai.navigation.MWAWPathNavigateGround;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Rotations;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class DropBearEntity extends CreatureEntity implements IMob
{
    private static final DataParameter<Boolean> TIRED = EntityDataManager.createKey(DropBearEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> HUGGING = EntityDataManager.createKey(DropBearEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Float> ANIMATION_SPEED = EntityDataManager.createKey(DropBearEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> HUG_DIR = EntityDataManager.createKey(DropBearEntity.class, DataSerializers.VARINT); //PZ, PX, NZ, NX

    private static final DataParameter<Rotations> HEAD_ROTATION = EntityDataManager.createKey(DropBearEntity.class, DataSerializers.ROTATIONS);
    private static final DataParameter<Rotations> BODY_ROTATION = EntityDataManager.createKey(DropBearEntity.class, DataSerializers.ROTATIONS);
    private static final DataParameter<Rotations> RIGHT_ARM_ROTATION = EntityDataManager.createKey(DropBearEntity.class, DataSerializers.ROTATIONS);
    private static final DataParameter<Rotations> LEFT_ARM_ROTATION = EntityDataManager.createKey(DropBearEntity.class, DataSerializers.ROTATIONS);
    private static final DataParameter<Rotations> RIGHT_LEG_ROTATION = EntityDataManager.createKey(DropBearEntity.class, DataSerializers.ROTATIONS);
    private static final DataParameter<Rotations> LEFT_LEG_ROTATION = EntityDataManager.createKey(DropBearEntity.class, DataSerializers.ROTATIONS);
    public final AnimationHelper animation = new AnimationHelper();

    private BlockPos huggingBlockPos;

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

        huggingBlockPos = null;
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

        this.dataManager.register(TIRED, true);
        this.dataManager.register(HUGGING, false);
        this.dataManager.register(ANIMATION_SPEED, 0F);
        this.dataManager.register(HUG_DIR, 0);

        this.dataManager.register(HEAD_ROTATION, new Rotations(0, 0, 0));
        this.dataManager.register(BODY_ROTATION, new Rotations(0, 0, 0));
        this.dataManager.register(RIGHT_ARM_ROTATION, new Rotations(0, 0, 0));
        this.dataManager.register(LEFT_ARM_ROTATION, new Rotations(0, 0, 0));
        this.dataManager.register(LEFT_LEG_ROTATION, new Rotations(0, 0, 0));
        this.dataManager.register(RIGHT_LEG_ROTATION, new Rotations(0, 0, 0));
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

        this.goalSelector.addGoal(0, new DropBearSwim(this));
        //this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
        // this.goalSelector.addGoal(11, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(12, new DropBearLookAt(this, PlayerEntity.class, 24.0F));
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

    public boolean isHugging()
    {
        return this.dataManager.get(HUGGING);
    }

    public void setHugging(boolean hugging)
    {
        this.dataManager.set(HUGGING, hugging);
    }

    public float getAnimationSpeed()
    {
        return this.dataManager.get(ANIMATION_SPEED);
    }

    public void setAnimationSpeed(float speedIn)
    {
        this.dataManager.set(ANIMATION_SPEED, speedIn);
    }

    public int getHuggingDir()
    {
        return this.dataManager.get(HUG_DIR);
    }

    public void setHuggingDir(int huggingDirIn)
    {
        this.dataManager.set(HUG_DIR, huggingDirIn);
    }

    public Rotations getHeadRotation()
    {
        return this.dataManager.get(HEAD_ROTATION);
    }

    public Rotations getBodyRotation()
    {
        return this.dataManager.get(BODY_ROTATION);
    }

    public Rotations getRightArmRotation()
    {
        return this.dataManager.get(RIGHT_ARM_ROTATION);
    }

    public Rotations getLeftArmRotation()
    {
        return this.dataManager.get(LEFT_ARM_ROTATION);
    }

    public Rotations getRightLegRotation()
    {
        return this.dataManager.get(RIGHT_LEG_ROTATION);
    }

    public Rotations getLeftLegRotation()
    {
        return this.dataManager.get(LEFT_LEG_ROTATION);
    }

    @Override
    public void tick()
    {
        super.tick();

        if (isHugging())
        {
            renderYawOffset = 0;
        }

        if (!world.isRemote)
        {
            if (isHugging())
            {
                setNoGravity(true);
                noClip = true;
                setMotion(Vector3d.ZERO);
            } else
            {
                if (hasNoGravity())
                {
                    setNoGravity(false);
                    noClip = false;
                }
            }

            this.setTired(false);

            if (huggingBlockPos == null)//NZ, PX, PZ, NX
            {
                if (world.getBlockState(new BlockPos(this.getPosition().getX() + 1, this.getPosition().getY(), this.getPosition().getZ())).getMaterial().equals(Material.WOOD) ||
                        world.getBlockState(new BlockPos(this.getPosition().getX() - 1, this.getPosition().getY(), this.getPosition().getZ())).getMaterial().equals(Material.WOOD) ||
                        world.getBlockState(new BlockPos(this.getPosition().getX(), this.getPosition().getY(), this.getPosition().getZ() + 1)).getMaterial().equals(Material.WOOD) ||
                        world.getBlockState(new BlockPos(this.getPosition().getX(), this.getPosition().getY(), this.getPosition().getZ() - 1)).getMaterial().equals(Material.WOOD))
                {
                    if (world.getBlockState(new BlockPos(this.getPosition().getX() + 1, this.getPosition().getY(), this.getPosition().getZ())).getMaterial().equals(Material.WOOD))
                    {
                        huggingBlockPos = new BlockPos(this.getPosition().getX() + 1, this.getPosition().getY(), this.getPosition().getZ());
                        setHuggingDir(1);
                    } else if (world.getBlockState(new BlockPos(this.getPosition().getX() - 1, this.getPosition().getY(), this.getPosition().getZ())).getMaterial().equals(Material.WOOD))
                    {
                        huggingBlockPos = new BlockPos(this.getPosition().getX() - 1, this.getPosition().getY(), this.getPosition().getZ());
                        setHuggingDir(3);
                    } else if (world.getBlockState(new BlockPos(this.getPosition().getX(), this.getPosition().getY(), this.getPosition().getZ() + 1)).getMaterial().equals(Material.WOOD))
                    {
                        huggingBlockPos = new BlockPos(this.getPosition().getX(), this.getPosition().getY(), this.getPosition().getZ() + 1);
                        setHuggingDir(2);
                    } else if (world.getBlockState(new BlockPos(this.getPosition().getX(), this.getPosition().getY(), this.getPosition().getZ() - 1)).getMaterial().equals(Material.WOOD))
                    {
                        huggingBlockPos = new BlockPos(this.getPosition().getX(), this.getPosition().getY(), this.getPosition().getZ() - 1);
                        setHuggingDir(0);
                    }

                    setHugging(true);
                    this.setPosition((float) this.getPosition().getX() + 0.5F, this.getPosition().getY(), (float) this.getPosition().getZ() + 0.5F);
                }
            }
        }
    }

    @Override
    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos)
    {
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

    private class DropBearSwim extends SwimGoal
    {
        DropBearEntity host;

        public DropBearSwim(MobEntity entityIn)
        {
            super(entityIn);
            host = (DropBearEntity) entityIn;
        }

        @Override
        public void startExecuting()
        {
            super.startExecuting();
            host.setTired(false);
        }
    }

    private class DropBearLookAt extends LookAtGoal
    {
        DropBearEntity host;

        public DropBearLookAt(MobEntity entityIn, Class<? extends LivingEntity> watchTargetClass, float maxDistance)
        {
            super(entityIn, watchTargetClass, maxDistance);
            host = (DropBearEntity) entityIn;
        }

        @Override
        public boolean shouldExecute()
        {
            return super.shouldExecute() && !host.isHugging();
        }
    }
}
