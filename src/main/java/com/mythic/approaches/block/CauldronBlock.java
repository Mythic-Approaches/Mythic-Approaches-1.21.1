package com.mythic.approaches.block;

import com.mojang.serialization.MapCodec;
import com.mythic.approaches.block.entity.CauldronBlockEntity;
import com.mythic.approaches.recipes.CauldronInput;
import com.mythic.approaches.recipes.CauldronRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CauldronBlock extends BaseEntityBlock {
    public static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 12, 14);
    public static final MapCodec<CauldronBlock> CODEC = simpleCodec(CauldronBlock::new);

    public CauldronBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new CauldronBlockEntity(pos, state);
    }

    @Override
    protected void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            if (level.getBlockEntity(pos) instanceof CauldronBlockEntity cauldron) {
                cauldron.drops();
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(
            @NotNull ItemStack stack, @NotNull BlockState state, Level level, @NotNull BlockPos pos,
            @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult
    ) {
        if (level.getBlockEntity(pos) instanceof CauldronBlockEntity cauldron) {
            //CROUCHING => LOOK FOR RECIPE
            if (player.isCrouching() && stack.isEmpty()) {
                if (!level.isClientSide) {
                    CauldronRecipe recipe = cauldron.getCurrentRecipe();

                    if (recipe != null) {
                        CauldronInput input = new CauldronInput(cauldron.getInventory());

                        ItemStack result = recipe.assemble(input, level.registryAccess());

                        cauldron.clearContents();
                        cauldron.checkForRecipe();

                        if (!player.getInventory().add(result)) {
                            player.drop(result, false);
                        }

                        return ItemInteractionResult.SUCCESS;
                    }
                }
            }
            // NOT CROUCHING => ADD / REMOVE ITEM
            else {
                // Check if empty-handed
                if (stack.isEmpty())
                    return retrieveLastItem(cauldron, level, player, pos);
                else
                    return addNewItem(cauldron, stack, level, player, pos);
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    /**
     * Retrieves the last item in the cauldron
     *
     * @return SUCCESS if the item stack was retrieved, FAIl otherwise
     */
    private ItemInteractionResult retrieveLastItem(CauldronBlockEntity cauldron, Level level, Player player, BlockPos pos) {
        int slot = getFirstFullSlot(cauldron);
        if (slot != -1) {
            ItemStack retrievedItem = cauldron.inventory.extractItem(slot, 1, false);
            player.addItem(retrievedItem);
            cauldron.inventory.setStackInSlot(slot, ItemStack.EMPTY);
            level.playSound(player, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1, 1);

            if (!level.isClientSide)
                cauldron.checkForRecipe();

            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.FAIL;
    }

    /**
     * Adds a new item to the cauldron
     *
     * @return SUCCESS if the item was added, FAIL otherwise
     */
    private ItemInteractionResult addNewItem(CauldronBlockEntity cauldron, ItemStack stack, Level level, Player player, BlockPos pos) {
        int slot = getFirstEmptySlot(cauldron);
        if (slot != -1 && !stack.isEmpty()) {
            cauldron.inventory.insertItem(slot, stack.copy(), false);
            stack.shrink(1);
            level.playSound(player, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1, 2);

            if (!level.isClientSide)
                cauldron.checkForRecipe();

            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.FAIL;
    }

    @Override
    public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        super.animateTick(state, level, pos, random);

        if (level.getBlockEntity(pos) instanceof CauldronBlockEntity cauldron && cauldron.hasValidRecipe()) {
            double centerX = pos.getX() + 0.5;
            double centerY = pos.getY() + 0.8;  // Matches ~1.25 in command
            double centerZ = pos.getZ() + 0.5;

            // Command uses 0.2 spread in X and Z
            double deltaX = 0.2;
            double deltaZ = 0.2;

            // Random position within the spread area
            double spawnX = centerX + (random.nextDouble() - 0.5) * 2 * deltaX;
            double spawnZ = centerZ + (random.nextDouble() - 0.5) * 2 * deltaZ;

            // Speed from command (0.01) with random direction
            double speed = 0.01;
            double motionX = (random.nextDouble() - 0.5) * 2 * speed;
            double motionY = (random.nextDouble() - 0.5) * 2 * speed;
            double motionZ = (random.nextDouble() - 0.5) * 2 * speed;

            // Spawn multiple particles like command's count=3
            for (int i = 0; i < 4; i++) {
                level.addParticle(ParticleTypes.END_ROD,
                        spawnX,
                        centerY,
                        spawnZ,
                        motionX, motionY, motionZ);
            }
        }
    }

    /**
     * Searches for an empty slot
     *
     * @param entity CaldronBlockEntity instance
     * @return The index of the first empty slot or -1 if there are no empty slots
     */
    private int getFirstEmptySlot(CauldronBlockEntity entity) {
        for (int i = 0; i < CauldronBlockEntity.SLOTS_AMOUNT; i++) {
            if (entity.inventory.getStackInSlot(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Searches for a full slot
     *
     * @param entity CauldronBlockEntity instance
     * @return The index of the first full slot or -1 if there are no full slots
     */
    private int getFirstFullSlot(CauldronBlockEntity entity) {
        for (int i = CauldronBlockEntity.SLOTS_AMOUNT - 1; i >= 0; i--) {
            if (!entity.inventory.getStackInSlot(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }
}
