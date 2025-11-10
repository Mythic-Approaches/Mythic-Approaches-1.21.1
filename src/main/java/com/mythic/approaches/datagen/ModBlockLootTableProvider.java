package com.mythic.approaches.datagen;

import com.mythic.approaches.block.ModBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {
    protected ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.BELLADONNA.get());
        add(ModBlocks.POTTED_BELLADONNA.get(), createPotFlowerItemTable(ModBlocks.BELLADONNA.get()));
        dropSelf(ModBlocks.MOLY.get());
        add(ModBlocks.POTTED_MOLY.get(), createPotFlowerItemTable(ModBlocks.MOLY.get()));
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
