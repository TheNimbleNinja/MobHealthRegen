package com.thenimbleninja.mobhealthregen;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = MobHealthRegen.MODID, name = MobHealthRegen.NAME, version = MobHealthRegen.VERSION)
public class MobHealthRegen {
    public static final String MODID = "mobhealthregen";
    public static final String NAME = "Mob Health Regen";
    public static final String VERSION = "1.0";

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        // Register our event handler so Forge knows to listen to it
        MinecraftForge.EVENT_BUS.register(new PlayerDeathHandler());
    }
}