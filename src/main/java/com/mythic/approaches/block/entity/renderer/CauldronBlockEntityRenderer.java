package com.mythic.approaches.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mythic.approaches.block.entity.CauldronBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CauldronBlockEntityRenderer implements BlockEntityRenderer<CauldronBlockEntity> {

    public CauldronBlockEntityRenderer(BlockEntityRendererProvider.Context renderContext) {
    }

    @Override
    public void render(CauldronBlockEntity cauldron, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        final int slots = cauldron.usedSlots;
        if (slots == 0) return;

        final Minecraft mc = Minecraft.getInstance();
        assert mc.level != null;

        final double radius = 0.25f;
        final double height = 0.7f;
        // global orbit timer
        final float orbitTime = (mc.level.getGameTime() + partialTick) * 0.8f;

        for (int i = 0; i < slots; i++) {
            final ItemStack stack = cauldron.getInventory().getStackInSlot(i);
            if (stack.isEmpty()) continue;

            poseStack.pushPose();

            // Get item angle and offset
            double offset = (i / (double) slots) * Math.PI * 2; // 0 ... 2pi
            double angle = orbitTime * 0.025 + offset;          // Rotate clockwise
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;

            double bob = Math.sin(orbitTime * 0.3f + i*2) * .04f;
            poseStack.translate(0.5 + x, height + bob, 0.5 + z);

            poseStack.scale(0.37f, 0.37f, 0.37f);

            // Change rotation so the item faces the center
            poseStack.mulPose(Axis.YP.rotationDegrees((float) Math.toDegrees(Math.atan2(-x, -z))));

            mc.getItemRenderer().renderStatic(stack,
                    ItemDisplayContext.GROUND,
                    packedLight,
                    packedOverlay,
                    poseStack,
                    bufferSource,
                    cauldron.getLevel(),
                    0);

            poseStack.popPose();
        }
    }
}
