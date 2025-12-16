package com.mythic.approaches.datagen;

import com.mythic.approaches.MythicApproachesMod;
import com.mythic.approaches.block.ModBlocks;
import com.mythic.approaches.item.ModItems;
import net.minecraft.data.PackOutput;
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
        itemFromCustomBlock(ModBlocks.CAULDRON);
        basicItem(ModItems.MAGIC_WAND.get());
    }

    private void flowerBlockItem(DeferredBlock<?> block) {
        withExistingParent(block.getId().getPath(), "item/generated").texture("layer0",
                modLoc("block/" + block.getId().getPath()));
    }

    private void itemFromCustomBlock(DeferredBlock<?> block) {
        withExistingParent(block.getId().getPath(), modLoc("block/" + block.getId().getPath()));
    }
}
