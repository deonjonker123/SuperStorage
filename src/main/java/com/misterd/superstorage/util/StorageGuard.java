package com.misterd.superstorage.util;

import com.misterd.superstorage.block.custom.SuperChestBlock;
import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.transfer.item.ItemResource;

public final class StorageGuard {
    private StorageGuard() {
    }

    public static boolean isAllowed(ItemResource resource) {
        return !(resource.getItem() instanceof BlockItem blockItem
                && blockItem.getBlock() instanceof SuperChestBlock
                && !resource.getComponentsPatch().isEmpty());
    }
}