package com.misterd.superstorage.util;

import com.misterd.superstorage.block.custom.DrumBlock;
import com.misterd.superstorage.block.custom.SuperChestBlock;
import com.misterd.superstorage.block.custom.SuperTankBlock;
import com.misterd.superstorage.component.SSDataComponents;
import com.misterd.superstorage.component.SuperChestContents;
import com.misterd.superstorage.component.SuperTankContents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.transfer.item.ItemResource;

public final class StorageGuard {
    private StorageGuard() {
    }

    public static boolean isAllowed(ItemResource resource) {
        if (!(resource.getItem() instanceof BlockItem blockItem)) {
            return true;
        }
        Block block = blockItem.getBlock();
        if (block instanceof SuperChestBlock) {
            SuperChestContents contents = resource.get(SSDataComponents.SUPER_CHEST_CONTENTS.get());
            return contents == null || contents.item().isEmpty();
        }
        if (block instanceof SuperTankBlock) {
            SuperTankContents contents = resource.get(SSDataComponents.SUPER_TANK_CONTENTS.get());
            return contents == null || contents.fluid().isEmpty();
        }
        if (block instanceof DrumBlock) {
            SimpleFluidContent contents = resource.get(SSDataComponents.FLUID_CONTENT.get());
            return contents == null || contents.isEmpty();
        }
        return true;
    }
}