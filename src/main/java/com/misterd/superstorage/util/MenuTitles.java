package com.misterd.superstorage.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class MenuTitles {
    private MenuTitles() {
    }

    public static Component of(BlockEntity blockEntity, Block block) {
        Component customName = blockEntity.components().get(DataComponents.CUSTOM_NAME);
        return customName != null ? customName : block.getName();
    }
}