package com.thenimbleninja.mobhealthregen;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;
import java.util.List;

public class MobHealthEventHandler {

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntityLiving().world.isRemote || !(event.getEntityLiving() instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer dyingPlayer = (EntityPlayer) event.getEntityLiving();
        WorldServer world = (WorldServer) dyingPlayer.world;
        double r = ModConfig.scanRadius;
        AxisAlignedBB area = dyingPlayer.getEntityBoundingBox().grow(r, r, r);

        // Run 1 tick later to ensure death logic is processed
        FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
            List<EntityPlayer> playersInArea = world.getEntitiesWithinAABB(EntityPlayer.class, area);
            boolean isWipeout = true;

            for (EntityPlayer p : playersInArea) {
                if (p.isEntityAlive() && !p.getUniqueID().equals(dyingPlayer.getUniqueID())) {
                    isWipeout = false;
                    break;
                }
            }

            if (isWipeout) {
                processHealing(world, area);
            }
        });
    }

    private void processHealing(WorldServer world, AxisAlignedBB area) {
        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, area);

        for (EntityLivingBase entity : entities) {
            if (shouldHeal(entity)) {
                float max = entity.getMaxHealth();

                // Set health logic-side
                entity.setHealth(max);

                // Sync to Client using NBT (Safe Fix for private field error)
                NBTTagCompound nbt = new NBTTagCompound();
                entity.writeEntityToNBT(nbt);
                nbt.setFloat("Health", max);
                entity.readEntityFromNBT(nbt);

                // Green sparkle particles
                world.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY,
                        entity.posX, entity.posY + (entity.height / 2), entity.posZ,
                        15, 0.5, 0.5, 0.5, 0.05);
            }
        }
    }

    private boolean shouldHeal(EntityLivingBase entity) {
        if (entity instanceof EntityPlayer || entity instanceof net.minecraft.entity.item.EntityArmorStand) return false;

        ResourceLocation res = EntityList.getKey(entity);
        String name = (res != null) ? res.toString() : "";

        List<String> bl = Arrays.asList(ModConfig.blacklist);
        List<String> wl = Arrays.asList(ModConfig.whitelist);

        if (bl.contains(name)) return false;
        if (!wl.isEmpty()) return wl.contains(name);

        boolean isHostile = entity instanceof IMob || name.contains("iceandfire");
        boolean isPassive = entity instanceof net.minecraft.entity.passive.IAnimals;

        if (isHostile) return true;
        if (isPassive) return ModConfig.healPassives;

        return false;
    }
}