package com.misterd.superstorage.util;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum DrumTier implements StringRepresentable {
    WOODEN("wooden", 16_000, 0xB8875A),
    COPPER("copper", 32_000, 0xE07B4F),
    IRON("iron", 64_000, 0xD8D8D8),
    GOLD("gold", 128_000, 0xFCDB4A),
    EMERALD("emerald", 256_000, 0x41D068),
    DIAMOND("diamond", 512_000, 0x4DE8E0),
    NETHERITE("netherite", 1_024_000, 0x9C8A8F);

    public static final Codec<DrumTier> CODEC = StringRepresentable.fromEnum(DrumTier::values);

    private final String name;
    private final int capacity;
    private final int color;

    DrumTier(String name, int capacity, int color) {
        this.name = name;
        this.capacity = capacity;
        this.color = color;
    }

    public int capacity() {
        return capacity;
    }

    public int color() {
        return color;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}