package com.thenimbleninja.mobhealthregen;

import net.minecraftforge.common.config.Config;

@Config(modid = MobHealthRegen.MODID)
public class ModConfig {
    @Config.Name("Scan Radius")
    @Config.Comment("The block distance to check for other players and mobs when someone dies.")
    public static double scanRadius = 32.0;
}