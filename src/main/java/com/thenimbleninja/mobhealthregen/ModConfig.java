package com.thenimbleninja.mobhealthregen;

import net.minecraftforge.common.ForgeConfigSpec;
import java.util.Arrays;
import java.util.List;

public class ModConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.DoubleValue SCAN_RADIUS;
    public static final ForgeConfigSpec.BooleanValue HEAL_PASSIVES;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLACKLIST;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> WHITELIST;

    static {
        BUILDER.push("General Settings");

        SCAN_RADIUS = BUILDER
                .comment("How far to look for mobs around the dying player.")
                .defineInRange("scanRadius", 32.0, 1.0, 128.0);

        HEAL_PASSIVES = BUILDER
                .comment("If true, animals (cows, etc.) will also heal. If false, only monsters heal.")
                .define("healPassives", false);

        BLACKLIST = BUILDER
                .comment("Registry names of mobs that should NEVER heal (e.g. ['minecraft:creeper', 'minecraft:tnt'])")
                .defineList("blacklist", Arrays.asList("minecraft:armor_stand"), obj -> obj instanceof String);

        WHITELIST = BUILDER
                .comment("If this list is NOT empty, ONLY these mobs will be allowed to heal.")
                .defineList("whitelist", Arrays.asList(), obj -> obj instanceof String);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}