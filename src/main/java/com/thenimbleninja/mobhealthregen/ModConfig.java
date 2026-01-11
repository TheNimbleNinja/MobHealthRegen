package com.thenimbleninja.mobhealthregen;

import net.minecraftforge.common.config.Config;

@Config(modid = MobHealthRegen.MODID)
public class ModConfig {

    @Config.Name("Scan Radius")
    @Config.Comment("Distance to check for players and mobs when someone dies.")
    public static double scanRadius = 32.0;

    @Config.Name("Heal Passive Mobs")
    @Config.Comment("If true, animals (cows, sheep) will also heal. If false, only monsters heal.")
    public static boolean healPassives = false;

    @Config.Name("Mob Blacklist")
    @Config.Comment("Registry names of mobs that should NEVER heal (e.g. minecraft:creeper)")
    public static String[] blacklist = new String[]{"minecraft:armor_stand"};

    @Config.Name("Mob Whitelist")
    @Config.Comment("If not empty, ONLY these mobs will heal.")
    public static String[] whitelist = new String[]{};
}