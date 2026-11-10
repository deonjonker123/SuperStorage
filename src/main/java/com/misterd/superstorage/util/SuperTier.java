package com.misterd.superstorage.util;

import com.misterd.superstorage.config.SSConfig;
import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum SuperTier implements StringRepresentable {
    I("i", 4_000_000L),
    II("ii", 8_000_000L),
    III("iii", 16_000_000L),
    IV("iv", 32_000_000L),
    CREATIVE("creative", Long.MAX_VALUE);

    public static final Codec<SuperTier> CODEC = StringRepresentable.fromEnum(SuperTier::values);

    private final String name;
    public final long capacity;

    SuperTier(String name, long capacity) {
        this.name = name;
        this.capacity = capacity;
    }

    public long defaultCapacity() {
        return capacity;
    }

    public boolean isCreative() {
        return this == CREATIVE;
    }

    public long itemCapacity() {
        return isCreative() ? Long.MAX_VALUE : SSConfig.superChestCapacity(this);
    }

    public int fluidCapacity() {
        return isCreative() ? Integer.MAX_VALUE : SSConfig.superTankCapacity(this);
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}