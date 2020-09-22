package com.hoopawolf.mwaw.util;

import com.hoopawolf.mwaw.entities.*;
import com.hoopawolf.mwaw.entities.projectiles.*;
import com.hoopawolf.mwaw.entities.renderer.*;
import com.hoopawolf.mwaw.entities.renderer.projectiles.FoxHeadRenderer;
import com.hoopawolf.mwaw.entities.renderer.projectiles.GoldenArrowRenderer;
import com.hoopawolf.mwaw.entities.renderer.projectiles.SpiritBombRenderer;
import com.hoopawolf.mwaw.ref.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
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
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, Reference.MOD_ID);

    //ENTITIES
    public static final RegistryObject<EntityType<FairyEntity>> FAIRY_ENTITY = ENTITIES.register("fairy", () -> EntityType.Builder.create(FairyEntity::new, EntityClassification.CREATURE)
            .size(0.4F, 0.8F)
            .setShouldReceiveVelocityUpdates(false)
            .build("fairy"));

    public static final RegistryObject<EntityType<SandWyrmEntity>> SAND_WYRM_ENTITY = ENTITIES.register("sandwyrm", () -> EntityType.Builder.create(SandWyrmEntity::new, EntityClassification.CREATURE)
            .size(1.5F, 1.0F)
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

    public static final RegistryObject<EntityType<DendroidElderEntity>> DENDROID_ELDER_ENTITY = ENTITIES.register("dendroidelder", () -> EntityType.Builder.create(DendroidElderEntity::new, EntityClassification.CREATURE)
            .size(1.4F, 2.9F)
            .setShouldReceiveVelocityUpdates(false)
            .build("dendroidelder"));

    public static final RegistryObject<EntityType<PyromancerEntity>> PYRO_ENTITY = ENTITIES.register("pyro", () -> EntityType.Builder.create(PyromancerEntity::new, EntityClassification.CREATURE)
            .size(0.75F, 2.3F)
            .setShouldReceiveVelocityUpdates(false)
            .build("pyro"));

    public static final RegistryObject<EntityType<FireSpiritEntity>> FIRE_SPIRIT_ENTITY = ENTITIES.register("firespirit", () -> EntityType.Builder.create(FireSpiritEntity::new, EntityClassification.CREATURE)
            .size(0.5F, 0.5F)
            .setShouldReceiveVelocityUpdates(false)
            .build("firespirit"));

    public static final RegistryObject<EntityType<DropBearEntity>> DROP_BEAR_ENTITY = ENTITIES.register("dropbear", () -> EntityType.Builder.create(DropBearEntity::new, EntityClassification.CREATURE)
            .size(0.7F, 0.9F)
            .setShouldReceiveVelocityUpdates(false)
            .build("dropbear"));

    public static final RegistryObject<EntityType<JackalopeEntity>> JACKALOPE_ENTITY = ENTITIES.register("jackalope", () -> EntityType.Builder.create(JackalopeEntity::new, EntityClassification.CREATURE)
            .size(1.0F, 1.2F)
            .setShouldReceiveVelocityUpdates(false)
            .build("jackalope"));

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

    public static final RegistryObject<EntityType<SpiritBombEntity>> SPIRIT_BOMB_ENTITY = ENTITIES.register("spiritbomb", () -> EntityType.Builder.<SpiritBombEntity>create(SpiritBombEntity::new, EntityClassification.MISC)
            .size(3.0F, 3.0F)
            .build("spiritbomb"));

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
    }//TODO ADD KITSUNE SPAWN IN VILLAGE, SPAWNING RATE NEED FIX

    @OnlyIn(Dist.CLIENT)
    public static void registerEntityRenderer()
    {
        RenderingRegistry.registerEntityRenderingHandler(FAIRY_ENTITY.get(), FairyRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(SAND_WYRM_ENTITY.get(), SandWyrmRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(DENDROID_ENTITY.get(), DendroidRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(WOLPERTINGER_ENTITY.get(), WolpertingerRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(KITSUNE_ENTITY.get(), KitsuneRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(HUNTER_ENTITY.get(), HunterRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(CLAY_GOLEM_ENTITY.get(), ClayGolemRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GOLDEN_RAM_ENTITY.get(), GoldenRamRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(DENDROID_ELDER_ENTITY.get(), DendroidElderRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(PYRO_ENTITY.get(), PyromancerRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(FIRE_SPIRIT_ENTITY.get(), FireSpiritRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(DROP_BEAR_ENTITY.get(), DropBearRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(JACKALOPE_ENTITY.get(), JackalopeRenderer::new);

        RenderingRegistry.registerEntityRenderingHandler(GOLDEN_ARROW_ENTITY.get(), GoldenArrowRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(SAP_ENTITY.get(), m -> new SpriteRenderer<SapEntity>(m, Minecraft.getInstance().getItemRenderer()));
        RenderingRegistry.registerEntityRenderingHandler(CLAY_ENTITY.get(), m -> new SpriteRenderer<ClayEntity>(m, Minecraft.getInstance().getItemRenderer()));
        RenderingRegistry.registerEntityRenderingHandler(FOX_HEAD_ENTITY.get(), FoxHeadRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(SPIRIT_BOMB_ENTITY.get(), SpiritBombRenderer::new);

        RenderTypeLookup.setRenderLayer(ItemBlockRegistryHandler.FAIRY_MUSHROOM_BLOCK.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ItemBlockRegistryHandler.DENDROID_ROOTS_BLOCK.get(), RenderType.getCutout());
    }

    public static void registerEntityAttributes()
    {
        GlobalEntityTypeAttributes.put(FAIRY_ENTITY.get(), FairyEntity.func_234321_m_().create());
        GlobalEntityTypeAttributes.put(SAND_WYRM_ENTITY.get(), SandWyrmEntity.func_234321_m_().create());
        GlobalEntityTypeAttributes.put(DENDROID_ENTITY.get(), DendroidEntity.func_234321_m_().create());
        GlobalEntityTypeAttributes.put(WOLPERTINGER_ENTITY.get(), WolpertingerEntity.func_234321_m_().create());
        GlobalEntityTypeAttributes.put(KITSUNE_ENTITY.get(), KitsuneEntity.func_234321_m_().create());
        GlobalEntityTypeAttributes.put(HUNTER_ENTITY.get(), HunterEntity.func_234321_m_().create());
        GlobalEntityTypeAttributes.put(CLAY_GOLEM_ENTITY.get(), ClayGolemEntity.func_234321_m_().create());
        GlobalEntityTypeAttributes.put(GOLDEN_RAM_ENTITY.get(), GoldenRamEntity.func_234321_m_().create());
        GlobalEntityTypeAttributes.put(DENDROID_ELDER_ENTITY.get(), DendroidElderEntity.func_234321_m_().create());
        GlobalEntityTypeAttributes.put(PYRO_ENTITY.get(), PyromancerEntity.func_234321_m_().create());
        GlobalEntityTypeAttributes.put(FIRE_SPIRIT_ENTITY.get(), FireSpiritEntity.func_234321_m_().create());
        GlobalEntityTypeAttributes.put(DROP_BEAR_ENTITY.get(), DropBearEntity.func_234321_m_().create());
        GlobalEntityTypeAttributes.put(JACKALOPE_ENTITY.get(), JackalopeEntity.func_234321_m_().create());
    }
}
