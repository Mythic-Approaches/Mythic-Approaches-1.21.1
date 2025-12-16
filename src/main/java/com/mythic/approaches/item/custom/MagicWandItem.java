package com.mythic.approaches.item.custom;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class MagicWandItem extends Item {
    private final static Map<EntityType<?>, EntityType<? extends Mob>> CONVERTIBLE_ANIMALS = Map.ofEntries(
            Map.entry(EntityType.COW, EntityType.MOOSHROOM),
            Map.entry(EntityType.SHEEP, EntityType.GOAT),
            Map.entry(EntityType.CHICKEN, EntityType.PARROT)
    );

    public MagicWandItem(Properties properties) {
        super(properties);
    }

    private void spawnParticles(double x, double y, double z, Level level, ServerLevel serverLevel) {
        for (int i = 0; i < 30; i++) {
            double offsetX = (level.random.nextDouble() - 0.5) * 1.5;
            double offsetY = level.random.nextDouble() * 1.5;
            double offsetZ = (level.random.nextDouble() - 0.5) * 1.5;

            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                    x + offsetX,
                    y + offsetY,
                    z + offsetZ,
                    1, 0, 0, 0, 0);
        }

        for (int i = 0; i < 40; i++) {
            //double offsetX = (level.random.nextDouble() - 0.5) * 1.5;
            //double offsetY = level.random.nextDouble() * 1.5;
            //double offsetZ = (level.random.nextDouble() - 0.5) * 1.5;

            serverLevel.sendParticles(ParticleTypes.CRIMSON_SPORE, x, y, z, 1, 0, 0.1, 0, 0.05);
        }
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack, @NotNull Player player, @NotNull LivingEntity target, @NotNull InteractionHand hand) {
        if (!player.level().isClientSide) {
            EntityType<?> targetType = target.getType();
            EntityType<? extends Mob> convertToType = CONVERTIBLE_ANIMALS.get(targetType);

            if (convertToType != null && target instanceof Mob mob && mob.isAlive()){

                Mob converted = mob.convertTo(convertToType, true);

                if (converted != null) {
                    float randomYaw = player.level().random.nextFloat() * 360f;
                    float randomPitch = (player.level().random.nextFloat() - 0.5f) * 60f; // -30 to 30 degrees

                    converted.setYRot(randomYaw);
                    converted.setXRot(randomPitch);
                    converted.yBodyRot = randomYaw;
                    converted.yHeadRot = randomYaw;

                    ServerLevel serverLevel = (ServerLevel) player.level();
                    spawnParticles(target.getX(), target.getY(), target.getZ(), serverLevel, serverLevel);
                    player.level().playSound(null, converted.blockPosition(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.NEUTRAL, 1f, .75f);

                    return InteractionResult.SUCCESS;
                }
            }
        }

        return InteractionResult.FAIL;
    }
}
