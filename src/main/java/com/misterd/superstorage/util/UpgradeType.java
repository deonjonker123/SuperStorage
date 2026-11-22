package com.misterd.superstorage.util;

public enum UpgradeType {
    HOPPER(120),
    COLLECTOR(104),
    COMPACTING(112);

    private final int iconU;

    UpgradeType(int iconU) {
        this.iconU = iconU;
    }

    public int iconU() {
        return iconU;
    }
}