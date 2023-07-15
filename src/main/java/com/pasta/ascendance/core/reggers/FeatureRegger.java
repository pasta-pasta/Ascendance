package com.pasta.ascendance.core.reggers;

import com.google.common.base.Suppliers;
import com.pasta.ascendance.Ascendance;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.function.Supplier;

import static com.pasta.ascendance.core.reggers.BlockRegger.NANOCOLONY;

public class FeatureRegger {
    public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES
            = DeferredRegister.create(Registry.CONFIGURED_FEATURE_REGISTRY, Ascendance.MOD_ID);

    public static final DeferredRegister<PlacedFeature> PLACED_FEATURES
            = DeferredRegister.create(Registry.PLACED_FEATURE_REGISTRY, Ascendance.MOD_ID);

    private static List<PlacementModifier> commonOrePlacement(int countPerChunk, PlacementModifier height) {
        return orePlacement(CountPlacement.of(countPerChunk), height);
    }

    private static List<PlacementModifier> orePlacement(PlacementModifier count, PlacementModifier height) {
        return List.of(count, InSquarePlacement.spread(), height, BiomeFilter.biome());
    }

    private static final Supplier<List<OreConfiguration.TargetBlockState>> NANOCOLONY_OVERWOLD_REPLACEMENT
            = Suppliers.memoize(() -> List.of(OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, NANOCOLONY.get().defaultBlockState()),
            OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, NANOCOLONY.get().defaultBlockState())
    ));

    public static final RegistryObject<ConfiguredFeature<?, ?>> NANOCOLONY_OVERWORLD
            = CONFIGURED_FEATURES.register("nanocolony_spawn",()
            -> new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(NANOCOLONY_OVERWOLD_REPLACEMENT.get(), 3)));

    public static final RegistryObject<PlacedFeature> OVERWORLD_NANOCOLONY
            = PLACED_FEATURES.register("nanocolony_overworld", ()
            -> new PlacedFeature(NANOCOLONY_OVERWORLD.getHolder().get(),
            commonOrePlacement(14, HeightRangePlacement.triangle(VerticalAnchor.absolute(-50),VerticalAnchor.absolute(130)))));

}
