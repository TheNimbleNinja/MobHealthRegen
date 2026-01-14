package com.thenimbleninja.mobhealthregen;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod(MobHealthRegen.MOD_ID)
public class MobHealthRegen {
    public static final String MOD_ID = "mobhealthregen";

    public MobHealthRegen() {
        // Register the event listener to the Forge Event Bus
        MinecraftForge.EVENT_BUS.register(new MobHealthEventHandler());
    }
}