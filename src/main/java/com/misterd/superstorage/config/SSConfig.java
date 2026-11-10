package com.misterd.superstorage.config;

import com.misterd.superstorage.util.DrumTier;
import com.misterd.superstorage.util.SuperTier;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.EnumMap;
import java.util.Map;

public final class SSConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.IntValue TRANSFER_INTERVAL;
    public static final ModConfigSpec.IntValue SUPER_CHEST_TRANSFER_RATE;
    public static final ModConfigSpec.IntValue SUPER_TANK_TRANSFER_RATE;

    private static final Map<SuperTier, ModConfigSpec.LongValue> SUPER_CHEST_CAPACITY = new EnumMap<>(SuperTier.class);
    private static final Map<SuperTier, ModConfigSpec.IntValue> SUPER_TANK_CAPACITY = new EnumMap<>(SuperTier.class);

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

        builder.comment("Capacity of each Super Chest tier, in items").push("super_chest_capacity");
        for (SuperTier tier : SuperTier.values()) {
            if (tier.isCreative()) {
                continue;
            }
            SUPER_CHEST_CAPACITY.put(tier, builder.defineInRange("tier_" + tier.getSerializedName(), tier.defaultCapacity(), 1L, Long.MAX_VALUE / 2));
        }
        builder.pop();

        builder.comment("Capacity of each Super Tank tier, in millibuckets").push("super_tank_capacity");
        for (SuperTier tier : SuperTier.values()) {
            if (tier.isCreative()) {
                continue;
            }
            SUPER_TANK_CAPACITY.put(tier, builder.defineInRange("tier_" + tier.getSerializedName(), (int) tier.defaultCapacity(), 1000, Integer.MAX_VALUE));
        }
        builder.pop();

        SPEC = builder.build();
    }

    private SSConfig() {
    }

    public static long superChestCapacity(SuperTier tier) {
        return SPEC.isLoaded() ? SUPER_CHEST_CAPACITY.get(tier).get() : tier.defaultCapacity();
    }

    public static int superTankCapacity(SuperTier tier) {
        return SPEC.isLoaded() ? SUPER_TANK_CAPACITY.get(tier).get() : (int) tier.defaultCapacity();
    }
}