package com.hoopawolf.mwaw.util;

import com.hoopawolf.mwaw.blocks.FairyMushroomBlock;
import com.hoopawolf.mwaw.items.BadAppleItem;
import com.hoopawolf.mwaw.items.MWAWSpawnEggItem;
import com.hoopawolf.mwaw.items.ShardItem;
import com.hoopawolf.mwaw.items.weapons.*;
import com.hoopawolf.mwaw.ref.Reference;
import com.hoopawolf.mwaw.tab.MWAWItemGroup;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.*;
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


public class ItemBlockRegistryHandler
{
    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, Reference.MOD_ID);
    public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, Reference.MOD_ID);

    public static final Food BAD_APPLE_STAT = (new Food.Builder()).hunger(4).saturation(1.2F).setAlwaysEdible().build();

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
    public static final RegistryObject<Item> MAGICAL_SEED = ITEMS.register("magicalseed", () -> new Item(new Item.Properties().maxStackSize(16).group(MWAWItemGroup.instance)));
    public static final RegistryObject<Item> SAP = ITEMS.register("sap", () -> new Item(new Item.Properties().maxStackSize(64)));
    public static final RegistryObject<Item> GOLDEN_ARROW = ITEMS.register("goldenarrow", () -> new GoldenArrowItem(new Item.Properties().maxStackSize(16).group(MWAWItemGroup.instance)));
    public static final RegistryObject<Item> HARDENED_LEATHER = ITEMS.register("hardenedleather", () -> new Item(new Item.Properties().group(MWAWItemGroup.instance)));
    public static final RegistryObject<Item> SAND_WYRM_TUSK = ITEMS.register("sandwyrmtusk", () -> new Item(new Item.Properties().group(MWAWItemGroup.instance)));
    public static final RegistryObject<Item> SAND_SCALE = ITEMS.register("sandscale", () -> new Item(new Item.Properties().group(MWAWItemGroup.instance)));
    public static final RegistryObject<Item> CELESTIAL_STAR_DUST = ITEMS.register("celestialstardust", () -> new Item(new Item.Properties().maxStackSize(16).group(MWAWItemGroup.instance)));
    public static final RegistryObject<Item> VOID_STAR_DUST = ITEMS.register("voidstardust", () -> new Item(new Item.Properties().maxStackSize(16).group(MWAWItemGroup.instance)));
    public static final RegistryObject<Item> DENDROID_ROOT = ITEMS.register("dendroidroot", () -> new Item(new Item.Properties().maxStackSize(1).group(MWAWItemGroup.instance)));
    public static final RegistryObject<Item> DENDROID_EYE = ITEMS.register("dendroideye", () -> new Item(new Item.Properties().maxStackSize(1).group(MWAWItemGroup.instance)));
    public static final RegistryObject<Item> ANTLER = ITEMS.register("antler", () -> new Item(new Item.Properties().group(MWAWItemGroup.instance)));
    public static final RegistryObject<Item> FAIRY_DUST = ITEMS.register("fairydust", () -> new Item(new Item.Properties().group(MWAWItemGroup.instance)));
    public static final RegistryObject<Item> SCALE_MAIL = ITEMS.register("scalemail", () -> new Item(new Item.Properties().group(MWAWItemGroup.instance)));
    public static final RegistryObject<Item> TAINTED_SEED = ITEMS.register("taintedseed", () -> new Item(new Item.Properties().maxStackSize(16).group(MWAWItemGroup.instance)));
    public static final RegistryObject<Item> BAD_APPLE = ITEMS.register("badapple", () -> new BadAppleItem(new Item.Properties().group(MWAWItemGroup.instance).food(BAD_APPLE_STAT)));
    public static final RegistryObject<Item> FIRE_EGG = ITEMS.register("fireegg", () -> new Item(new Item.Properties().maxStackSize(1).group(MWAWItemGroup.instance)));
    public static final RegistryObject<Item> FIRE_STAFF = ITEMS.register("firestaff", () -> new Item(new Item.Properties().group(MWAWItemGroup.instance)));
    public static final RegistryObject<Item> DENDROID_SWORD = ITEMS.register("dendroidsword", () -> new DendroidSwordItem(ItemTier.DIAMOND, 3, -2.5f, new Item.Properties().maxDamage(359)));
    public static final RegistryObject<Item> MARROW_SWORD = ITEMS.register("marrowsword", () -> new MarrowSwordItem(ItemTier.DIAMOND, 4, -2.5f, new Item.Properties().maxDamage(1000)));
    /* public static final RegistryObject<Item> WATER_EGG = ITEMS.register("wateregg", ItemBase::new);
     public static final RegistryObject<Item> LIGHTNING_EGG = ITEMS.register("lightningegg", ItemBase::new);
     public static final RegistryObject<Item> EARTH_EGG = ITEMS.register("earthegg", ItemBase::new);
     public static final RegistryObject<Item> LIGHT_EGG = ITEMS.register("lightegg", ItemBase::new);
     public static final RegistryObject<Item> DARK_EGG = ITEMS.register("darkegg", ItemBase::new);
     public static final RegistryObject<Item> NATURE_EGG = ITEMS.register("natureegg", ItemBase::new);*/
    public static final RegistryObject<Item> BONE_DAGGER = ITEMS.register("bonedagger", () -> new BoneDaggerItem(ItemTier.DIAMOND, 4, -2.5f, new Item.Properties().maxDamage(1000)));
    public static final RegistryObject<Item> GOLDEN_BOW = ITEMS.register("goldenbow", () -> new GoldenBowItem(new Item.Properties().maxStackSize(1).group(MWAWItemGroup.instance).rarity(Rarity.UNCOMMON)));
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
    //SPAWN EGGS
    public static final RegistryObject<MWAWSpawnEggItem> FAIRY_SPAWN_EGG = ITEMS.register("fairyspawnegg", () -> new MWAWSpawnEggItem(EntityRegistryHandler.FAIRY_ENTITY, 0x15153F, 0x153F3F, new Item.Properties().group(MWAWItemGroup.instance)));
    public static final RegistryObject<MWAWSpawnEggItem> SAND_WYRM_SPAWN_EGG = ITEMS.register("sandwyrmspawnegg", () -> new MWAWSpawnEggItem(EntityRegistryHandler.SAND_WYRM_ENTITY, 0x2A2A00, 0x3F3F15, new Item.Properties().group(MWAWItemGroup.instance)));
    public static final RegistryObject<MWAWSpawnEggItem> DENDROID_SPAWN_EGG = ITEMS.register("dendroidspawnegg", () -> new MWAWSpawnEggItem(EntityRegistryHandler.DENDROID_ENTITY, 0x2A2A00, 0x153F3F, new Item.Properties().group(MWAWItemGroup.instance)));
    public static final RegistryObject<MWAWSpawnEggItem> WOLPERTINGER_SPAWN_EGG = ITEMS.register("wolpertingerspawnegg", () -> new MWAWSpawnEggItem(EntityRegistryHandler.WOLPERTINGER_ENTITY, 0x2A2A00, 0x153F3F, new Item.Properties().group(MWAWItemGroup.instance)));
    public static final RegistryObject<MWAWSpawnEggItem> KITSUNE_SPAWN_EGG = ITEMS.register("kitsunespawnegg", () -> new MWAWSpawnEggItem(EntityRegistryHandler.KITSUNE_ENTITY, 0x153F3F, 0x15153F, new Item.Properties().group(MWAWItemGroup.instance)));
    public static final RegistryObject<MWAWSpawnEggItem> HUNTER_SPAWN_EGG = ITEMS.register("hunterspawnegg", () -> new MWAWSpawnEggItem(EntityRegistryHandler.HUNTER_ENTITY, 0x153F3F, 0x15153F, new Item.Properties().group(MWAWItemGroup.instance)));
    public static final RegistryObject<MWAWSpawnEggItem> CLAY_GOLEM_SPAWN_EGG = ITEMS.register("claygolemspawnegg", () -> new MWAWSpawnEggItem(EntityRegistryHandler.CLAY_GOLEM_ENTITY, 0x153F3F, 0x15153F, new Item.Properties().group(MWAWItemGroup.instance)));
    public static final RegistryObject<MWAWSpawnEggItem> GOLDEN_RAM_SPAWN_EGG = ITEMS.register("goldenramspawnegg", () -> new MWAWSpawnEggItem(EntityRegistryHandler.GOLDEN_RAM_ENTITY, 0x15235F, 0x16234F, new Item.Properties().group(MWAWItemGroup.instance)));

    public static void init(IEventBus _iEventBus)
    {
        ITEMS.register(_iEventBus);
        BLOCKS.register(_iEventBus);
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

}