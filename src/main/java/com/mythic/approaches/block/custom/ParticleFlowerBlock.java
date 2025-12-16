package com.mythic.approaches.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ParticleFlowerBlock extends FlowerBlock {
    public ParticleFlowerBlock(Holder<MobEffect> effect, float seconds, Properties properties) {
        super(effect, seconds, properties);
    }

    public ParticleFlowerBlock(SuspiciousStewEffects suspiciousStewEffects, Properties properties) {
        super(suspiciousStewEffects, properties);
    }

    @Override
    public void animateTick(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        // Create 3 particles
        if (level.isClientSide() && random.nextFloat() < .3f) {
            // Increased spread from 0.5 to 1.2 for more space between particles
            double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 1.2;
            double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 1.2;
            // Y position already uses pos.getY() which is the plant's position
            level.addParticle(ParticleTypes.PORTAL, x, pos.getY() + 0.2, z, 0.0, 0.04, 0.0);
        }
    }
}
