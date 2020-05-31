package com.hoopawolf.mwaw.entities;

import com.hoopawolf.mwaw.entities.helper.EntityHelper;
import com.hoopawolf.mwaw.entities.projectiles.FoxHeadEntity;
import com.hoopawolf.mwaw.network.MWAWPacketHandler;
import com.hoopawolf.mwaw.network.packets.client.SpawnParticleMessage;
import com.hoopawolf.mwaw.network.packets.client.SpawnSuckingParticleMessage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class KitsuneEntity extends CreatureEntity
{
    private static final DataParameter<Boolean> VILLAGER_FORM = EntityDataManager.createKey(KitsuneEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SHOUTING = EntityDataManager.createKey(KitsuneEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> FOX_PHASE = EntityDataManager.createKey(KitsuneEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Byte> FOX_FLAGS = EntityDataManager.createKey(KitsuneEntity.class, DataSerializers.BYTE);
    private static final DataParameter<Integer> SHAKE_HEAD_TICKS = EntityDataManager.createKey(KitsuneEntity.class, DataSerializers.VARINT);
    private static final Predicate<Entity> IS_PREY = (p_213498_0_) ->
    {
        return p_213498_0_ instanceof ChickenEntity || p_213498_0_ instanceof SheepEntity || p_213498_0_ instanceof WolfEntity;
    };
    private static final Predicate<Entity> SHOULD_AVOID = (p_213463_0_) ->
    {
        return !p_213463_0_.isDiscrete() && EntityPredicates.CAN_AI_TARGET.test(p_213463_0_);
    };
    private static final Predicate<ItemEntity> TRUSTED_TARGET_SELECTOR = (p_213489_0_) ->
    {
        return !p_213489_0_.cannotPickup() && p_213489_0_.isAlive();
    };
    private float shouting_timer;
    private float villager_absorb;
    private boolean summoned;

    public KitsuneEntity(EntityType<? extends CreatureEntity> type, World worldIn)
    {
        super(type, worldIn);
        this.lookController = new KitsuneEntity.LookHelperController();
        this.moveController = new KitsuneEntity.MoveHelperController();
        this.setPathPriority(PathNodeType.DANGER_OTHER, 0.0F);
        this.setPathPriority(PathNodeType.DAMAGE_OTHER, 0.0F);
        this.setCanPickUpLoot(true);
        shouting_timer = 0.0F;
        villager_absorb = 0.0F;
        summoned = false;
    }

    @Override
    protected void registerGoals()
    {
        this.targetSelector.addGoal(2, (new HurtByTargetGoal(this)));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, AnimalEntity.class, 10, false, false, IS_PREY));

        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(2, new KitsuneEntity.LeapGoal(this, 0.4F));
        this.goalSelector.addGoal(3, new KitsuneEntity.BiteGoal(this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue(), true));
        this.goalSelector.addGoal(3, new KitsuneEntity.JumpGoal());
        this.goalSelector.addGoal(4, new KitsuneEntity.AvoidPlayerGoal(this, PlayerEntity.class, 16.0F, this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue(), this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue(), (p_213497_1_) ->
        {
            return SHOULD_AVOID.test(p_213497_1_) && this.getAttackTarget() == null;
        }));
        this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(8, new KitsuneEntity.MoveToVillageGoal(this, this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue()));
        this.goalSelector.addGoal(9, new KitsuneEntity.StrollGoal(32, 200));
        this.goalSelector.addGoal(11, new WaterAvoidingRandomWalkingGoal(this, this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue()));
        this.goalSelector.addGoal(11, new KitsuneEntity.FindItemsGoal());
        this.goalSelector.addGoal(12, new LookAtGoal(this, VillagerEntity.class, 24.0F));
    }

    @Override
    protected void registerData()
    {
        super.registerData();
        this.dataManager.register(VILLAGER_FORM, false);
        this.dataManager.register(SHOUTING, false);
        this.dataManager.register(FOX_PHASE, 3);
        this.dataManager.register(FOX_FLAGS, (byte) 0);
        this.dataManager.register(SHAKE_HEAD_TICKS, 0);
    }

    @Override
    protected void registerAttributes()
    {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(60.0D);
        this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
        this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
    }

    public boolean isVillagerForm()
    {
        return this.dataManager.get(VILLAGER_FORM);
    }

    public void setVillagerForm(boolean isVillagerForm)
    {
        this.dataManager.set(VILLAGER_FORM, isVillagerForm);
    }

    public int getShakeHeadTicks()
    {
        return this.dataManager.get(SHAKE_HEAD_TICKS);
    }

    public void setShakeHeadTicks(int shakeTick)
    {
        this.dataManager.set(SHAKE_HEAD_TICKS, shakeTick);
    }

    public boolean isShouting()
    {
        return this.dataManager.get(SHOUTING);
    }

    public void setShouting(boolean isShouting)
    {
        this.dataManager.set(SHOUTING, isShouting);
    }

    public int getFoxPhase()
    {
        return this.dataManager.get(FOX_PHASE);
    }

    public void setFoxPhase(int foxPhaseIn)
    {
        this.dataManager.set(FOX_PHASE, foxPhaseIn);
    }

    public boolean isStuck()
    {
        return this.getFoxFlag(64);
    }

    private void setStuck(boolean p_213492_1_)
    {
        this.setFoxFlag(64, p_213492_1_);
    }

    private void setFoxFlag(int p_213505_1_, boolean p_213505_2_)
    {
        if (p_213505_2_)
        {
            this.dataManager.set(FOX_FLAGS, (byte) (this.dataManager.get(FOX_FLAGS) | p_213505_1_));
        } else
        {
            this.dataManager.set(FOX_FLAGS, (byte) (this.dataManager.get(FOX_FLAGS) & ~p_213505_1_));
        }

    }

    private boolean getFoxFlag(int p_213507_1_)
    {
        return (this.dataManager.get(FOX_FLAGS) & p_213507_1_) != 0;
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

    @Override
    public void tick()
    {
        super.tick();

        if (this.getShakeHeadTicks() > 0)
        {
            this.setShakeHeadTicks(this.getShakeHeadTicks() - 1);
        }

        if (shouting_timer > 0 && !isShouting())
        {
            setShouting(true);
        } else if (shouting_timer <= 0 && isShouting())
        {
            setShouting(false);
        }

        if (this.isShouting() || this.isMovementBlocked())
        {
            this.isJumping = false;
            this.moveStrafing = 0.0F;
            this.moveForward = 0.0F;

            List<LivingEntity> entities = EntityHelper.INSTANCE.getEntityLivingBaseNearby(this, 10, 3, 10, 10);
            for (LivingEntity entity : entities)
            {
                if (!(entity instanceof PlayerEntity && ((PlayerEntity) entity).isCreative()))
                {
                    double angle = (EntityHelper.INSTANCE.getAngleBetweenEntities(this, entity) + 90) * Math.PI / 180;
                    double distance = getDistance(entity);
                    entity.setMotion(
                            entity.getMotion().getX() + Math.min(1 / (distance * distance), 1) * -2 * Math.cos(angle),
                            entity.getMotion().getY(),
                            entity.getMotion().getZ() + Math.min(1 / (distance * distance), 1) * -2 * Math.sin(angle));


                    int i = 0;
                    if (this.world.getDifficulty() == Difficulty.NORMAL)
                    {
                        i = 5;
                    } else if (this.world.getDifficulty() == Difficulty.HARD)
                    {
                        i = 10;
                    }

                    entity.addPotionEffect(new EffectInstance(Effects.BLINDNESS, 20 * i, 1));
                }
            }

            if (shouting_timer % 5 == 0 && shouting_timer >= 5 && !summoned)
            {
                if (!world.isRemote)
                {
                    FoxHeadEntity foxHead = new FoxHeadEntity(this.world, this, getAttackTarget());
                    foxHead.setPosition(getPosX() + ((shouting_timer / 5 == 3 || shouting_timer / 5 == 1) ? ((shouting_timer / 5 == 1) ? -2.5F : 2.5F) : 0.0F), getPosY() + 2.5F, getPosZ() + ((shouting_timer / 5 == 2) ? 2.5F : 0.0F));
                    this.world.addEntity(foxHead);
                    this.playSound(SoundEvents.BLOCK_BELL_USE, 5.0F, 10.0F);
                }
                summoned = true;
            }

            if (ticksExisted % 2 == 0)
            {
                --shouting_timer;
                summoned = false;
            }
        }

        if (!isShouting() && (getFoxPhase() == 3 && getHealth() <= 40 || getFoxPhase() == 2 && getHealth() <= 20 || getFoxPhase() == 1 && getHealth() <= 0))
        {
            setFoxPhase(getFoxPhase() - 1);
            shouting_timer = 20.0F;
            if (!world.isRemote)
            {
                this.playSound(SoundEvents.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, 4.0F, 10.0F);

                for (int i = 1; i <= 180; ++i)
                {
                    double yaw = i * 360 / 180;
                    double speed = 1.5;
                    double xSpeed = speed * Math.cos(Math.toRadians(yaw));
                    double zSpeed = speed * Math.sin(Math.toRadians(yaw));

                    SpawnParticleMessage spawnParticleMessage = new SpawnParticleMessage(new Vec3d(getPosX(), getPosY() + 0.5F, getPosZ()), new Vec3d(xSpeed, 0.0D, zSpeed), 3, 4, 0.0F);
                    MWAWPacketHandler.packetHandler.sendToDimension(this.dimension, spawnParticleMessage);
                }
            }
        }

        if (!world.isRemote)
        {
            if (!isVillagerForm() && villager_absorb >= 100.0F && world.isDaytime())
            {
                changingForm(true);
                spitOutItem(this.getItemStackFromSlot(EquipmentSlotType.MAINHAND));
            } else if (isVillagerForm() && world.isNightTime())
            {
                changingForm(false);
            }

            if (this.isStuck() && this.world.rand.nextFloat() < 0.2F)
            {
                BlockPos blockpos = new BlockPos(this);
                BlockState blockstate = this.world.getBlockState(blockpos);
                this.world.playEvent(2001, blockpos, Block.getStateId(blockstate));
            }

            if (ticksExisted % 3 == 0 && !isVillagerForm() && world.isDaytime())
            {
                if (getAttackTarget() == null)
                {
                    List<LivingEntity> entities = EntityHelper.INSTANCE.getEntityLivingBaseNearby(this, 10, 3, 10, 10);
                    for (LivingEntity entity : entities)
                    {
                        if (entity instanceof VillagerEntity)
                        {
                            villager_absorb += 0.5F;
                            SpawnSuckingParticleMessage spawnParticleMessage = new SpawnSuckingParticleMessage(new Vec3d(entity.getPosX(), entity.getPosY() + 0.75F, entity.getPosZ()), new Vec3d(0.1D, 0.1D, 0.1D), 5, 0, 0.5F);
                            MWAWPacketHandler.packetHandler.sendToDimension(this.dimension, spawnParticleMessage);
                            entity.playSound(SoundEvents.BLOCK_NOTE_BLOCK_CHIME, 0.5F, 10.0F);
                        }
                    }
                }
            }
        }
    }

    private void changingForm(boolean change)
    {
        setVillagerForm(change);
        SpawnSuckingParticleMessage spawnSuckingParticleMessage = new SpawnSuckingParticleMessage(new Vec3d(this.getPosX(), this.getPosY() + 0.75F, this.getPosZ()), new Vec3d(0.1D, 0.1D, 0.1D), 5, 0, 0.5F);
        MWAWPacketHandler.packetHandler.sendToDimension(this.dimension, spawnSuckingParticleMessage);

        SpawnParticleMessage spawnParticleMessage = new SpawnParticleMessage(new Vec3d(this.getPosX(), this.getPosY() + 0.75F, this.getPosZ()), new Vec3d(0.0D, 0.1D, 0.0D), 10, 4, 0.5F);
        MWAWPacketHandler.packetHandler.sendToDimension(this.dimension, spawnParticleMessage);

        this.playSound(SoundEvents.ENTITY_PUFFER_FISH_BLOW_OUT, 1.5F, 10.0F);
    }

    @Override
    public void handleStatusUpdate(byte id)
    {
        if (id == 45)
        {
            ItemStack itemstack = this.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
            if (!itemstack.isEmpty())
            {
                for (int i = 0; i < 8; ++i)
                {
                    Vec3d vec3d = (new Vec3d(((double) this.rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D)).rotatePitch(-this.rotationPitch * ((float) Math.PI / 180F)).rotateYaw(-this.rotationYaw * ((float) Math.PI / 180F));
                    this.world.addParticle(new ItemParticleData(ParticleTypes.ITEM, itemstack), this.getPosX() + this.getLookVec().x / 2.0D, this.getPosY(), this.getPosZ() + this.getLookVec().z / 2.0D, vec3d.x, vec3d.y + 0.05D, vec3d.z);
                }
            }
        } else
        {
            super.handleStatusUpdate(id);
        }
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn)
    {
        return this.isChild() ? sizeIn.height * 0.85F : 0.4F;
    }

    @Override
    protected boolean canEquipItem(ItemStack stack)
    {
        Item item = stack.getItem();
        ItemStack itemstack = this.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
        return itemstack.isEmpty();
    }

    private void spitOutItem(ItemStack stackIn)
    {
        if (!stackIn.isEmpty() && !this.world.isRemote)
        {
            ItemEntity itementity = new ItemEntity(this.world, this.getPosX() + this.getLookVec().x, this.getPosY() + 1.0D, this.getPosZ() + this.getLookVec().z, stackIn);
            itementity.setPickupDelay(40);
            itementity.setThrowerId(this.getUniqueID());
            this.playSound(SoundEvents.ENTITY_FOX_SPIT, 1.0F, 1.0F);
            this.world.addEntity(itementity);
            this.setItemStackToSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
        }
    }

    private void shakeHead()
    {
        this.setShakeHeadTicks(40);
        if (!this.world.isRemote())
        {
            this.playSound(SoundEvents.ENTITY_VILLAGER_NO, this.getSoundVolume(), this.getSoundPitch());
        }
    }

    private void spawnItem(ItemStack stackIn)
    {
        ItemEntity itementity = new ItemEntity(this.world, this.getPosX(), this.getPosY(), this.getPosZ(), stackIn);
        this.world.addEntity(itementity);
    }

    @Override
    protected void updateEquipmentIfNeeded(ItemEntity itemEntity)
    {
        if (!isVillagerForm())
        {
            ItemStack itemstack = itemEntity.getItem();
            if (this.canEquipItem(itemstack))
            {
                int i = itemstack.getCount();
                if (i > 1)
                {
                    this.spawnItem(itemstack.split(i - 1));
                }

                this.spitOutItem(this.getItemStackFromSlot(EquipmentSlotType.MAINHAND));
                this.setItemStackToSlot(EquipmentSlotType.MAINHAND, itemstack.split(1));
                this.inventoryHandsDropChances[EquipmentSlotType.MAINHAND.getIndex()] = 2.0F;
                this.onItemPickup(itemEntity, itemstack.getCount());
                itemEntity.remove();
            }
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (isVillagerForm())
        {
            changingForm(false);
            villager_absorb = 0.0F;
        }

        return super.attackEntityFrom(source, amount);
    }

    @Override
    public boolean processInteract(PlayerEntity player, Hand hand)
    {
        if (isVillagerForm())
            this.shakeHead();
        return super.processInteract(player, hand);
    }

    @Override
    protected void spawnDrops(DamageSource damageSourceIn)
    {
        ItemStack itemstack = this.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
        if (!itemstack.isEmpty())
        {
            this.entityDropItem(itemstack);
            this.setItemStackToSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
        }

        super.spawnDrops(damageSourceIn);
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return isVillagerForm() ? SoundEvents.ENTITY_VILLAGER_AMBIENT : null;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return isVillagerForm() ? SoundEvents.ENTITY_VILLAGER_HURT : SoundEvents.ENTITY_FOX_HURT;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return isVillagerForm() ? SoundEvents.ENTITY_VILLAGER_DEATH : SoundEvents.ENTITY_FOX_DEATH;
    }

    class JumpGoal extends Goal
    {
        int delay;

        public JumpGoal()
        {
            this.setMutexFlags(EnumSet.of(Goal.Flag.LOOK, Goal.Flag.JUMP, Goal.Flag.MOVE));
        }

        @Override
        public boolean shouldExecute()
        {
            return KitsuneEntity.this.isStuck() && !KitsuneEntity.this.isVillagerForm();
        }

        @Override
        public boolean shouldContinueExecuting()
        {
            return this.shouldExecute() && this.delay > 0;
        }

        @Override
        public void startExecuting()
        {
            this.delay = 40;
        }

        @Override
        public void resetTask()
        {
            KitsuneEntity.this.setStuck(false);
        }

        @Override
        public void tick()
        {
            --this.delay;
        }
    }

    class MoveHelperController extends MovementController
    {
        public MoveHelperController()
        {
            super(KitsuneEntity.this);
        }

        @Override
        public void tick()
        {
            if (!KitsuneEntity.this.isShouting())
            {
                super.tick();
            }
        }
    }

    public class LookHelperController extends LookController
    {
        public LookHelperController()
        {
            super(KitsuneEntity.this);
        }

        @Override
        public void tick()
        {
            if (!KitsuneEntity.this.isShouting())
            {
                super.tick();
            }

        }
    }

    class MoveToVillageGoal extends MoveTowardsVillageGoal
    {
        public MoveToVillageGoal(CreatureEntity p_i50325_1_, double p_i50325_2_)
        {
            super(p_i50325_1_, p_i50325_2_);
        }

        @Override
        public boolean shouldExecute()
        {
            return super.shouldExecute() && this.check();
        }

        @Override
        public boolean shouldContinueExecuting()
        {
            return super.shouldContinueExecuting() && this.check();
        }

        private boolean check()
        {
            return !KitsuneEntity.this.isSleeping() && KitsuneEntity.this.getAttackTarget() == null;
        }
    }

    class StrollGoal extends MoveThroughVillageAtNightGoal
    {
        public StrollGoal(int p_i50726_2_, int p_i50726_3_)
        {
            super(KitsuneEntity.this, p_i50726_3_);
        }

        @Override
        public boolean shouldExecute()
        {
            return super.shouldExecute() && this.check();
        }

        @Override
        public boolean shouldContinueExecuting()
        {
            return super.shouldContinueExecuting() && this.check();
        }

        private boolean check()
        {
            return !KitsuneEntity.this.isSleeping() && KitsuneEntity.this.getAttackTarget() == null;
        }
    }

    class LeapGoal extends LeapAtTargetGoal
    {

        public LeapGoal(MobEntity leapingEntity, float leapMotionYIn)
        {
            super(leapingEntity, leapMotionYIn);
        }

        @Override
        public boolean shouldExecute()
        {
            return !KitsuneEntity.this.isShouting() && !KitsuneEntity.this.isVillagerForm() && super.shouldExecute();
        }
    }

    class BiteGoal extends MeleeAttackGoal
    {
        public BiteGoal(double p_i50731_2_, boolean p_i50731_4_)
        {
            super(KitsuneEntity.this, p_i50731_2_, p_i50731_4_);
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr)
        {
            double d0 = this.getAttackReachSqr(enemy);
            if (distToEnemySqr <= d0 && this.attackTick <= 0)
            {
                this.attackTick = 20;
                this.attacker.attackEntityAsMob(enemy);
                KitsuneEntity.this.playSound(SoundEvents.ENTITY_FOX_BITE, 1.0F, 1.0F);
            }

        }

        @Override
        public boolean shouldExecute()
        {
            return !KitsuneEntity.this.isShouting() && !KitsuneEntity.this.isVillagerForm() && super.shouldExecute();
        }
    }

    class AvoidPlayerGoal extends AvoidEntityGoal
    {
        public AvoidPlayerGoal(CreatureEntity entityIn, Class classToAvoidIn, float avoidDistanceIn, double farSpeedIn, double nearSpeedIn)
        {
            super(entityIn, classToAvoidIn, avoidDistanceIn, farSpeedIn, nearSpeedIn);
        }

        public AvoidPlayerGoal(CreatureEntity entityIn, Class<PlayerEntity> avoidClass, float distance, double nearSpeedIn, double farSpeedIn, Predicate<LivingEntity> targetPredicate)
        {
            super(entityIn, avoidClass, distance, nearSpeedIn, farSpeedIn, targetPredicate);
        }

        @Override
        public boolean shouldExecute()
        {
            return super.shouldExecute() && !isVillagerForm();
        }
    }

    class FindItemsGoal extends Goal
    {
        public FindItemsGoal()
        {
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean shouldExecute()
        {
            if (!KitsuneEntity.this.getItemStackFromSlot(EquipmentSlotType.MAINHAND).isEmpty())
            {
                return false;
            } else if (KitsuneEntity.this.getAttackTarget() == null && KitsuneEntity.this.getRevengeTarget() == null)
            {
                if (KitsuneEntity.this.getRNG().nextInt(10) != 0)
                {
                    return false;
                } else
                {
                    List<ItemEntity> list = KitsuneEntity.this.world.getEntitiesWithinAABB(ItemEntity.class, KitsuneEntity.this.getBoundingBox().grow(8.0D, 8.0D, 8.0D), KitsuneEntity.TRUSTED_TARGET_SELECTOR);
                    return !list.isEmpty() && KitsuneEntity.this.getItemStackFromSlot(EquipmentSlotType.MAINHAND).isEmpty() && !KitsuneEntity.this.isVillagerForm();
                }
            } else
            {
                return false;
            }
        }

        @Override
        public void tick()
        {
            List<ItemEntity> list = KitsuneEntity.this.world.getEntitiesWithinAABB(ItemEntity.class, KitsuneEntity.this.getBoundingBox().grow(8.0D, 8.0D, 8.0D), KitsuneEntity.TRUSTED_TARGET_SELECTOR);
            ItemStack itemstack = KitsuneEntity.this.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
            if (itemstack.isEmpty() && !list.isEmpty())
            {
                KitsuneEntity.this.getNavigator().tryMoveToEntityLiving(list.get(0), KitsuneEntity.this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue());
            }

        }

        @Override
        public void startExecuting()
        {
            List<ItemEntity> list = KitsuneEntity.this.world.getEntitiesWithinAABB(ItemEntity.class, KitsuneEntity.this.getBoundingBox().grow(8.0D, 8.0D, 8.0D), KitsuneEntity.TRUSTED_TARGET_SELECTOR);
            if (!list.isEmpty())
            {
                KitsuneEntity.this.getNavigator().tryMoveToEntityLiving(list.get(0), KitsuneEntity.this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue());
            }

        }
    }
}