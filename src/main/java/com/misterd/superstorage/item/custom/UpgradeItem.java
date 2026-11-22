package com.misterd.superstorage.item.custom;

import com.misterd.superstorage.util.UpgradeType;
import net.minecraft.world.item.Item;

public class UpgradeItem extends Item {
    private final UpgradeType type;

    public UpgradeItem(UpgradeType type, Properties properties) {
        super(properties);
        this.type = type;
    }

    public UpgradeType getType() {
        return type;
    }
}