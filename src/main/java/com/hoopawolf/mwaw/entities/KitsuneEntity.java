package com.hoopawolf.mwaw.entities;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

//TODO SUCKING PARTICLE FROM VILLAGER POSITION TO FOX POSTION, SLOWING INCREASING TRANSFORM TIMER WHEN VILLAGER IN AREA AND FOLLOWING VILLAGER WHILE THIS HAPPEN, WHEN ALMOST FINISH, SUMMON SMOKE TO COVER FOX BODY, ONCE TRANSFORM, SET VILLAGER LAST POSITION
//TODO SO AS TO KEEP TRACK OF WHERE VILLAGE IS, CHECK OUT MOVE THROUGH VILLAGE GOAL
//TODO SHOUT THEN SUMMON GHOST SPIRIT TO ATTACK, MAYBE PUSH BACK ANYONE AROUND IT
public class KitsuneEntity extends CreatureEntity
{
    private static final DataParameter<Boolean> VILLAGER_FORM = EntityDataManager.createKey(KitsuneEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> FOX_PHASE = EntityDataManager.createKey(KitsuneEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Byte> FOX_FLAGS = EntityDataManager.createKey(KitsuneEntity.class, DataSerializers.BYTE);
    private float shouting_timer;

    private BlockPos villager_pos;

    private static final Predicate<Entity> STALKABLE_PREY = (p_213470_0_) ->
    {
        if (!(p_213470_0_ instanceof LivingEntity))
        {
            return false;
        } else
        {
            LivingEntity livingentity = (LivingEntity) p_213470_0_;
            return livingentity.getLastAttackedEntity() != null && livingentity.getLastAttackedEntityTime() < livingentity.ticksExisted + 600;
        }
    };
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

    public KitsuneEntity(EntityType<? extends CreatureEntity> type, World worldIn)
    {
        super(type, worldIn);
        this.lookController = new KitsuneEntity.LookHelperController();
        this.moveController = new KitsuneEntity.MoveHelperController();
        this.setPathPriority(PathNodeType.DANGER_OTHER, 0.0F);
        this.setPathPriority(PathNodeType.DAMAGE_OTHER, 0.0F);
        this.setCanPickUpLoot(true);
        shouting_timer = 0.0F;
    }

    protected void registerGoals()
    {
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, AnimalEntity.class, 10, false, false, (p_213487_0_) ->
        {
            return p_213487_0_ instanceof ChickenEntity || p_213487_0_ instanceof SheepEntity || p_213487_0_ instanceof WolfEntity;
        }));

        this.goalSelector.addGoal(0, new SwimGoal(this));
        // this.goalSelector.addGoal(1, new KitsuneEntity.JumpGoal());
        this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue()));
        this.goalSelector.addGoal(7, new LookRandomlyGoal(this));

    }

    protected void registerData()
    {
        super.registerData();
        this.dataManager.register(VILLAGER_FORM, false);
        this.dataManager.register(FOX_PHASE, 3);
    }

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

    public int getFoxPhase()
    {
        return this.dataManager.get(FOX_PHASE);
    }

    public void setFoxPhase(int foxPhaseIn)
    {
        this.dataManager.set(FOX_PHASE, foxPhaseIn);
    }

    public boolean isShouting()
    {
        return shouting_timer > 0;
    }

    public void writeAdditional(CompoundNBT compound)
    {
        super.writeAdditional(compound);
        if (this.hasVillagerPos())
        {
            compound.put("VillagerPos", NBTUtil.writeBlockPos(this.getVillagerPos()));
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readAdditional(CompoundNBT compound)
    {
        super.readAdditional(compound);

        this.villager_pos = null;
        if (compound.contains("VillagerPos"))
        {
            this.villager_pos = NBTUtil.readBlockPos(compound.getCompound("VillagerPos"));
        }
    }

    @Nullable
    public BlockPos getVillagerPos()
    {
        return this.villager_pos;
    }

    public boolean hasVillagerPos()
    {
        return this.getVillagerPos() != null;
    }

    @Nullable
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_FOX_HURT;
    }

    @Nullable
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_FOX_DEATH;
    }

    //TODO GOD DAMMIT
//    class JumpGoal extends Goal {
//        int delay;
//
//        public JumpGoal() {
//            this.setMutexFlags(EnumSet.of(Goal.Flag.LOOK, Goal.Flag.JUMP, Goal.Flag.MOVE));
//        }
//
//        /**
//         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
//         * method as well.
//         */
//        public boolean shouldExecute() {
//            return KitsuneEntity.this.isStuck();
//        }
//
//        /**
//         * Returns whether an in-progress EntityAIBase should continue executing
//         */
//        public boolean shouldContinueExecuting() {
//            return this.shouldExecute() && this.delay > 0;
//        }
//
//        /**
//         * Execute a one shot task or start executing a continuous task
//         */
//        public void startExecuting() {
//            this.delay = 40;
//        }
//
//        /**
//         * Reset the task's internal state. Called when this task is interrupted by another one
//         */
//        public void resetTask() {
//            KitsuneEntity.this.setStuck(false);
//        }
//
//        /**
//         * Keep ticking a continuous task that has already been started
//         */
//        public void tick() {
//            --this.delay;
//        }
//    }

    class MoveHelperController extends MovementController
    {
        public MoveHelperController()
        {
            super(KitsuneEntity.this);
        }

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

        /**
         * Updates look
         */
        public void tick()
        {
            if (!KitsuneEntity.this.isShouting())
            {
                super.tick();
            }

        }
    }

    class BiteGoal extends MeleeAttackGoal
    {
        public BiteGoal(double p_i50731_2_, boolean p_i50731_4_)
        {
            super(KitsuneEntity.this, p_i50731_2_, p_i50731_4_);
        }

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

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean shouldExecute()
        {
            return !KitsuneEntity.this.isShouting() && super.shouldExecute();
        }
    }

    class FindItemsGoal extends Goal
    {
        public FindItemsGoal()
        {
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
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
                    return !list.isEmpty() && KitsuneEntity.this.getItemStackFromSlot(EquipmentSlotType.MAINHAND).isEmpty();
                }
            } else
            {
                return false;
            }
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void tick()
        {
            List<ItemEntity> list = KitsuneEntity.this.world.getEntitiesWithinAABB(ItemEntity.class, KitsuneEntity.this.getBoundingBox().grow(8.0D, 8.0D, 8.0D), KitsuneEntity.TRUSTED_TARGET_SELECTOR);
            ItemStack itemstack = KitsuneEntity.this.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
            if (itemstack.isEmpty() && !list.isEmpty())
            {
                KitsuneEntity.this.getNavigator().tryMoveToEntityLiving(list.get(0), (double) 1.2F);
            }

        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void startExecuting()
        {
            List<ItemEntity> list = KitsuneEntity.this.world.getEntitiesWithinAABB(ItemEntity.class, KitsuneEntity.this.getBoundingBox().grow(8.0D, 8.0D, 8.0D), KitsuneEntity.TRUSTED_TARGET_SELECTOR);
            if (!list.isEmpty())
            {
                KitsuneEntity.this.getNavigator().tryMoveToEntityLiving(list.get(0), (double) 1.2F);
            }

        }
    }
}