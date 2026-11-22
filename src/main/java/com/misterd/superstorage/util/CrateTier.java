package com.misterd.superstorage.util;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum CrateTier implements StringRepresentable {
    WOODEN("wooden", 9, 6),
    COPPER("copper", 12, 6),
    IRON("iron", 12, 10),
    GOLD("gold", 12, 14),
    EMERALD("emerald", 12, 18),
    DIAMOND("diamond", 12, 22),
    NETHERITE("netherite", 12, 26);

    public static final Codec<CrateTier> CODEC = StringRepresentable.fromEnum(CrateTier::values);

    public static final int SLOT_X = 8;
    public static final int SLOT_Y = 8;
    public static final int SLOT_SIZE = 18;

    private final String name;
    private final int columns;
    private final int rows;

    CrateTier(String name, int columns, int rows) {
        this.name = name;
        this.columns = columns;
        this.rows = rows;
    }

    public int columns() {
        return columns;
    }

    public int rows() {
        return rows;
    }

    public int slotCount() {
        return columns * rows;
    }

    public boolean fixedLayout() {
        return this == WOODEN;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public static CrateTier byId(int id) {
        CrateTier[] values = values();
        return values[Math.floorMod(id, values.length)];
    }
}