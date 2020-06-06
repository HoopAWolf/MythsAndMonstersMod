package com.hoopawolf.mwaw.entities;

import com.hoopawolf.mwaw.entities.helper.EntityHelper;
import com.hoopawolf.mwaw.network.MWAWPacketHandler;
import com.hoopawolf.mwaw.network.packets.client.SpawnParticleMessage;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class SandWyrmEntity extends CreatureEntity implements IMob
{
    private static final DataParameter<Boolean> TIRED = EntityDataManager.createKey(SandWyrmEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> TYPE = EntityDataManager.createKey(SandWyrmEntity.class, DataSerializers.VARINT); //0 - NORMAL SAND, 1 - RED SAND
    private static final DataParameter<Integer> ROTATION = EntityDataManager.createKey(SandWyrmEntity.class, DataSerializers.VARINT); // 0 - normal, 1 - dive up, 2 - dive down
    private final int maxJump = 5;
    private final float[] modelRotateX = new float[6];
    boolean _flag = false;
    boolean dived = false;
    int recoveryTimer = 0;
    MovementController land_controller = new MovementController(this);
    MovementController underground_controller = new SandWyrmEntity.MoveHelperController(this);
    private int jumpRemaining;
    private float lastRotateX, newRotateX;

    private int attackTimer;

    public SandWyrmEntity(EntityType<? extends SandWyrmEntity> type, World worldIn)
    {
        super(type, worldIn);
        this.moveController = underground_controller;
        this.jumpRemaining = this.maxJump;
        this.setTired(false);
        this.experienceValue = 5;
    }

    @Override
    protected void registerData()
    {
        super.registerData();

        this.dataManager.register(TIRED, false);
        this.dataManager.register(ROTATION, 0);
        this.dataManager.register(TYPE, 0);
    }

    @Override
    public void move(MoverType typeIn, Vec3d pos)
    {
        super.move(typeIn, pos);
        this.doBlockCollisions();
    }

    @Override
    protected void registerGoals()
    {
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(3, new SandWyrmEntity.ChargeAttackGoal());
        this.goalSelector.addGoal(4, new SandWyrmEntity.DiveGoal());
        this.goalSelector.addGoal(5, new SandWyrmEntity.MoveRandomGoal());
        this.goalSelector.addGoal(6, new SandWyrmEntity.TiredMeleeAttackGoal(this, this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue() * 0.5F, true));

        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    protected void registerAttributes()
    {
        super.registerAttributes();
        this.getAttributes().registerAttribute(SharedMonsterAttributes.FLYING_SPEED);
        this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);

        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(24.0D);
        this.getAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(1.7D);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
        this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(12.0D);
    }

    @Override
    public void writeAdditional(CompoundNBT compound)
    {
        super.writeAdditional(compound);
        compound.putInt("SandWyrmType", this.getSandWyrmType());

        for (int i = 0; i < modelRotateX.length; ++i)
        {
            compound.putFloat("RotateX" + i, modelRotateX[i]);
        }
    }

    @Override
    public void readAdditional(CompoundNBT compound)
    {
        super.readAdditional(compound);

        this.setSandWyrmType(compound.getInt("SandWyrmType"));

        for (int i = 0; i < modelRotateX.length; ++i)
        {
            if (compound.contains("RotateX" + i))
            {
                modelRotateX[i] = compound.getFloat("RotateX" + i);
            } else
            {
                modelRotateX[i] = 0.0F;
            }
        }
    }

    @Override
    protected boolean isDespawnPeaceful()
    {
        return true;
    }

    @Override
    public float getBrightness()
    {
        return 1.0F;
    }

    @Override
    public void tick()
    {
        super.tick();

        if (this.attackTimer > 0)
        {
            --this.attackTimer;
        }

        if (!isTired())
        {
            if (getBlockUnder(0).isIn(BlockTags.SAND))
            {
                for (int i = 0; i < modelRotateX.length; ++i)
                {
                    if (i == 0)
                    {
                        this.lastRotateX = this.modelRotateX[i];
                        this.modelRotateX[i] = MathHelper.cos(ticksExisted * 0.6F) * (float) Math.PI * 0.01F * (float) (1 + Math.abs(i - 2));

                    } else
                    {
                        newRotateX = this.lastRotateX;
                        this.lastRotateX = this.modelRotateX[i];

                        this.modelRotateX[i] = newRotateX;
                    }
                }

                this.moveController = underground_controller;
                this.noClip = true;
                this.setNoGravity(true);

                if (ticksExisted % 20 == 0)
                {
                    if (lastTickPosX == getPosX() && lastTickPosY == getPosY() && lastTickPosZ == getPosZ())
                    {
                        this.navigator.clearPath();
                        this.setMotion(this.getMotion().add(0.0D, -0.5F, 0.0D));
                    }
                }

                if (_flag)
                {
                    if (!world.isRemote)
                    {
                        SpawnParticleMessage spawnParticleMessage = new SpawnParticleMessage(this.getPositionVec(), new Vec3d(world.rand.nextInt(2), world.rand.nextInt(2), world.rand.nextInt(2)), 5, 2, 1.5F);
                        MWAWPacketHandler.packetHandler.sendToDimension(this.dimension, spawnParticleMessage);
                    }
                    this.playSound(SoundEvents.BLOCK_SAND_BREAK, 1.0F, 1.0F);
                }
            } else
            {
                if (world.isAirBlock(this.getPosition()))
                {
                    this.moveController = land_controller;
                    this.noClip = false;
                    this.setNoGravity(false);
                    if (world.isAirBlock(this.getPosition()) && world.getBlockState(getPositionUnderneath()).isSolid())
                    {
                        DecreaseStamina();
                    }
                }
            }
        } else
        {
            if (world.isAirBlock(this.getPosition()))
            {
                this.moveController = land_controller;
                this.noClip = false;
                this.setNoGravity(false);

                SandWyrmEntity.this.rotationPitch = 0.0F;

                if (isTired())
                {
                    for (int i = 0; i < modelRotateX.length; ++i)
                    {
                        this.modelRotateX[i] = 0.0F;
                    }

                    SandWyrmEntity.this.setRotation(0);
                }

            } else
            {
                this.setMotion(this.getMotion().add(0.0D, 0.05F, 0.0D));
            }
        }

        if (getAttackTarget() == null)
        {
            double d0 = this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getBaseValue();
            List<PlayerEntity> entities = EntityHelper.INSTANCE.getPlayersNearby(this, d0, d0, d0, d0 * 2);
            for (PlayerEntity entity : entities)
            {
                if (!entity.isCreative() && !entity.isCrouching())
                {
                    this.setAttackTarget(entity);
                    break;
                }
            }
        } else
        {
            if (!getAttackTarget().isAlive() || getDistanceSq(getAttackTarget()) > 50.0D)
            {
                this.setAttackTarget(null);
                this.navigator.clearPath();
            }
        }
    }

    @Override
    protected void updateAITasks()
    {
        if (!this.isTired())
        {
            if (getBlockAbove(1).isIn(BlockTags.SAND))
            {
                if (!(getAttackTarget() != null && this.getMotion().getY() > 0.0F))
                    this.setMotion(this.getMotion().add(0.0D, -this.getMotion().getY(), 0.0D));
                _flag = false;
            } else
            {
                _flag = true;
            }

            if (this.jumpRemaining <= 0)
            {
                this.jumpRemaining = 0;
                this.setTired(true);
            }
        } else
        {
            SandWyrmEntity.this.IncreaseStamina();

            if (this.jumpRemaining >= this.maxJump)
            {
                this.navigator.clearPath();
                this.ResetStamina();
                this.setTired(false);
            }
        }
    }

    @Override
    public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag)
    {
        int i = 0;

        if (worldIn.getBiome(this.getPosition()) == Biomes.BADLANDS)
            i = 1;

        this.setSandWyrmType(i);
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (source.damageType.equals(DamageSource.IN_WALL.damageType))
        {
            return false;
        }

        return super.attackEntityFrom(source, amount);
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn)
    {
        this.attackTimer = 10;
        this.world.setEntityState(this, (byte) 4);

        return super.attackEntityAsMob(entityIn);
    }

    @Override
    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos)
    {
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handleStatusUpdate(byte id)
    {
        if (id == 4)
        {
            this.attackTimer = 10;
            this.playSound(SoundEvents.ENTITY_PANDA_BITE, 1.0F, 1.0F);
        } else
        {
            super.handleStatusUpdate(id);
        }
    }

    @Override
    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn)
    {
        return worldIn.canSeeSky(getPosition());
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return null;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_VEX_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_VEX_HURT;
    }

    public float[] getAllRotateX()
    {
        return modelRotateX;
    }

    public boolean isTired()
    {
        return this.dataManager.get(TIRED);
    }

    public void setTired(boolean tired)
    {
        this.dataManager.set(TIRED, tired);
    }

    public int getSandWyrmType()
    {
        return this.dataManager.get(TYPE);
    }

    public void setSandWyrmType(int type)
    {
        this.dataManager.set(TYPE, type);
    }

    public int getRotation()
    {
        return this.dataManager.get(ROTATION);
    }

    public void setRotation(int _rotation)
    {
        this.dataManager.set(ROTATION, _rotation);
    }

    public int getAttackTimer()
    {
        return this.attackTimer;
    }

    protected void DecreaseStamina()
    {
        if (rand.nextInt(100) < 60)
            --jumpRemaining;
    }

    protected void IncreaseStamina()
    {
        if (rand.nextInt(100) < 40)
        {
            ++recoveryTimer;

            if (recoveryTimer >= 100)
            {
                ++jumpRemaining;
                recoveryTimer = 0;
            }
        }
    }

    protected void ResetStamina()
    {
        jumpRemaining = maxJump;
    }

    private BlockState getBlockUnder(int _deepness)
    {
        for (int i = 0; i <= _deepness; ++i)
        {
            if (!this.world.isAirBlock(new BlockPos(this.getPosX(), this.getPosY() - (1 + i), this.getPosZ())))
            {
                return this.world.getBlockState(new BlockPos(this.getPosX(), this.getPosY() - (1 + i), this.getPosZ()));
            }
        }

        return this.world.getBlockState(new BlockPos(this.getPosX(), this.getPosY() - 1, this.getPosZ()));
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

    class ChargeAttackGoal extends Goal
    {
        public ChargeAttackGoal()
        {
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean shouldExecute()
        {
            return dived && !_flag && !SandWyrmEntity.this.isTired() && SandWyrmEntity.this.getAttackTarget() != null;
        }

        @Override
        public void resetTask()
        {
            dived = false;
        }

        @Override
        public void tick()
        {
            LivingEntity livingentity = SandWyrmEntity.this.getAttackTarget();
            if (livingentity != null && SandWyrmEntity.this.getBoundingBox().intersects(livingentity.getBoundingBox().grow(1.0D)))
            {
                SandWyrmEntity.this.attackEntityAsMob(livingentity);
            }

            double d0 = SandWyrmEntity.this.getDistanceSq(livingentity);
            if (d0 < 32.0D && SandWyrmEntity.this.rand.nextInt(7) == 0)
            {
                SandWyrmEntity.this.playSound(SoundEvents.BLOCK_SAND_HIT, 1.0F, 1.0F);
                Direction direction = SandWyrmEntity.this.getAdjustedHorizontalFacing();
                SandWyrmEntity.this.setMotion(SandWyrmEntity.this.getMotion().add((double) direction.getXOffset() * 0.6D, 0.0D, (double) direction.getZOffset() * 0.6D));
                SandWyrmEntity.this.setMotion(SandWyrmEntity.this.getMotion().getX(), 0.5D, SandWyrmEntity.this.getMotion().getZ());
                SandWyrmEntity.this.navigator.clearPath();
                SandWyrmEntity.this.DecreaseStamina();
                SandWyrmEntity.this.setRotation(2);
            } else
            {
                Vec3d vec3d = livingentity.getPositionVec();
                SandWyrmEntity.this.moveController.setMoveTo(vec3d.x, vec3d.y - 3.0F, vec3d.z, SandWyrmEntity.this.getAttribute(SharedMonsterAttributes.FLYING_SPEED).getBaseValue());
                SandWyrmEntity.this.setRotation(0);
            }
        }
    }

    class DiveGoal extends Goal
    {
        private boolean hasDived = false;

        public DiveGoal()
        {
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean shouldExecute()
        {
            return !dived && !SandWyrmEntity.this.isTired();
        }

        @Override
        public boolean shouldContinueExecuting()
        {
            return !dived && _flag && !SandWyrmEntity.this.isTired();
        }

        @Override
        public void resetTask()
        {
            dived = true;
            hasDived = false;
        }

        @Override
        public void tick()
        {
            SandWyrmEntity.this.navigator.clearPath();
            if (hasDived)
            {
                if (SandWyrmEntity.this.world.isAirBlock(new BlockPos(SandWyrmEntity.this.getPosX(), SandWyrmEntity.this.getPosY() - 0.75F, SandWyrmEntity.this.getPosZ())))
                {
                    LivingEntity livingentity = SandWyrmEntity.this.getAttackTarget();
                    if (livingentity != null && SandWyrmEntity.this.getBoundingBox().intersects(livingentity.getBoundingBox().grow(1.0D)) && getMotion().getY() > 0.1F)
                    {
                        SandWyrmEntity.this.attackTimer = 10;
                        SandWyrmEntity.this.world.setEntityState(SandWyrmEntity.this, (byte) 4);
                        SandWyrmEntity.this.attackEntityAsMob(livingentity);
                    }
                }

                if (getBlockUnder(3).isIn(BlockTags.SAND) && SandWyrmEntity.this.getMotion().getY() < 0.0D)
                {
                    SandWyrmEntity.this.setMotion(SandWyrmEntity.this.getMotion().getX(), -0.5D, SandWyrmEntity.this.getMotion().getZ());
                    SandWyrmEntity.this.setRotation(2);
                }
            }

            if (_flag && !hasDived)
            {
                Direction direction = SandWyrmEntity.this.getAdjustedHorizontalFacing();
                SandWyrmEntity.this.setMotion(SandWyrmEntity.this.getMotion().add((double) direction.getXOffset() * 0.6D, 1.0D, (double) direction.getZOffset() * 0.6D));
                SandWyrmEntity.this.setRotation(1);
                hasDived = true;
            }
        }
    }

    class MoveHelperController extends MovementController
    {
        public MoveHelperController(SandWyrmEntity sandwyrm)
        {
            super(sandwyrm);
        }

        @Override
        public void tick()
        {
            if (this.action == MovementController.Action.MOVE_TO)
            {
                Vec3d vec3d = new Vec3d(this.posX - SandWyrmEntity.this.getPosX(), this.posY - SandWyrmEntity.this.getPosY(), this.posZ - SandWyrmEntity.this.getPosZ());
                double d0 = vec3d.length();
                if (d0 < SandWyrmEntity.this.getBoundingBox().getAverageEdgeLength())
                {
                    this.action = MovementController.Action.WAIT;
                    SandWyrmEntity.this.setMotion(SandWyrmEntity.this.getMotion().scale(0.5D));
                } else
                {
                    if (dived)
                    {
                        SandWyrmEntity.this.setMotion(SandWyrmEntity.this.getMotion().add(vec3d.scale(SandWyrmEntity.this.getAttribute(SharedMonsterAttributes.FLYING_SPEED).getBaseValue() * 0.05D / d0)));

                        if (!world.isRemote)
                        {
                            SpawnParticleMessage spawnParticleMessage = new SpawnParticleMessage(SandWyrmEntity.this.getPositionVec(), new Vec3d(world.rand.nextInt(2), world.rand.nextInt(2), world.rand.nextInt(2)), 5, 2, 1.5F);
                            MWAWPacketHandler.packetHandler.sendToDimension(SandWyrmEntity.this.dimension, spawnParticleMessage);
                        }
                        SandWyrmEntity.this.playSound(SoundEvents.BLOCK_SAND_BREAK, 1.0F, 1.0F);

                        if (SandWyrmEntity.this.getAttackTarget() == null)
                        {
                            Vec3d vec3d1 = SandWyrmEntity.this.getMotion();
                            SandWyrmEntity.this.rotationYaw = -((float) MathHelper.atan2(vec3d1.x, vec3d1.z)) * (180F / (float) Math.PI);
                        } else
                        {
                            double d2 = SandWyrmEntity.this.getAttackTarget().getPosX() - SandWyrmEntity.this.getPosX();
                            double d1 = SandWyrmEntity.this.getAttackTarget().getPosZ() - SandWyrmEntity.this.getPosZ();
                            SandWyrmEntity.this.rotationYaw = -((float) MathHelper.atan2(d2, d1)) * (180F / (float) Math.PI);
                        }
                        SandWyrmEntity.this.renderYawOffset = SandWyrmEntity.this.rotationYaw;
                        SandWyrmEntity.this.setRotation(0);
                    }
                }
            }
        }
    }

    class MoveRandomGoal extends Goal
    {
        public MoveRandomGoal()
        {
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean shouldExecute()
        {
            return dived && !_flag && !SandWyrmEntity.this.isTired() && !SandWyrmEntity.this.getMoveHelper().isUpdating() && SandWyrmEntity.this.rand.nextInt(20) == 0;
        }

        @Override
        public void tick()
        {
            for (int i = 0; i < 3; ++i)
            {
                BlockPos blockpos1 = SandWyrmEntity.this.getPosition().add(SandWyrmEntity.this.rand.nextInt(15) - 7, SandWyrmEntity.this.rand.nextInt(11) - 5, SandWyrmEntity.this.rand.nextInt(15) - 7);
                if (SandWyrmEntity.this.world.getBlockState(blockpos1).isIn(BlockTags.SAND))
                {
                    SandWyrmEntity.this.setRotation(0);
                    SandWyrmEntity.this.moveController.setMoveTo((double) blockpos1.getX() + 0.5D, (double) blockpos1.getY() + 0.5D, (double) blockpos1.getZ() + 0.5D, 0.25D);
                    break;
                }
            }
        }
    }

    class TiredMeleeAttackGoal extends MeleeAttackGoal
    {
        public TiredMeleeAttackGoal(CreatureEntity creature, double speedIn, boolean useLongMemory)
        {
            super(creature, speedIn, useLongMemory);
        }

        @Override
        public boolean shouldExecute()
        {
            return isTired() && super.shouldExecute();
        }

        @Override
        public boolean shouldContinueExecuting()
        {
            return isTired() && super.shouldContinueExecuting();
        }
    }
}
