package com.misterd.superstorage.util;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum SideMode implements StringRepresentable {
    NONE("none", false, false),
    INPUT("input", true, false),
    OUTPUT("output", false, true),
    BOTH("both", true, true);

    public static final Codec<SideMode> CODEC = StringRepresentable.fromEnum(SideMode::values);

    private final String name;
    private final boolean input;
    private final boolean output;

    SideMode(String name, boolean input, boolean output) {
        this.name = name;
        this.input = input;
        this.output = output;
    }

    public boolean canInput() {
        return input;
    }

    public boolean canOutput() {
        return output;
    }

    public SideMode next() {
        SideMode[] values = values();
        return values[(ordinal() + 1) % values.length];
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}