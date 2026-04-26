package com.thenimbleninja.mobhealthregen;

import net.neoforged.neoforge.common.ModConfigSpec;
import java.util.List;

public class ModConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.DoubleValue SCAN_RADIUS;
    public static final ModConfigSpec.BooleanValue HEAL_PASSIVES;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> BLACKLIST;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> WHITELIST;

    static {
        BUILDER.push("General Settings");

        SCAN_RADIUS = BUILDER
                .comment("How far to look for mobs around the dying player.")
                .defineInRange("scanRadius", 32.0, 1.0, 128.0);

        HEAL_PASSIVES = BUILDER
                .comment("If true, animals (cows, etc.) will also heal. If false, only monsters heal.")
                .define("healPassives", false);

        // Use List.of("item") instead of Arrays.asList
        BLACKLIST = BUILDER
                .comment("Registry names of mobs that should NEVER heal (e.g. ['minecraft:creeper', 'minecraft:armor_stand'])")
                .defineList("blacklist", List.of("minecraft:armor_stand"), obj -> obj instanceof String);

        // Use List.of() for an empty list
        WHITELIST = BUILDER
                .comment("If this list is NOT empty, ONLY these mobs will be allowed to heal.")
                .defineList("whitelist", List.of(), obj -> obj instanceof String);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}