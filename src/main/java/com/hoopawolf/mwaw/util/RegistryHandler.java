package com.hoopawolf.mwaw.util;

import com.hoopawolf.mwaw.blocks.FairyMushroomBlock;
import com.hoopawolf.mwaw.entities.*;
import com.hoopawolf.mwaw.entities.projectiles.GoldenArrowEntity;
import com.hoopawolf.mwaw.entities.projectiles.SapEntity;
import com.hoopawolf.mwaw.items.ItemBase;
import com.hoopawolf.mwaw.items.MWAWSpawnEggItem;
import com.hoopawolf.mwaw.items.ShardItem;
import com.hoopawolf.mwaw.items.weapons.DendroidSwordItem;
import com.hoopawolf.mwaw.items.weapons.GoldenArrowItem;
import com.hoopawolf.mwaw.items.weapons.GoldenBowItem;
import com.hoopawolf.mwaw.items.weapons.MarrowSwordItem;
import com.hoopawolf.mwaw.ref.Reference;
import com.hoopawolf.mwaw.tab.MWAWItemGroup;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemTier;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.blockplacer.SimpleBlockPlacer;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.BlockClusterFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;


public class RegistryHandler
{
    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, Reference.MOD_ID);
    public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, Reference.MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = new DeferredRegister<>(ForgeRegistries.ENTITIES, Reference.MOD_ID);
    public static final DeferredRegister<ParticleType<?>> PARTICLES = new DeferredRegister<>(ForgeRegistries.PARTICLE_TYPES, Reference.MOD_ID);

    public static void init(IEventBus _iEventBus)
    {
        ITEMS.register(_iEventBus);
        BLOCKS.register(_iEventBus);
        ENTITIES.register(_iEventBus);
        PARTICLES.register(_iEventBus);
    }

    public static void generateEntityWorldSpawn()
    {
        registerEntityWorldSpawn(SAND_WYRM_ENTITY.get(), EntityClassification.CREATURE, 10, 2, 2, new Biome[]{Biomes.DESERT, Biomes.DESERT_HILLS, Biomes.DESERT_LAKES});
        registerEntityWorldSpawn(FAIRY_ENTITY.get(), EntityClassification.CREATURE, 13, 3, 3, new Biome[]{Biomes.FLOWER_FOREST, Biomes.SUNFLOWER_PLAINS});
        registerEntityWorldSpawn(FAIRY_ENTITY.get(), EntityClassification.CREATURE, 10, 3, 3, new Biome[]{Biomes.FOREST, Biomes.BIRCH_FOREST, Biomes.BIRCH_FOREST_HILLS, Biomes.TALL_BIRCH_FOREST});
        registerEntityWorldSpawn(FAIRY_ENTITY.get(), EntityClassification.CREATURE, 5, 3, 3, new Biome[]{Biomes.SWAMP});
        registerEntityWorldSpawn(WOLPERTINGER_ENTITY.get(), EntityClassification.CREATURE, 10, 1, 3, new Biome[]{Biomes.PLAINS});
        registerEntityWorldSpawn(WOLPERTINGER_ENTITY.get(), EntityClassification.CREATURE, 13, 1, 3, new Biome[]{Biomes.FLOWER_FOREST, Biomes.SUNFLOWER_PLAINS, Biomes.WOODED_MOUNTAINS, Biomes.GIANT_TREE_TAIGA});

        registerEntityWorldSpawn(DENDROID_ENTITY.get(), EntityClassification.MONSTER, 5, 3, 3, new Biome[]{Biomes.FOREST, Biomes.DARK_FOREST, Biomes.DARK_FOREST_HILLS});
    }

    public static void generateBlockWorldSpawn()
    {
        registerBlockWorldSpawn(GenerationStage.Decoration.VEGETAL_DECORATION,
                Feature.RANDOM_PATCH.withConfiguration(new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(FAIRY_MUSHROOM_BLOCK.get().getDefaultState()), new SimpleBlockPlacer()).tries(64).func_227317_b_().build()).withPlacement(Placement.CHANCE_HEIGHTMAP_DOUBLE.configure(new ChanceConfig(8))),
                new Biome[]{Biomes.FOREST, Biomes.BIRCH_FOREST, Biomes.BIRCH_FOREST_HILLS, Biomes.TALL_BIRCH_FOREST, Biomes.FLOWER_FOREST});
    }

    protected static void registerBlockWorldSpawn(GenerationStage.Decoration decoration, ConfiguredFeature<?, ?> featureIn, Biome[] biomes)
    {
        for (Biome biome : biomes)
        {
            biome.addFeature(decoration, featureIn);
        }
    }

    protected static void registerEntityWorldSpawn(EntityType<?> entity, EntityClassification classification, int weight, int minGroup, int maxGroup, Biome[] biomes)
    {
        for (Biome biome : biomes)
        {
            biome.getSpawns(classification).add(new Biome.SpawnListEntry(entity, weight, minGroup, maxGroup));
        }
    }

    //ITEMS
    public static final RegistryObject<Item> FIRE_SHARD = ITEMS.register("fireshard", () -> new ShardItem());
    public static final RegistryObject<Item> WATER_SHARD = ITEMS.register("watershard", () -> new ShardItem());
    public static final RegistryObject<Item> LIGHTNING_SHARD = ITEMS.register("lightningshard", () -> new ShardItem());
    public static final RegistryObject<Item> EARTH_SHARD = ITEMS.register("earthshard", () -> new ShardItem());
    public static final RegistryObject<Item> LIGHT_SHARD = ITEMS.register("lightshard", () -> new ShardItem());
    public static final RegistryObject<Item> DARK_SHARD = ITEMS.register("darkshard", () -> new ShardItem());
    public static final RegistryObject<Item> ICE_SHARD = ITEMS.register("iceshard", () -> new ShardItem());
    public static final RegistryObject<Item> NATURE_SHARD = ITEMS.register("natureshard", () -> new ShardItem());
    public static final RegistryObject<Item> AIR_SHARD = ITEMS.register("airshard", () -> new ShardItem());
    public static final RegistryObject<Item> SAND_SHARD = ITEMS.register("sandshard", () -> new ShardItem());

    public static final RegistryObject<Item> MAGICAL_SEED = ITEMS.register("magicalseed", () -> new ItemBase(new Item.Properties().maxStackSize(16)));
    public static final RegistryObject<Item> SAP = ITEMS.register("sap", () -> new ItemBase(new Item.Properties().maxStackSize(64)));
    public static final RegistryObject<Item> GOLDEN_ARROW = ITEMS.register("goldenarrow", () -> new GoldenArrowItem(new Item.Properties().maxStackSize(16).group(MWAWItemGroup.instance)));
    public static final RegistryObject<Item> HARDENED_LEATHER = ITEMS.register("hardenedleather", () -> new ItemBase(new Item.Properties()));
    public static final RegistryObject<Item> SAND_WYRM_TUSK = ITEMS.register("sandwyrmtusk", () -> new ItemBase(new Item.Properties()));
    public static final RegistryObject<Item> SAND_SCALE = ITEMS.register("sandscale", () -> new ItemBase(new Item.Properties()));
    public static final RegistryObject<Item> CELESTIAL_STAR_DUST = ITEMS.register("celestialstardust", () -> new ItemBase(new Item.Properties().maxStackSize(16)));
    public static final RegistryObject<Item> VOID_STAR_DUST = ITEMS.register("voidstardust", () -> new ItemBase(new Item.Properties().maxStackSize(16)));
    public static final RegistryObject<Item> DENDROID_ROOT = ITEMS.register("dendroidroot", () -> new ItemBase(new Item.Properties().maxStackSize(1)));
    public static final RegistryObject<Item> DENDROID_EYE = ITEMS.register("dendroideye", () -> new ItemBase(new Item.Properties().maxStackSize(1)));
    public static final RegistryObject<Item> ANTLER = ITEMS.register("antler", () -> new ItemBase(new Item.Properties()));
    public static final RegistryObject<Item> FAIRY_DUST = ITEMS.register("fairydust", () -> new ItemBase(new Item.Properties()));
    public static final RegistryObject<Item> BAD_APPLE = ITEMS.register("badapple", () -> new ItemBase(new Item.Properties()));


    public static final RegistryObject<Item> FIRE_EGG = ITEMS.register("fireegg", () -> new ItemBase(new Item.Properties().maxStackSize(1)));
   /* public static final RegistryObject<Item> WATER_EGG = ITEMS.register("wateregg", ItemBase::new);
    public static final RegistryObject<Item> LIGHTNING_EGG = ITEMS.register("lightningegg", ItemBase::new);
    public static final RegistryObject<Item> EARTH_EGG = ITEMS.register("earthegg", ItemBase::new);
    public static final RegistryObject<Item> LIGHT_EGG = ITEMS.register("lightegg", ItemBase::new);
    public static final RegistryObject<Item> DARK_EGG = ITEMS.register("darkegg", ItemBase::new);
    public static final RegistryObject<Item> NATURE_EGG = ITEMS.register("natureegg", ItemBase::new);*/


    public static final RegistryObject<Item> FIRE_STAFF = ITEMS.register("firestaff", () -> new ItemBase(new Item.Properties()));
    public static final RegistryObject<Item> DENDROID_SWORD = ITEMS.register("dendroidsword", () -> new DendroidSwordItem(ItemTier.DIAMOND, 3, -2.5f, new Item.Properties().maxDamage(359)));
    public static final RegistryObject<Item> MARROW_SWORD = ITEMS.register("marrowsword", () -> new MarrowSwordItem(ItemTier.DIAMOND, 4, -2.5f, new Item.Properties().maxDamage(1000)));
    public static final RegistryObject<Item> GOLDEN_BOW = ITEMS.register("goldenbow", () -> new GoldenBowItem(new Item.Properties().maxStackSize(1).group(MWAWItemGroup.instance)));
    /*public static final RegistryObject<Item> WATER_STAFF = ITEMS.register("waterstaff", ItemBase::new);
    public static final RegistryObject<Item> LIGHTNING_STAFF = ITEMS.register("lightningstaff", ItemBase::new);
    public static final RegistryObject<Item> EARTH_STAFF = ITEMS.register("earthstaff", ItemBase::new);
    public static final RegistryObject<Item> LIGHT_STAFF = ITEMS.register("lightstaff", ItemBase::new);
    public static final RegistryObject<Item> DARK_STAFF = ITEMS.register("darkstaff", ItemBase::new);
    public static final RegistryObject<Item> HALLOWEEN_STAFF = ITEMS.register("halloweenstaff", ItemBase::new);
    public static final RegistryObject<Item> CHRISTMAS_STAFF = ITEMS.register("christmasstaff", ItemBase::new);*/

    //BLOCKS
    public static final RegistryObject<Block> FAIRY_MUSHROOM_BLOCK = BLOCKS.register("fairymushroom", () -> new FairyMushroomBlock(Block.Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly().sound(SoundType.PLANT).lightValue(5)));
    public static final RegistryObject<Item> FAIRY_MUSHROOM_ITEM = ITEMS.register("fairymushroom", () -> new BlockItem(FAIRY_MUSHROOM_BLOCK.get(), new Item.Properties().group(MWAWItemGroup.instance)));

    //ENTITIES
    public static final RegistryObject<EntityType<FairyEntity>> FAIRY_ENTITY = ENTITIES.register("fairy", () -> EntityType.Builder.create(FairyEntity::new, EntityClassification.CREATURE)
            .size(0.4F, 0.8F)
            .setShouldReceiveVelocityUpdates(false)
            .build("fairy"));

    public static final RegistryObject<EntityType<SandWyrmEntity>> SAND_WYRM_ENTITY = ENTITIES.register("sandwyrm", () -> EntityType.Builder.create(SandWyrmEntity::new, EntityClassification.CREATURE)
            .size(2.0F, 1.0F)
            .setShouldReceiveVelocityUpdates(false)
            .build("sandwyrm"));

    public static final RegistryObject<EntityType<DendroidEntity>> DENDROID_ENTITY = ENTITIES.register("dendroid", () -> EntityType.Builder.create(DendroidEntity::new, EntityClassification.CREATURE)
            .size(1.0F, 1.5F)
            .setShouldReceiveVelocityUpdates(false)
            .build("dendroid"));

    public static final RegistryObject<EntityType<WolpertingerEntity>> WOLPERTINGER_ENTITY = ENTITIES.register("wolpertinger", () -> EntityType.Builder.create(WolpertingerEntity::new, EntityClassification.CREATURE)
            .size(1.0F, 0.9F)
            .setShouldReceiveVelocityUpdates(false)
            .build("wolpertinger"));

    public static final RegistryObject<EntityType<KitsuneEntity>> KITSUNE_ENTITY = ENTITIES.register("kitsune", () -> EntityType.Builder.create(KitsuneEntity::new, EntityClassification.CREATURE)
            .size(1.0F, 0.9F)
            .setShouldReceiveVelocityUpdates(false)
            .build("wolpertinger"));

    public static final RegistryObject<EntityType<GoldenArrowEntity>> GOLDEN_ARROW_ENTITY = ENTITIES.register("goldenarrow", () -> EntityType.Builder.<GoldenArrowEntity>create(GoldenArrowEntity::new, EntityClassification.MISC)
            .size(0.5F, 0.5F)
            .build("goldenarrow"));

    public static final RegistryObject<EntityType<SapEntity>> SAP_ENTITY = ENTITIES.register("sap", () -> EntityType.Builder.<SapEntity>create(SapEntity::new, EntityClassification.MISC)
            .size(0.25F, 0.25F)
            .build("sap"));

    //PARTICLES
    public static final RegistryObject<BasicParticleType> YELLOW_ORBITING_ENCHANTMENT_PARTICLE = PARTICLES.register("yelloworbitingenchantparticle", () -> new BasicParticleType(false));
    public static final RegistryObject<BasicParticleType> YELLOW_SUCKING_ENCHANTMENT_PARTICLE = PARTICLES.register("yellowsuckingenchantparticle", () -> new BasicParticleType(false));


    //SPAWN EGGS
    public static final RegistryObject<MWAWSpawnEggItem> FAIRY_SPAWN_EGG = ITEMS.register("fairyspawnegg", () -> new MWAWSpawnEggItem(FAIRY_ENTITY, 0x15153F, 0x153F3F, new Item.Properties().group(MWAWItemGroup.instance)));
    public static final RegistryObject<MWAWSpawnEggItem> SAND_WYRM_SPAWN_EGG = ITEMS.register("sandwyrmspawnegg", () -> new MWAWSpawnEggItem(SAND_WYRM_ENTITY, 0x2A2A00, 0x3F3F15, new Item.Properties().group(MWAWItemGroup.instance)));
    public static final RegistryObject<MWAWSpawnEggItem> DENDROID_SPAWN_EGG = ITEMS.register("dendroidspawnegg", () -> new MWAWSpawnEggItem(DENDROID_ENTITY, 0x2A2A00, 0x153F3F, new Item.Properties().group(MWAWItemGroup.instance)));
    public static final RegistryObject<MWAWSpawnEggItem> WOLPERTINGER_SPAWN_EGG = ITEMS.register("wolpertingerspawnegg", () -> new MWAWSpawnEggItem(WOLPERTINGER_ENTITY, 0x2A2A00, 0x153F3F, new Item.Properties().group(MWAWItemGroup.instance)));
    public static final RegistryObject<MWAWSpawnEggItem> KITSUNE_SPAWN_EGG = ITEMS.register("kitsunespawnegg", () -> new MWAWSpawnEggItem(KITSUNE_ENTITY, 0x153F3F, 0x15153F, new Item.Properties().group(MWAWItemGroup.instance)));

}