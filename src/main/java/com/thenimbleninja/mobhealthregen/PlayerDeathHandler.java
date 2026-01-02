package com.thenimbleninja.mobhealthregen;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.util.List;

public class PlayerDeathHandler {

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        // Only run on the server and only if the victim is a player
        if (event.getEntityLiving().world.isRemote || !(event.getEntityLiving() instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer dyingPlayer = (EntityPlayer) event.getEntityLiving();
        World world = dyingPlayer.world;
        double r = ModConfig.scanRadius;

        // Define the area around the dead player
        AxisAlignedBB area = dyingPlayer.getEntityBoundingBox().grow(r, r, r);

        // Check for survivors
        List<EntityPlayer> playersInArea = world.getEntitiesWithinAABB(EntityPlayer.class, area);
        boolean isWipeout = true;

        for (EntityPlayer p : playersInArea) {
            // If anyone is still alive (and isn't the one who just died), it's not a wipeout
            if (p.isEntityAlive() && !p.getUniqueID().equals(dyingPlayer.getUniqueID())) {
                isWipeout = false;
                break;
            }
        }

        if (isWipeout) {
            healAllMobs(world, area);
        }
    }

    private void healAllMobs(World world, AxisAlignedBB area) {
        // Find all living things in the area
        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, area);

        for (EntityLivingBase entity : entities) {
            // Heal if it's a monster (IMob) or a hostile-type modded entity
            if (entity instanceof IMob && !(entity instanceof EntityPlayer)) {
                float maxHealth = entity.getMaxHealth();

                // Set health to full
                entity.setHealth(maxHealth);

                // Sync to Client via NBT (Universal fix for health bars)
                net.minecraft.nbt.NBTTagCompound nbt = new net.minecraft.nbt.NBTTagCompound();
                entity.writeEntityToNBT(nbt);
                nbt.setFloat("Health", maxHealth);
                entity.readEntityFromNBT(nbt);

                // Sparkle particles to show it worked
                if (world instanceof WorldServer) {
                    ((WorldServer) world).spawnParticle(EnumParticleTypes.VILLAGER_HAPPY,
                            entity.posX, entity.posY + (entity.height / 2), entity.posZ,
                            10, 0.5, 0.5, 0.5, 0.02);
                }
            }
        }
    }
}