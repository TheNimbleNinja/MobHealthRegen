package com.thenimbleninja.mobhealthregen;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class MobHealthEventHandler {

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        // Check if the entity dying is a player and on the server
        if (!(event.getEntity() instanceof ServerPlayer dyingPlayer)) return;

        ServerLevel level = dyingPlayer.serverLevel();
        double radius = 32.0; // You can move this to a config later
        AABB area = dyingPlayer.getBoundingBox().inflate(radius);

        // Check for "Wipeout": Are any OTHER players still alive in the area?
        List<ServerPlayer> otherPlayers = level.getEntitiesOfClass(ServerPlayer.class, area,
                p -> p.isAlive() && !p.getUUID().equals(dyingPlayer.getUUID()));

        if (otherPlayers.isEmpty()) {
            processHealing(level, area);
        }
    }

    private void processHealing(ServerLevel level, AABB area) {
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area);

        for (LivingEntity entity : entities) {
            if (shouldHeal(entity)) {
                // In 1.20.1, setHealth handles the network syncing automatically!
                entity.setHealth(entity.getMaxHealth());

                // Spawn "Happy Villager" green sparkles
                level.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                        entity.getX(), entity.getY() + (entity.getBbHeight() / 2), entity.getZ(),
                        15, 0.5, 0.5, 0.5, 0.05);
            }
        }
    }

    private boolean shouldHeal(LivingEntity entity) {
        if (entity instanceof net.minecraft.world.entity.player.Player) return false;

        // Get the name, e.g., "minecraft:zombie"
        String registryName = net.minecraft.world.entity.EntityType.getKey(entity.getType()).toString();

        List<? extends String> blacklist = ModConfig.BLACKLIST.get();
        List<? extends String> whitelist = ModConfig.WHITELIST.get();

        // 1. Check Blacklist first
        if (blacklist.contains(registryName)) return false;

        // 2. Check Whitelist (if it's not empty, it overrides everything else)
        if (!whitelist.isEmpty()) {
            return whitelist.contains(registryName);
        }

        // 3. Fallback to Category Logic
        boolean isHostile = entity instanceof net.minecraft.world.entity.monster.Enemy || registryName.contains("iceandfire") || registryName.contains("bloodandmadness");
        boolean isPassive = entity instanceof net.minecraft.world.entity.animal.Animal;

        if (isHostile) return true;
        if (isPassive) return ModConfig.HEAL_PASSIVES.get();

        return false;
    }
}