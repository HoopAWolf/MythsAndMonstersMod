package com.hoopawolf.mwaw.util;

import com.hoopawolf.mwaw.entities.*;
import com.hoopawolf.mwaw.entities.projectiles.ClayEntity;
import com.hoopawolf.mwaw.entities.projectiles.FoxHeadEntity;
import com.hoopawolf.mwaw.entities.projectiles.GoldenArrowEntity;
import com.hoopawolf.mwaw.entities.projectiles.SapEntity;
import com.hoopawolf.mwaw.entities.renderer.*;
import com.hoopawolf.mwaw.entities.renderer.projectiles.FoxHeadRenderer;
import com.hoopawolf.mwaw.entities.renderer.projectiles.GoldenArrowRenderer;
import com.hoopawolf.mwaw.ref.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class EntityRegistryHandler
{
    public static final DeferredRegister<EntityType<?>> ENTITIES = new DeferredRegister<>(ForgeRegistries.ENTITIES, Reference.MOD_ID);

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
            .build("kitsune"));

    public static final RegistryObject<EntityType<HunterEntity>> HUNTER_ENTITY = ENTITIES.register("hunter", () -> EntityType.Builder.create(HunterEntity::new, EntityClassification.CREATURE)
            .size(0.7F, 1.85F)
            .setShouldReceiveVelocityUpdates(false)
            .build("hunter"));

    public static final RegistryObject<EntityType<ClayGolemEntity>> CLAY_GOLEM_ENTITY = ENTITIES.register("claygolem", () -> EntityType.Builder.create(ClayGolemEntity::new, EntityClassification.CREATURE)
            .size(1.4F, 2.4F)
            .setShouldReceiveVelocityUpdates(false)
            .build("claygolem"));

    public static final RegistryObject<EntityType<GoldenRamEntity>> GOLDEN_RAM_ENTITY = ENTITIES.register("goldenram", () -> EntityType.Builder.create(GoldenRamEntity::new, EntityClassification.CREATURE)
            .size(1.0F, 1.2F)
            .setShouldReceiveVelocityUpdates(false)
            .build("goldenram"));

    //PROJECTILE
    public static final RegistryObject<EntityType<GoldenArrowEntity>> GOLDEN_ARROW_ENTITY = ENTITIES.register("goldenarrow", () -> EntityType.Builder.<GoldenArrowEntity>create(GoldenArrowEntity::new, EntityClassification.MISC)
            .size(0.5F, 0.5F)
            .build("goldenarrow"));

    public static final RegistryObject<EntityType<SapEntity>> SAP_ENTITY = ENTITIES.register("sap", () -> EntityType.Builder.<SapEntity>create(SapEntity::new, EntityClassification.MISC)
            .size(0.25F, 0.25F)
            .build("sap"));

    public static final RegistryObject<EntityType<ClayEntity>> CLAY_ENTITY = ENTITIES.register("clay", () -> EntityType.Builder.<ClayEntity>create(ClayEntity::new, EntityClassification.MISC)
            .size(0.25F, 0.25F)
            .build("clay"));

    public static final RegistryObject<EntityType<FoxHeadEntity>> FOX_HEAD_ENTITY = ENTITIES.register("foxspirit", () -> EntityType.Builder.<FoxHeadEntity>create(FoxHeadEntity::new, EntityClassification.MISC)
            .size(0.5F, 0.5F)
            .build("foxspirit"));

    public static void generateEntityWorldSpawn()
    {
        registerEntityWorldSpawn(SAND_WYRM_ENTITY.get(), EntityClassification.CREATURE, 1, 0, 2, new Biome[]{Biomes.DESERT, Biomes.DESERT_HILLS, Biomes.DESERT_LAKES});
        registerEntityWorldSpawn(FAIRY_ENTITY.get(), EntityClassification.CREATURE, 10, 3, 3, new Biome[]{Biomes.FLOWER_FOREST, Biomes.SUNFLOWER_PLAINS});
        registerEntityWorldSpawn(FAIRY_ENTITY.get(), EntityClassification.CREATURE, 3, 0, 3, new Biome[]{Biomes.FOREST, Biomes.BIRCH_FOREST, Biomes.BIRCH_FOREST_HILLS, Biomes.TALL_BIRCH_FOREST});
        registerEntityWorldSpawn(FAIRY_ENTITY.get(), EntityClassification.CREATURE, 1, 0, 3, new Biome[]{Biomes.SWAMP});
        registerEntityWorldSpawn(WOLPERTINGER_ENTITY.get(), EntityClassification.CREATURE, 3, 3, 3, new Biome[]{Biomes.PLAINS});
        registerEntityWorldSpawn(WOLPERTINGER_ENTITY.get(), EntityClassification.CREATURE, 10, 3, 3, new Biome[]{Biomes.FLOWER_FOREST, Biomes.SUNFLOWER_PLAINS, Biomes.WOODED_MOUNTAINS, Biomes.GIANT_TREE_TAIGA});
        registerEntityWorldSpawn(KITSUNE_ENTITY.get(), EntityClassification.CREATURE, 1, 0, 1, new Biome[]{Biomes.TALL_BIRCH_FOREST, Biomes.DARK_FOREST});
        registerEntityWorldSpawn(HUNTER_ENTITY.get(), EntityClassification.CREATURE, 3, 0, 2, new Biome[]{Biomes.FOREST, Biomes.DARK_FOREST, Biomes.TALL_BIRCH_FOREST, Biomes.FLOWER_FOREST, Biomes.BIRCH_FOREST});

        registerEntityWorldSpawn(DENDROID_ENTITY.get(), EntityClassification.MONSTER, 1, 0, 3, new Biome[]{Biomes.FOREST, Biomes.DARK_FOREST, Biomes.DARK_FOREST_HILLS});
        registerEntityWorldSpawn(CLAY_GOLEM_ENTITY.get(), EntityClassification.MONSTER, 1, 0, 1, new Biome[]{Biomes.BADLANDS, Biomes.SWAMP});
    }

    protected static void registerEntityWorldSpawn(EntityType<?> entity, EntityClassification classification, int weight, int minGroup, int maxGroup, Biome[] biomes)
    {
        for (Biome biome : biomes)
        {
            biome.getSpawns(classification).add(new Biome.SpawnListEntry(entity, weight, minGroup, maxGroup));
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerEntityRenderer()
    {
        RenderingRegistry.registerEntityRenderingHandler(EntityRegistryHandler.FAIRY_ENTITY.get(), FairyRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityRegistryHandler.SAND_WYRM_ENTITY.get(), SandWyrmRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityRegistryHandler.DENDROID_ENTITY.get(), DendroidRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityRegistryHandler.WOLPERTINGER_ENTITY.get(), WolpertingerRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityRegistryHandler.KITSUNE_ENTITY.get(), KitsuneRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityRegistryHandler.HUNTER_ENTITY.get(), HunterRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityRegistryHandler.CLAY_GOLEM_ENTITY.get(), ClayGolemRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityRegistryHandler.GOLDEN_RAM_ENTITY.get(), GoldenRamRenderer::new);

        RenderingRegistry.registerEntityRenderingHandler(EntityRegistryHandler.GOLDEN_ARROW_ENTITY.get(), GoldenArrowRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityRegistryHandler.SAP_ENTITY.get(), m -> new SpriteRenderer<SapEntity>(m, Minecraft.getInstance().getItemRenderer()));
        RenderingRegistry.registerEntityRenderingHandler(EntityRegistryHandler.CLAY_ENTITY.get(), m -> new SpriteRenderer<ClayEntity>(m, Minecraft.getInstance().getItemRenderer()));
        RenderingRegistry.registerEntityRenderingHandler(EntityRegistryHandler.FOX_HEAD_ENTITY.get(), FoxHeadRenderer::new);

        RenderTypeLookup.setRenderLayer(ItemBlockRegistryHandler.FAIRY_MUSHROOM_BLOCK.get(), RenderType.getCutout());
    }//TODO ADD KITSUNE SPAWN IN VILLAGE, SPAWNING RATE NEED FIX
}
