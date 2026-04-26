package com.thenimbleninja.mobhealthregen;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;

@Mod(MobHealthRegen.MOD_ID)
public class MobHealthRegen {
    public static final String MOD_ID = "mobhealthregen";

    public MobHealthRegen(ModContainer container, IEventBus modEventBus) {
        // Register the config FIRST
        container.registerConfig(ModConfig.Type.COMMON, com.thenimbleninja.mobhealthregen.ModConfig.SPEC);

        // Register the handler to the NeoForge event bus
        NeoForge.EVENT_BUS.register(new MobHealthEventHandler());
    }
}