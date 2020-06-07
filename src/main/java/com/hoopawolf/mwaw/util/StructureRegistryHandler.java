package com.hoopawolf.mwaw.util;

import com.hoopawolf.mwaw.structure.CampStructure;
import com.hoopawolf.mwaw.structure.piece.CampPiece;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Locale;

//Credits to TelepathicGrunt for helping me out
public class StructureRegistryHandler
{
    public static final Structure<NoFeatureConfig> HUNTER_CAMP = new CampStructure(NoFeatureConfig::deserialize);
    public static IStructurePieceType HUNTER_CAMP_FEATURE = CampPiece.Piece::new;

    public static void generateStructureWorldSpawn()
    {
        registerStructureWorldSpawn(HUNTER_CAMP.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG),
                new Biome[]{Biomes.FOREST, Biomes.TALL_BIRCH_FOREST});

        registerFeatureWorldSpawn(HUNTER_CAMP.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.NOPE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG)));
    }

    public static void registerFeature(IForgeRegistry<Feature<?>> registry)
    {
        registry.register(StructureRegistryHandler.HUNTER_CAMP.setRegistryName("huntercamp"));
        register(HUNTER_CAMP_FEATURE, "HCF");
    }

    protected static void registerStructureWorldSpawn(ConfiguredFeature structureIn, Biome[] biomes)
    {
        for (Biome biome : biomes)
        {
            biome.addStructure(structureIn);
        }
    }

    protected static void registerFeatureWorldSpawn(ConfiguredFeature<?, ?> featureIn)
    {
        for (Biome biome : ForgeRegistries.BIOMES)
        {
            biome.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, featureIn);
        }
    }


    static IStructurePieceType register(IStructurePieceType structurePiece, String key)
    {
        return Registry.register(Registry.STRUCTURE_PIECE, key.toLowerCase(Locale.ROOT), structurePiece);
    }
}
