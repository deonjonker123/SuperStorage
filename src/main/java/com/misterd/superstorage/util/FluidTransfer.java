package com.misterd.superstorage.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.neoforged.neoforge.transfer.resource.ResourceStack;
import org.jspecify.annotations.Nullable;

public final class FluidTransfer {
    private FluidTransfer() {
    }

    public static boolean moveBucket(ResourceHandler<FluidResource> tank, @Nullable ResourceHandler<FluidResource> item, Level level, BlockPos pos, Player player) {
        if (item == null) {
            return false;
        }
        ResourceStack<FluidResource> moved = ResourceHandlerUtil.moveFirst(tank, item, resource -> true, FluidType.BUCKET_VOLUME, null);
        if (moved != null) {
            FluidUtil.triggerSoundAndGameEvent(moved.resource(), level, Vec3.atCenterOf(pos), player, true);
            return true;
        }
        moved = ResourceHandlerUtil.moveFirst(item, tank, resource -> true, FluidType.BUCKET_VOLUME, null);
        if (moved != null) {
            FluidUtil.triggerSoundAndGameEvent(moved.resource(), level, Vec3.atCenterOf(pos), player, false);
            return true;
        }
        return false;
    }

    public static FluidResource firstFluid(@Nullable ResourceHandler<FluidResource> handler) {
        if (handler == null) {
            return FluidResource.EMPTY;
        }
        for (int i = 0; i < handler.size(); i++) {
            FluidResource resource = handler.getResource(i);
            if (!resource.isEmpty() && handler.getAmountAsLong(i) > 0) {
                return resource;
            }
        }
        return FluidResource.EMPTY;
    }
}