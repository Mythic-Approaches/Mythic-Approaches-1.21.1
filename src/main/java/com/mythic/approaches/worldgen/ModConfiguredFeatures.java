package com.mythic.approaches.worldgen;

import com.mythic.approaches.MythicApproachesMod;
import com.mythic.approaches.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import java.util.List;

public class ModConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> BELLADONNA_KEY = registerKey("belladonna");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MOLY_KEY = registerKey("moly");

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        register(context,
                MOLY_KEY,
                Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(
                        16, // tries: The number of attempts to place a patch in a chunk.
                        5,  // xzSpread: The horizontal spread of the patch.
                        3,  // ySpread: The vertical spread of the patch.
                        FeatureUtils.simplePatchConfiguration(
                                Feature.SIMPLE_BLOCK,
                                new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.MOLY.get().defaultBlockState())), List.of(Blocks.GRASS_BLOCK)
                        ).feature()
                )
        );

        register(context,
                BELLADONNA_KEY,
                Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(
                        32, // tries: The number of attempts to place a patch in a chunk.
                        5,  // xzSpread: The horizontal spread of the patch.
                        5,  // ySpread: The vertical spread of the patch.
                        FeatureUtils.simplePatchConfiguration(
                                Feature.SIMPLE_BLOCK,
                                new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.BELLADONNA.get().defaultBlockState())), List.of(Blocks.GRASS_BLOCK)
                        ).feature()
                )
        );
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(MythicApproachesMod.MOD_ID, name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(
            BootstrapContext<ConfiguredFeature<?, ?>> context,
            ResourceKey<ConfiguredFeature<?, ?>> key,
            F feature,
            FC configuration
    ) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}
