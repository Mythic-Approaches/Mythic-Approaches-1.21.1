package com.mythic.approaches.item;

import com.mythic.approaches.MythicApproachesMod;
import com.mythic.approaches.item.custom.MagicWandItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MythicApproachesMod.MOD_ID);

    public static final DeferredItem<Item> MAGIC_WAND = ITEMS.register("magic_wand",
            () -> new MagicWandItem(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
