package com.hoopawolf.mwaw.entities;

import com.hoopawolf.mwaw.entities.ai.RangedBowAttackHunterGoal;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class HunterEntity extends CreatureEntity implements IRangedAttackMob
{
    private static final Predicate<ItemEntity> TRUSTED_TARGET_SELECTOR = (p_213489_0_) ->
    {
        return (p_213489_0_.getItem().isFood())
                && (!p_213489_0_.cannotPickup() && p_213489_0_.isAlive());
    };

    private final Item[] WEAPON =
            {
                    Items.BOW
            };

    private final Item[] BODY_ARMOR =
            {
                    Items.LEATHER_CHESTPLATE,
            };

    private final Item[] LEGGING_ARMOR =
            {
                    Items.LEATHER_LEGGINGS,
            };

    private final Item[] BOOTS_ARMOR =
            {
                    Items.LEATHER_BOOTS,
            };

    public HunterEntity(EntityType<? extends HunterEntity> type, World worldIn)
    {
        super(type, worldIn);

        this.setCanPickUpLoot(true);
    }

    @Override
    protected void registerData()
    {
        super.registerData();
    }

    @Override
    protected void registerGoals()
    {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setCallsForHelp());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, DendroidEntity.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AnimalEntity.class, true));

        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(4, new RangedBowAttackHunterGoal(this, this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue(), 20, 15.0F));
        this.goalSelector.addGoal(5, new FindItemsGoal());
        this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(10, new LookAtGoal(this, PlayerEntity.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtGoal(this, MobEntity.class, 8.0F));
        this.goalSelector.addGoal(11, new WaterAvoidingRandomWalkingGoal(this, this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue()));
    }

    @Override
    protected void registerAttributes()
    {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.525D);
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
    }

    @Override
    protected boolean isDespawnPeaceful()
    {
        return false;
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
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_PLAYER_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_PLAYER_HURT;
    }

    @Override
    protected void updateAITasks()
    {

    }

    @Override
    public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, ILivingEntityData spawnDataIn, CompoundNBT dataTag)
    {
        this.setEquipmentBasedOnDifficulty(difficultyIn);
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Override
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty)
    {
        ItemStack
                weapon = null,
                chest = null,
                legging = null,
                boot = null;

        int random_weapon = rand.nextInt(WEAPON.length);
        weapon = new ItemStack(WEAPON[random_weapon]);

        if (this.rand.nextInt(50) < 25)
        {
            int random = rand.nextInt(BODY_ARMOR.length);
            chest = new ItemStack(BODY_ARMOR[random]);
        }

        if (this.rand.nextInt(50) < 25)
        {
            int random = rand.nextInt(LEGGING_ARMOR.length);
            legging = new ItemStack(LEGGING_ARMOR[random]);
        }

        if (this.rand.nextInt(50) < 25)
        {
            int random = rand.nextInt(BOOTS_ARMOR.length);
            boot = new ItemStack(BOOTS_ARMOR[random]);
        }

        if (weapon != null)
            this.setItemStackToSlot(EquipmentSlotType.MAINHAND, weapon);

        if (chest != null)
            this.setItemStackToSlot(EquipmentSlotType.CHEST, chest);

        if (legging != null)
            this.setItemStackToSlot(EquipmentSlotType.LEGS, legging);

        if (boot != null)
            this.setItemStackToSlot(EquipmentSlotType.FEET, boot);
    }


    private void spawnItem(ItemStack stackIn)
    {
        ItemEntity itementity = new ItemEntity(this.world, this.getPosX(), this.getPosY(), this.getPosZ(), stackIn);
        this.world.addEntity(itementity);
    }

    @Override
    protected void updateEquipmentIfNeeded(ItemEntity itemEntity)
    {
        ItemStack itemstack = itemEntity.getItem();
        if (this.canEquipItem(itemstack))
        {
            this.setItemStackToSlot(EquipmentSlotType.OFFHAND, itemstack);
            this.onItemPickup(itemEntity, itemstack.getCount());
            itemEntity.remove();
        }
    }

    @Override
    protected boolean canEquipItem(ItemStack stack)
    {
        Item item = stack.getItem();
        ItemStack itemstack = this.getItemStackFromSlot(EquipmentSlotType.OFFHAND);
        return itemstack.isEmpty() && item.isFood();
    }

    private void eatFoodOnOffHand(float healAmount)
    {
        if (this.getHeldItemOffhand().isFood())
        {
            this.heal(healAmount);
            ItemStack itemstack = this.getItemStackFromSlot(EquipmentSlotType.OFFHAND);
            ItemStack itemstack1 = itemstack.onItemUseFinish(this.world, this);
            if (!itemstack1.isEmpty())
            {
                this.setItemStackToSlot(EquipmentSlotType.OFFHAND, itemstack1);
            }

            this.playSound(this.getEatSound(itemstack), 1.0F, 1.0F);
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {

        if (source.getTrueSource() instanceof HunterEntity)
            return false;

        if (ticksExisted % 2 == 0)
            eatFoodOnOffHand(getMaxHealth() * 0.2F);

        return super.attackEntityFrom(source, amount);
    }

    @Override
    public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor)
    {
        ItemStack itemstack = this.findAmmo(this.getHeldItem(ProjectileHelper.getHandWith(this, Items.BOW)));
        AbstractArrowEntity abstractarrowentity = this.fireArrow(itemstack, distanceFactor);
        if (this.getHeldItemMainhand().getItem() instanceof net.minecraft.item.BowItem)
            abstractarrowentity = ((net.minecraft.item.BowItem) this.getHeldItemMainhand().getItem()).customeArrow(abstractarrowentity);
        double d0 = target.getPosX() - this.getPosX();
        double d1 = target.getPosYHeight(0.3333333333333333D) - abstractarrowentity.getPosY();
        double d2 = target.getPosZ() - this.getPosZ();
        double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
        abstractarrowentity.shoot(d0, d1 + d3 * (double) 0.2F, d2, 1.6F, (float) (14 - this.world.getDifficulty().getId() * 4));
        this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.world.addEntity(abstractarrowentity);
    }

    protected AbstractArrowEntity fireArrow(ItemStack arrowStack, float distanceFactor)
    {
        return ProjectileHelper.fireArrow(this, arrowStack, distanceFactor);
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
            if (!HunterEntity.this.getItemStackFromSlot(EquipmentSlotType.OFFHAND).isEmpty())
            {
                return false;
            } else if (HunterEntity.this.getAttackTarget() == null && HunterEntity.this.getRevengeTarget() == null)
            {
                if (HunterEntity.this.getRNG().nextInt(10) != 0)
                {
                    return false;
                } else
                {
                    List<ItemEntity> list = HunterEntity.this.world.getEntitiesWithinAABB(ItemEntity.class, HunterEntity.this.getBoundingBox().grow(8.0D, 8.0D, 8.0D), HunterEntity.TRUSTED_TARGET_SELECTOR);
                    return !list.isEmpty() && HunterEntity.this.getItemStackFromSlot(EquipmentSlotType.OFFHAND).isEmpty();
                }
            } else
            {
                return false;
            }
        }

        @Override
        public void tick()
        {
            List<ItemEntity> list = HunterEntity.this.world.getEntitiesWithinAABB(ItemEntity.class, HunterEntity.this.getBoundingBox().grow(8.0D, 8.0D, 8.0D), HunterEntity.TRUSTED_TARGET_SELECTOR);
            ItemStack itemstack = HunterEntity.this.getItemStackFromSlot(EquipmentSlotType.OFFHAND);
            if (itemstack.isEmpty() && !list.isEmpty())
            {
                HunterEntity.this.getNavigator().tryMoveToEntityLiving(list.get(0), HunterEntity.this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue());
            }
        }

        @Override
        public void startExecuting()
        {
            List<ItemEntity> list = HunterEntity.this.world.getEntitiesWithinAABB(ItemEntity.class, HunterEntity.this.getBoundingBox().grow(8.0D, 8.0D, 8.0D), HunterEntity.TRUSTED_TARGET_SELECTOR);
            if (!list.isEmpty())
            {
                HunterEntity.this.getNavigator().tryMoveToEntityLiving(list.get(0), HunterEntity.this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue());
            }

        }
    }
}