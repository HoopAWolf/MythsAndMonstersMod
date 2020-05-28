package com.hoopawolf.mwaw.entities;

import com.hoopawolf.mwaw.ref.Reference;
import com.hoopawolf.mwaw.util.RegistryHandler;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class HunterEntity extends CreatureEntity implements IRangedAttackMob
{
    private final Item[] WEAPON =
            {
                    Items.STONE_SWORD,
                    Items.IRON_SWORD,
                    Items.DIAMOND_SWORD,
                    RegistryHandler.MARROW_SWORD.get(),
                    RegistryHandler.DENDROID_SWORD.get(),
                    RegistryHandler.BONE_DAGGER.get(),
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

        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(10, new LookAtGoal(this, PlayerEntity.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtGoal(this, MobEntity.class, 8.0F));
        this.goalSelector.addGoal(11, new WaterAvoidingRandomWalkingGoal(this, this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue()));
    }

    @Override
    protected void registerData()
    {
        super.registerData();
    }

    @Override
    protected void registerGoals()
    {

    }

    @Override
    protected void registerAttributes()
    {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.35D);
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
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

        if (this.rand.nextInt(50) < 25)
        {
            int random = rand.nextInt(WEAPON.length);
            weapon = new ItemStack(WEAPON[random]);
            Reference.LOGGER.debug("HAVE: " + weapon.toString());
        }

        if (this.rand.nextInt(50) < 25)
        {
            int random = rand.nextInt(BODY_ARMOR.length);
            chest = new ItemStack(BODY_ARMOR[random]);
            Reference.LOGGER.debug("HAVE: " + chest.toString());
        }

        if (this.rand.nextInt(50) < 25)
        {
            int random = rand.nextInt(LEGGING_ARMOR.length);
            legging = new ItemStack(LEGGING_ARMOR[random]);
            Reference.LOGGER.debug("HAVE: " + legging.toString());
        }

        if (this.rand.nextInt(50) < 25)
        {
            int random = rand.nextInt(BOOTS_ARMOR.length);
            boot = new ItemStack(BOOTS_ARMOR[random]);
            Reference.LOGGER.debug("HAVE: " + boot.toString());
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

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {

        return super.attackEntityFrom(source, amount);
    }

    @Override
    public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor)
    {

    }
}