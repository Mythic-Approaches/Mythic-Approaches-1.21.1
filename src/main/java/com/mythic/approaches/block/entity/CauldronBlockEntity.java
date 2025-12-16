package com.mythic.approaches.block.entity;

import com.mythic.approaches.recipes.CauldronInput;
import com.mythic.approaches.recipes.CauldronRecipe;
import com.mythic.approaches.recipes.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class CauldronBlockEntity extends BlockEntity {
    public static final int SLOTS_AMOUNT = 5;
    public int usedSlots = 0;
    @Nullable
    private ResourceLocation currentRecipeId = null;
    @Nullable
    private CauldronRecipe cachedRecipe = null;


    public final ItemStackHandler inventory = new ItemStackHandler(SLOTS_AMOUNT) {
        @Override
        protected int getStackLimit(int slot, @NotNull ItemStack stack) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
                int count = 0;
                for (int i = 0; i < getSlots(); i++) {
                    if (!getStackInSlot(i).isEmpty()) {
                        count++;
                    }
                }
                usedSlots = count;
                setChanged();
            }
        }
    };

    public CauldronBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CAULDRON_ENTITY.get(), pos, state);
    }

    public void clearContents() {
        for (int i = 0; i < inventory.getSlots(); i++) {
            inventory.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(inventory.getSlots());
        for (int i = 0; i < inventory.getSlots(); i++) {
            inv.setItem(i, inventory.getStackInSlot(i));
        }

        assert this.level != null;
        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inventory.serializeNBT(registries));
        tag.putInt("usedSlots", usedSlots);

        if (currentRecipeId != null) {
            tag.putString("currentRecipe", currentRecipeId.toString());
        }
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        usedSlots = tag.getInt("usedSlots");

        if (tag.contains("currentRecipe", CompoundTag.TAG_STRING)) {
            currentRecipeId = ResourceLocation.tryParse(tag.getString("currentRecipe"));
        } else {
            currentRecipeId = null;
        }

        cachedRecipe = null;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    public void checkForRecipe() {
        if (level == null || level.isClientSide) return;

        Optional<RecipeHolder<CauldronRecipe>> recipeHolder = findMatchingRecipeHolder(this, level);
        ResourceLocation newRecipeId = recipeHolder.map(RecipeHolder::id).orElse(null);

        // Update if changed
        if (!Objects.equals(currentRecipeId, newRecipeId)) {
            currentRecipeId = newRecipeId;
            cachedRecipe = recipeHolder.map(RecipeHolder::value).orElse(null);

            setChanged();

            // Sync to client for particles
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
            }
        }
    }

    @Nullable
    public CauldronRecipe getCurrentRecipe() {
        if (cachedRecipe != null) {
            return cachedRecipe;
        }

        // Rebuild from ID
        if (currentRecipeId != null && level != null) {
            Optional<RecipeHolder<?>> holder = level.getRecipeManager().byKey(currentRecipeId);

            if (holder.isPresent() && holder.get().value() instanceof CauldronRecipe cauldronRecipe) {
                cachedRecipe = cauldronRecipe;
                return cachedRecipe;
            }
        }

        return null;
    }

    public boolean hasValidRecipe() {
        return currentRecipeId != null;
    }

    /**
     * Searches for a cauldron recipe
     *
     * @param cauldron CauldronBlockEntity instance
     * @param level    Level instance
     * @return first matching recipe containing the items in the cauldron's inventory
     */
    public Optional<RecipeHolder<CauldronRecipe>> findMatchingRecipeHolder(CauldronBlockEntity cauldron, Level level) {
        CauldronInput input = new CauldronInput(cauldron.getInventory());

        return level.getRecipeManager()
                .getAllRecipesFor(ModRecipes.CAULDRON_TYPE.get())
                .stream()
                .filter(holder -> holder.value().matches(input, level))
                .findFirst();
    }
}