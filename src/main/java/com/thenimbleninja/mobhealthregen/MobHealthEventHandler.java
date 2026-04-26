package com.thenimbleninja.mobhealthregen;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import java.util.List;

public class MobHealthEventHandler {

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        // 1. Guard against null entities (common at 100% load)
        if (event == null || event.getEntity() == null) return;

        // 2. Only run for Players on the Server
        if (!(event.getEntity() instanceof ServerPlayer dyingPlayer)) return;
        if (dyingPlayer.level().isClientSide) return;

        ServerLevel level = dyingPlayer.serverLevel();

        // 3. Hardcoded radius for testing (removes Config dependency)
        double radius = 32.0;

        // 4. Try to get radius from config, if it fails, we keep 32.0
        try {
            if (ModConfig.SPEC.isLoaded()) {
                radius = ModConfig.SCAN_RADIUS.get();
            }
        } catch (Exception ignored) {}

        AABB area = dyingPlayer.getBoundingBox().inflate(radius);

        // 5. Check for "Wipeout"
        try {
            List<ServerPlayer> otherPlayers = level.getEntitiesOfClass(ServerPlayer.class, area,
                    p -> p != null && p.isAlive() && !p.getUUID().equals(dyingPlayer.getUUID()));

            if (otherPlayers.isEmpty()) {
                processHealing(level, area);
            }
        } catch (Exception e) {
            System.err.println("MobHealthRegen: Error during player death check!");
        }
    }

    private void processHealing(ServerLevel level, AABB area) {
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area);

        for (LivingEntity entity : entities) {
            if (entity == null || !entity.isAlive()) continue;

            if (shouldHeal(entity)) {
                entity.setHealth(entity.getMaxHealth());

                level.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                        entity.getX(), entity.getY() + (entity.getBbHeight() / 2.0), entity.getZ(),
                        15, 0.5, 0.5, 0.5, 0.05);
            }
        }
    }

    private boolean shouldHeal(LivingEntity entity) {
        try {
            if (entity instanceof ServerPlayer) return false;

            // Safely check the Registry Name
            var key = EntityType.getKey(entity.getType());
            if (key == null) return false;
            String registryName = key.toString();

            // Category logic
            boolean isHostile = entity instanceof Enemy ||
                    registryName.contains("iceandfire") ||
                    registryName.contains("bloodandmadness");

            if (isHostile) return true;

            // Check config for passives if loaded
            if (entity instanceof Animal && ModConfig.SPEC.isLoaded()) {
                return ModConfig.HEAL_PASSIVES.get();
            }
        } catch (Exception ignored) {}

        return false;
    }
}