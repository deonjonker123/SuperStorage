package com.misterd.superstorage.blockentity.custom;

import com.misterd.superstorage.util.RelativeSide;
import com.misterd.superstorage.util.SuperConfig;

public interface SuperBlockEntity {
    SuperConfig getConfig();

    long getAmount();

    long getCapacity();

    boolean isCreative();

    void setLocked(boolean locked);

    void setVoidOverflow(boolean voidOverflow);

    void setCapabilitiesEnabled(boolean enabled);

    void cycleSide(RelativeSide side);
}