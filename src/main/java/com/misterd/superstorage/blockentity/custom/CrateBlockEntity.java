package com.misterd.superstorage.blockentity.custom;

import com.misterd.superstorage.block.custom.CrateBlock;
import com.misterd.superstorage.component.FilterSettings;
import com.misterd.superstorage.component.HopperSettings;
import com.misterd.superstorage.config.SSConfig;
import com.misterd.superstorage.util.*;
import com.misterd.superstorage.blockentity.SSBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class CrateBlockEntity extends BlockEntity {
    private static final String INVENTORY_KEY = "inventory";
    private static final String UPGRADES_KEY = "upgrades";

    private final CrateTier tier;
    private final CrateItemHandler itemHandler;
    private final CrateUpgrades upgrades = new CrateUpgrades(this::setChanged);
    @SuppressWarnings("unchecked")
    private final BlockCapabilityCache<ResourceHandler<ItemResource>, @Nullable Direction>[] neighbours = new BlockCapabilityCache[Direction.values().length];

    public CrateBlockEntity(BlockPos pos, BlockState state) {
        super(SSBlockEntities.CRATE_BE.get(), pos, state);
        this.tier = state.getBlock() instanceof CrateBlock crate ? crate.getTier() : CrateTier.WOODEN;
        this.itemHandler = new CrateItemHandler(tier.slotCount());
    }

    public CrateTier getTier() {
        return tier;
    }

    public ItemStacksResourceHandler getItemHandler() {
        return itemHandler;
    }

    public CrateUpgrades getUpgrades() {
        return upgrades;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CrateBlockEntity crate) {
        if (level instanceof ServerLevel serverLevel && level.getGameTime() % SSConfig.HOPPER_UPGRADE_INTERVAL.get() == 0) {
            crate.runHopper(serverLevel);
        }
    }

    private void runHopper(ServerLevel serverLevel) {
        ItemStack upgrade = upgrades.get(UpgradeType.HOPPER);
        if (upgrade.isEmpty()) {
            return;
        }
        HopperSettings settings = HopperSettings.of(upgrade);
        Predicate<ItemResource> pull = FilterSettings.predicate(settings.filter(HopperSettings.INPUT_FILTER));
        Predicate<ItemResource> push = FilterSettings.predicate(settings.filter(HopperSettings.OUTPUT_FILTER));
        int rate = SSConfig.HOPPER_UPGRADE_RATE.get();
        for (Direction direction : Direction.values()) {
            SideMode mode = settings.side(direction);
            if (mode == SideMode.NONE) {
                continue;
            }
            ResourceHandler<ItemResource> neighbour = neighbour(serverLevel, direction);
            if (neighbour == null) {
                continue;
            }
            if (mode.canOutput()) {
                ResourceHandlerUtil.move(itemHandler, neighbour, push, rate, null);
            }
            if (mode.canInput()) {
                ResourceHandlerUtil.move(neighbour, itemHandler, pull, rate, null);
            }
        }
    }

    private @Nullable ResourceHandler<ItemResource> neighbour(ServerLevel serverLevel, Direction direction) {
        BlockCapabilityCache<ResourceHandler<ItemResource>, @Nullable Direction> cache = neighbours[direction.ordinal()];
        if (cache == null) {
            cache = BlockCapabilityCache.create(Capabilities.Item.BLOCK, serverLevel, worldPosition.relative(direction), direction.getOpposite(), () -> !isRemoved(), () -> {});
            neighbours[direction.ordinal()] = cache;
        }
        return cache.getCapability();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        itemHandler.serialize(output.child(INVENTORY_KEY));
        upgrades.serialize(output.child(UPGRADES_KEY));
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        itemHandler.deserialize(input.childOrEmpty(INVENTORY_KEY));
        upgrades.deserialize(input.childOrEmpty(UPGRADES_KEY));
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (level != null) {
            Containers.dropContents(level, pos, itemHandler.copyToList());
            Containers.dropContents(level, pos, upgrades.copyToList());
        }
    }

    private class CrateItemHandler extends ItemStacksResourceHandler {
        private final int expectedSize;

        CrateItemHandler(int size) {
            super(size);
            this.expectedSize = size;
        }

        @Override
        public boolean isValid(int index, ItemResource resource) {
            return StorageGuard.isAllowed(resource);
        }

        @Override
        protected void onContentsChanged(int index, ItemStack previousContents) {
            setChanged();
        }

        @Override
        public void deserialize(ValueInput input) {
            super.deserialize(input);
            if (size() != expectedSize) {
                NonNullList<ItemStack> resized = NonNullList.withSize(expectedSize, ItemStack.EMPTY);
                NonNullList<ItemStack> loaded = copyToList();
                for (int i = 0; i < Math.min(expectedSize, loaded.size()); i++) {
                    resized.set(i, loaded.get(i));
                }
                setStacks(resized);
            }
        }
    }
}