package com.mythic.approaches.datagen;

import com.mythic.approaches.MythicApproachesMod;
import com.mythic.approaches.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModItemModelProvider extends ItemModelProvider {

    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MythicApproachesMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        flowerBlockItem(ModBlocks.BELLADONNA);
        flowerBlockItem(ModBlocks.MOLY);
    }

    private void flowerBlockItem(DeferredBlock<?> block) {
        withExistingParent(block.getId().getPath(), "item/generated").texture("layer0",
                modLoc("block/" + block.getId().getPath()));
    }
}
