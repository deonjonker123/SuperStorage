package com.misterd.superstorage.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class SSConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.IntValue TRANSFER_INTERVAL;
    public static final ModConfigSpec.IntValue SUPER_CHEST_TRANSFER_RATE;
    public static final ModConfigSpec.IntValue SUPER_TANK_TRANSFER_RATE;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.push("super_blocks");
        TRANSFER_INTERVAL = builder
                .comment("Ticks between each automatic push/pull on configured sides")
                .defineInRange("transferInterval", 8, 1, 1200);
        SUPER_CHEST_TRANSFER_RATE = builder
                .comment("Items a Super Chest pushes or pulls per side, per transfer")
                .defineInRange("superChestTransferRate", 64, 1, Integer.MAX_VALUE);
        SUPER_TANK_TRANSFER_RATE = builder
                .comment("Millibuckets a Super Tank pushes or pulls per side, per transfer")
                .defineInRange("superTankTransferRate", 4000, 1, Integer.MAX_VALUE);
        builder.pop();
        SPEC = builder.build();
    }

    private SSConfig() {
    }
}