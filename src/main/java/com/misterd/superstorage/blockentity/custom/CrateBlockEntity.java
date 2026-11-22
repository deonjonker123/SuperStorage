package com.misterd.superstorage.blockentity.custom;

import com.misterd.superstorage.block.custom.CrateBlock;
import com.misterd.superstorage.component.CollectorSettings;
import com.misterd.superstorage.component.CompactingSettings;
import com.misterd.superstorage.component.FilterSettings;
import com.misterd.superstorage.component.HopperSettings;
import com.misterd.superstorage.component.PackedCrate;
import com.misterd.superstorage.component.SSDataComponents;
import com.misterd.superstorage.config.SSConfig;
import com.misterd.superstorage.util.*;
import com.misterd.superstorage.blockentity.SSBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    public boolean isTaped() {
        return getBlockState().hasProperty(CrateBlock.TAPED) && getBlockState().getValue(CrateBlock.TAPED);
    }

    public @Nullable Component tape() {
        if (isTaped()) {
            return Component.translatable("message.superstorage.tape.already");
        }
        for (ItemStack stack : itemHandler.copyToList()) {
            if (holdsItems(stack)) {
                return Component.translatable("message.superstorage.tape.nested");
            }
        }
        if (level == null) {
            return null;
        }
        int size = PackedCrate.CODEC.encodeStart(level.registryAccess().createSerializationContext(NbtOps.INSTANCE), pack())
                .result().map(Tag::sizeInBytes).orElse(Integer.MAX_VALUE);
        if (size > SSConfig.TAPE_MAX_BYTES.get()) {
            return Component.translatable("message.superstorage.tape.too_large");
        }
        level.setBlock(worldPosition, getBlockState().setValue(CrateBlock.TAPED, true), Block.UPDATE_ALL);
        invalidateCapabilities();
        return null;
    }

    private static boolean holdsItems(ItemStack stack) {
        if (stack.has(SSDataComponents.PACKED_CRATE.get())) {
            return true;
        }
        ItemContainerContents container = stack.get(DataComponents.CONTAINER);
        if (container != null && !container.equals(ItemContainerContents.EMPTY)) {
            return true;
        }
        BundleContents bundle = stack.get(DataComponents.BUNDLE_CONTENTS);
        return bundle != null && !bundle.isEmpty();
    }

    private PackedCrate pack() {
        List<PackedCrate.Entry> entries = new ArrayList<>();
        NonNullList<ItemStack> stacks = itemHandler.copyToList();
        for (int i = 0; i < stacks.size(); i++) {
            if (!stacks.get(i).isEmpty()) {
                entries.add(new PackedCrate.Entry(i, stacks.get(i)));
            }
        }
        return new PackedCrate(List.copyOf(entries), ItemContainerContents.fromItems(upgrades.copyToList()));
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CrateBlockEntity crate) {
        if (!(level instanceof ServerLevel serverLevel) || crate.isTaped()) {
            return;
        }
        long time = level.getGameTime();
        if (time % SSConfig.HOPPER_UPGRADE_INTERVAL.get() == 0) {
            crate.runHopper(serverLevel);
        }
        if (time % SSConfig.COMPACTING_UPGRADE_INTERVAL.get() == 0) {
            crate.runCompacting(serverLevel);
        }
        if (time % SSConfig.COLLECTOR_UPGRADE_INTERVAL.get() == 0) {
            crate.runCollector(serverLevel);
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

    private void runCompacting(ServerLevel serverLevel) {
        ItemStack upgrade = upgrades.get(UpgradeType.COMPACTING);
        if (upgrade.isEmpty()) {
            return;
        }
        CompactingSettings settings = CompactingSettings.of(upgrade);
        int size = settings.size();
        int needed = size * size;
        Predicate<ItemResource> filter = FilterSettings.predicate(settings.filter());
        NonNullList<ItemStack> stacks = itemHandler.copyToList();
        Map<Item, Integer> totals = new LinkedHashMap<>();
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty() && stack.getComponentsPatch().isEmpty()) {
                totals.merge(stack.getItem(), stack.getCount(), Integer::sum);
            }
        }
        boolean changed = false;
        for (Map.Entry<Item, Integer> entry : totals.entrySet()) {
            Item item = entry.getKey();
            if (entry.getValue() < needed || !filter.test(ItemResource.of(new ItemStack(item)))) {
                continue;
            }
            ItemStack result = CompactingRecipes.result(serverLevel, item, size, settings.reversibleOnly());
            if (result.isEmpty()) {
                continue;
            }
            int crafts = entry.getValue() / needed;
            while (crafts > 0) {
                NonNullList<ItemStack> attempt = copy(stacks);
                remove(attempt, item, crafts * needed);
                int leftover = insert(attempt, result, crafts * result.getCount());
                if (leftover == 0) {
                    stacks = attempt;
                    changed = true;
                    break;
                }
                crafts -= Math.max(1, (leftover + result.getCount() - 1) / result.getCount());
            }
        }
        if (changed) {
            itemHandler.replace(stacks);
        }
    }

    private void runCollector(ServerLevel serverLevel) {
        ItemStack upgrade = upgrades.get(UpgradeType.COLLECTOR);
        if (upgrade.isEmpty()) {
            return;
        }
        CollectorSettings settings = CollectorSettings.of(upgrade);
        Predicate<ItemResource> filter = FilterSettings.predicate(settings.filter());
        int delay = SSConfig.COLLECTOR_PICKUP_DELAY.get();
        for (ItemEntity entity : serverLevel.getEntitiesOfClass(ItemEntity.class, settings.area(worldPosition),
                e -> e.isAlive() && !e.hasPickUpDelay() && e.getAge() >= delay)) {
            ItemStack stack = entity.getItem();
            ItemResource resource = ItemResource.of(stack);
            if (!filter.test(resource)) {
                continue;
            }
            int inserted = ResourceHandlerUtil.insertStacking(itemHandler, resource, stack.getCount(), null);
            if (inserted <= 0) {
                continue;
            }
            ItemStack remaining = stack.copyWithCount(stack.getCount() - inserted);
            if (remaining.isEmpty()) {
                entity.discard();
            } else {
                entity.setItem(remaining);
            }
        }
    }

    private static NonNullList<ItemStack> copy(NonNullList<ItemStack> stacks) {
        NonNullList<ItemStack> copy = NonNullList.withSize(stacks.size(), ItemStack.EMPTY);
        for (int i = 0; i < stacks.size(); i++) {
            copy.set(i, stacks.get(i).copy());
        }
        return copy;
    }

    private static void remove(NonNullList<ItemStack> stacks, Item item, int amount) {
        for (int i = stacks.size() - 1; i >= 0 && amount > 0; i--) {
            ItemStack stack = stacks.get(i);
            if (stack.is(item) && stack.getComponentsPatch().isEmpty()) {
                int taken = Math.min(amount, stack.getCount());
                stack.shrink(taken);
                amount -= taken;
                if (stack.isEmpty()) {
                    stacks.set(i, ItemStack.EMPTY);
                }
            }
        }
    }

    private static int insert(NonNullList<ItemStack> stacks, ItemStack result, int amount) {
        for (ItemStack stack : stacks) {
            if (amount == 0) {
                return 0;
            }
            if (!stack.isEmpty() && ItemStack.isSameItemSameComponents(stack, result)) {
                int added = Math.min(amount, stack.getMaxStackSize() - stack.getCount());
                stack.grow(added);
                amount -= added;
            }
        }
        for (int i = 0; i < stacks.size() && amount > 0; i++) {
            if (stacks.get(i).isEmpty()) {
                int added = Math.min(amount, result.getMaxStackSize());
                stacks.set(i, result.copyWithCount(added));
                amount -= added;
            }
        }
        return amount;
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
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);
        PackedCrate packed = components.get(SSDataComponents.PACKED_CRATE.get());
        if (packed == null) {
            return;
        }
        NonNullList<ItemStack> stacks = NonNullList.withSize(tier.slotCount(), ItemStack.EMPTY);
        for (PackedCrate.Entry entry : packed.items()) {
            if (entry.slot() >= 0 && entry.slot() < stacks.size()) {
                stacks.set(entry.slot(), entry.stack().copy());
            }
        }
        itemHandler.replace(stacks);
        NonNullList<ItemStack> installed = packed.upgradeList();
        for (UpgradeType type : UpgradeType.values()) {
            upgrades.put(type, installed.get(type.ordinal()));
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        if (isTaped()) {
            components.set(SSDataComponents.PACKED_CRATE.get(), pack());
        }
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (level != null && !isTaped()) {
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

        void replace(NonNullList<ItemStack> stacks) {
            setStacks(stacks);
            setChanged();
        }
    }
}