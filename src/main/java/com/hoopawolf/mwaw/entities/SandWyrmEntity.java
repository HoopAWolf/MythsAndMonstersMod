package com.hoopawolf.mwaw.entities;

import com.hoopawolf.mwaw.entities.ai.MWAWMeleeAttackGoal;
import com.hoopawolf.mwaw.entities.helper.EntityHelper;
import com.hoopawolf.mwaw.network.MWAWPacketHandler;
import com.hoopawolf.mwaw.network.packets.client.SpawnParticleMessage;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.Effects;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.Heightmap;
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
    private float timer;

    public SandWyrmEntity(EntityType<? extends SandWyrmEntity> type, World worldIn)
    {
        super(type, worldIn);
        this.moveController = underground_controller;
        this.jumpRemaining = this.maxJump;
        this.setTired(false);
        this.experienceValue = 5;
    }

    public static AttributeModifierMap.MutableAttribute func_234321_m_()
    {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.FLYING_SPEED, 1.7D).createMutableAttribute(Attributes.MAX_HEALTH, 24.0D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 3.0D)
                .createMutableAttribute(Attributes.FOLLOW_RANGE, 12.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.35D);
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
    public void move(MoverType typeIn, Vector3d pos)
    {
        super.move(typeIn, pos);
        this.doBlockCollisions();
    }

    @Override
    protected void registerGoals()
    {
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(3, new SandWyrmEntity.ChargeAttackGoal(this));
        this.goalSelector.addGoal(4, new SandWyrmEntity.DiveGoal(this));
        this.goalSelector.addGoal(5, new SandWyrmEntity.MoveRandomGoal());
        this.goalSelector.addGoal(6, new SandWyrmEntity.TiredMeleeAttackGoal(this, 0.5F, true));

        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
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
    public void tick()
    {
        super.tick();

        if (this.attackTimer > 0)
        {
            --this.attackTimer;
        }

        if (!isTired())
        {
            if (getBlockUnder(0).isIn(BlockTags.SAND) || getBlockUnder(4).isIn(BlockTags.SAND))
            {
                timer = 0;
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

                if (ticksExisted % 10 == 0)
                {
                    if (lastTickPosX == getPosX() && lastTickPosZ == getPosZ() || !world.getBlockState(this.getPosition()).isSolid())
                    {
                        this.navigator.clearPath();
                        this.setMotion(this.getMotion().add(0.0D, -0.5F, 0.0D));
                    }
                }

                if (_flag)
                {
                    if (!world.isRemote)
                    {
                        SpawnParticleMessage spawnParticleMessage = new SpawnParticleMessage(this.getPositionVec(), new Vector3d(world.rand.nextInt(2), world.rand.nextInt(2), world.rand.nextInt(2)), 5, 2, 1.5F);
                        MWAWPacketHandler.packetHandler.sendToDimension(this.world.func_234923_W_(), spawnParticleMessage);
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
                    timer += 0.1F;
                    if (world.isAirBlock(this.getPosition()) && world.getBlockState(getPositionUnderneath()).isSolid() || timer > 50.0F)
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
            double d0 = this.getAttribute(Attributes.FOLLOW_RANGE).getBaseValue();
            List<PlayerEntity> entities = EntityHelper.getPlayersNearby(this, d0, d0, d0, d0 * 2);
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
        Biome optional = worldIn.getBiome(this.getPosition());
        int i = 0;

        if (optional.equals(Biomes.BADLANDS))
            i = 1;

        this.setSandWyrmType(i);
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
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
            if (source.damageType.equals(DamageSource.IN_WALL.damageType))
            {
                return false;
            }

            return super.attackEntityFrom(source, amount);
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
    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos)
    {
    }

    @Override
    public int getMaxSpawnedInChunk()
    {
        return 1;
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
        int y = world.getHeight(Heightmap.Type.MOTION_BLOCKING, getPosition().getX(), getPosition().getZ());

        return worldIn.canSeeSky(this.getPosition()) && (int) this.getPosY() == y;
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
        SandWyrmEntity entity;

        public ChargeAttackGoal(SandWyrmEntity _entity)
        {
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
            entity = _entity;
        }

        @Override
        public boolean shouldExecute()
        {
            return dived && !_flag && !entity.isTired() && entity.getAttackTarget() != null;
        }

        @Override
        public void resetTask()
        {
            dived = false;
        }

        @Override
        public void tick()
        {
            LivingEntity livingentity = entity.getAttackTarget();
            if (livingentity != null && entity.getBoundingBox().intersects(livingentity.getBoundingBox().grow(1.0D)))
            {
                entity.attackEntityAsMob(livingentity);
            }

            double d0 = entity.getDistanceSq(livingentity);
            if (d0 < 32.0D && entity.rand.nextInt(7) == 0)
            {
                entity.playSound(SoundEvents.BLOCK_SAND_HIT, 1.0F, 1.0F);
                Direction direction = entity.getAdjustedHorizontalFacing();
                entity.setMotion(entity.getMotion().add((double) direction.getXOffset() * 0.6D, 0.0D, (double) direction.getZOffset() * 0.6D));
                entity.setMotion(entity.getMotion().getX(), 0.5D, entity.getMotion().getZ());
                entity.navigator.clearPath();
                entity.DecreaseStamina();
                entity.setRotation(2);
            } else
            {
                Vector3d vec3d = livingentity.getPositionVec();
                entity.moveController.setMoveTo(vec3d.x, vec3d.y - 3.0F, vec3d.z, entity.getAttribute(Attributes.FLYING_SPEED).getBaseValue());
                entity.setRotation(0);
            }
        }
    }

    class DiveGoal extends Goal
    {
        SandWyrmEntity entity;
        private boolean hasDived = false;

        public DiveGoal(SandWyrmEntity _entity)
        {
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
            entity = _entity;
        }

        @Override
        public boolean shouldExecute()
        {
            return !dived && !entity.isTired();
        }

        @Override
        public boolean shouldContinueExecuting()
        {
            return !dived && _flag && !entity.isTired();
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
            entity.navigator.clearPath();
            if (hasDived)
            {
                if (entity.world.isAirBlock(new BlockPos(entity.getPosX(), entity.getPosY() - 0.75F, entity.getPosZ())))
                {
                    LivingEntity livingentity = entity.getAttackTarget();
                    if (livingentity != null && entity.getBoundingBox().intersects(livingentity.getBoundingBox().grow(1.0D)) && getMotion().getY() > 0.1F)
                    {
                        entity.attackTimer = 10;
                        entity.world.setEntityState(entity, (byte) 4);
                        entity.attackEntityAsMob(livingentity);
                    }
                }

                if (getBlockUnder(3).isIn(BlockTags.SAND) && entity.getMotion().getY() < 0.0D)
                {
                    entity.setMotion(entity.getMotion().getX(), -0.5D, entity.getMotion().getZ());
                    entity.setRotation(2);
                }
            }

            if (_flag && !hasDived)
            {
                Direction direction = entity.getAdjustedHorizontalFacing();
                entity.setMotion(entity.getMotion().add((double) direction.getXOffset() * 0.6D, 1.0D, (double) direction.getZOffset() * 0.6D));
                entity.setRotation(1);
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
                Vector3d vec3d = new Vector3d(this.posX - SandWyrmEntity.this.getPosX(), this.posY - SandWyrmEntity.this.getPosY(), this.posZ - SandWyrmEntity.this.getPosZ());
                double d0 = vec3d.length();
                if (d0 < SandWyrmEntity.this.getBoundingBox().getAverageEdgeLength())
                {
                    this.action = MovementController.Action.WAIT;
                    SandWyrmEntity.this.setMotion(SandWyrmEntity.this.getMotion().scale(0.5D));
                } else
                {
                    if (dived)
                    {
                        SandWyrmEntity.this.setMotion(SandWyrmEntity.this.getMotion().add(vec3d.scale(SandWyrmEntity.this.getAttribute(Attributes.FLYING_SPEED).getBaseValue() * 0.05D / d0)));

                        if (!world.isRemote)
                        {
                            SpawnParticleMessage spawnParticleMessage = new SpawnParticleMessage(SandWyrmEntity.this.getPositionVec(), new Vector3d(world.rand.nextInt(2), world.rand.nextInt(2), world.rand.nextInt(2)), 5, 2, 1.5F);
                            MWAWPacketHandler.packetHandler.sendToDimension(SandWyrmEntity.this.world.func_234923_W_(), spawnParticleMessage);
                        }
                        SandWyrmEntity.this.playSound(SoundEvents.BLOCK_SAND_BREAK, 1.0F, 1.0F);

                        if (SandWyrmEntity.this.getAttackTarget() == null)
                        {
                            Vector3d vec3d1 = SandWyrmEntity.this.getMotion();
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

    class TiredMeleeAttackGoal extends MWAWMeleeAttackGoal
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
