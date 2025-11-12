package com.mythic.approaches.block;

import com.mythic.approaches.MythicApproachesMod;
import com.mythic.approaches.block.custom.ParticleFlowerBlock;
import com.mythic.approaches.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(MythicApproachesMod.MOD_ID);

    public static final DeferredBlock<Block> CAULDRON = registerBlock("cauldron", () -> new Block(BlockBehaviour.Properties.of()
            .sound(SoundType.NETHER_ORE)
            .strength(5f)
            .noOcclusion()
            .lightLevel((state) -> 5)));

    public static final DeferredBlock<FlowerBlock> BELLADONNA = registerBlock("belladonna",
            () -> new ParticleFlowerBlock(SuspiciousStewEffects.EMPTY,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.ALLIUM)
                            .noCollission()
                            .noOcclusion()
            )
    );

    public static final DeferredBlock<FlowerPotBlock> POTTED_BELLADONNA = registerBlock("potted_belladonna",
            () -> new FlowerPotBlock(() ->
                    (FlowerPotBlock) Blocks.FLOWER_POT,
                    ModBlocks.BELLADONNA,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_ALLIUM)
                            .noOcclusion()
            )
    );

    public static final DeferredBlock<FlowerBlock> MOLY = registerBlock("moly",
            () -> new ParticleFlowerBlock(SuspiciousStewEffects.EMPTY,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.ALLIUM)
                            .noCollission()
                            .noOcclusion()
            )
    );

    public static final DeferredBlock<FlowerPotBlock> POTTED_MOLY = registerBlock("potted_moly",
            () -> new FlowerPotBlock(() ->
                    (FlowerPotBlock) Blocks.FLOWER_POT,
                    ModBlocks.MOLY,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_ALLIUM)
                            .noOcclusion()
            )
    );

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> dBlock = BLOCKS.register(name, block);
        registerBlockItem(name, dBlock);
        return dBlock;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
