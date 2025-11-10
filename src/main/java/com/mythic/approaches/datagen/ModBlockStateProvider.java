package com.mythic.approaches.datagen;

import com.mythic.approaches.MythicApproachesMod;
import com.mythic.approaches.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, MythicApproachesMod.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        flowerBlockWithItem(ModBlocks.BELLADONNA);
        pottedFlowerBlock(ModBlocks.POTTED_BELLADONNA, ModBlocks.BELLADONNA);
        flowerBlockWithItem(ModBlocks.MOLY);
        pottedFlowerBlock(ModBlocks.POTTED_MOLY, ModBlocks.MOLY);
    }

    private void pottedFlowerBlock(DeferredBlock<?> deferredBlock, DeferredBlock<?> block_with_texture) {
        simpleBlockWithItem(deferredBlock.get(), models().singleTexture(blockTexture(deferredBlock.get()).getPath(),
                ResourceLocation.withDefaultNamespace("flower_pot_cross"), "plant", blockTexture(block_with_texture.get())).renderType("cutout"));
    }

    private void blockWithItem(DeferredBlock<?> deferredBlock) {
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }

    private void flowerBlockWithItem(DeferredBlock<?> deferredBlock) {
        simpleBlock(deferredBlock.get(), models().cross(blockTexture(deferredBlock.get()).getPath(), blockTexture(deferredBlock.get())).renderType("cutout"));
    }
}