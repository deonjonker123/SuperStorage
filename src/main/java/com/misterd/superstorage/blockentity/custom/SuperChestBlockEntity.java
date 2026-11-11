package com.misterd.superstorage.blockentity.custom;

import com.misterd.superstorage.block.custom.SuperChestBlock;
import com.misterd.superstorage.blockentity.SSBlockEntities;
import com.misterd.superstorage.component.SSDataComponents;
import com.misterd.superstorage.component.SuperChestContents;
import com.misterd.superstorage.component.SuperSettings;
import com.misterd.superstorage.config.SSConfig;
import com.misterd.superstorage.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SuperChestBlockEntity extends BlockEntity implements SuperBlockEntity {
    private static final String ITEM_KEY = "item";
    private static final String COUNT_KEY = "count";
    private static final String CONFIG_KEY = "config";
    private static final int DOUBLE_CLICK_TICKS = 10;
    private static final int COUNT_SYNC_TICKS = 4;
    private static final float SOUND_VOLUME = 0.4F;
    private static final float INSERT_PITCH = 0.8F;
    private static final float EXTRACT_PITCH = 1.2F;

    private final SuperTier tier;
    private final SuperConfig config = new SuperConfig();
    private final Storage storage = new Storage();
    private final ResourceHandler<ItemResource> inputOnly = new Restricted(true, false);
    private final ResourceHandler<ItemResource> outputOnly = new Restricted(false, true);
    private final Map<UUID, Long> lastInsertTicks = new HashMap<>();
    @SuppressWarnings("unchecked")
    private final BlockCapabilityCache<ResourceHandler<ItemResource>, @Nullable Direction>[] neighbours = new BlockCapabilityCache[RelativeSide.values().length];
    private ItemResource stored = ItemResource.EMPTY;
    private long count;
    private boolean countDirty;

    public SuperChestBlockEntity(BlockPos pos, BlockState state) {
        super(SSBlockEntities.SUPER_CHEST_BE.get(), pos, state);
        this.tier = state.getBlock() instanceof SuperChestBlock chest ? chest.getTier() : SuperTier.I;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, SuperChestBlockEntity chest) {
        if (level instanceof ServerLevel serverLevel && level.getGameTime() % SSConfig.TRANSFER_INTERVAL.get() == 0) {
            chest.transfer(serverLevel);
        }
        chest.syncCount();
    }

    private void transfer(ServerLevel serverLevel) {
        if (!config.isCapabilitiesEnabled()) {
            return;
        }
        Direction facing = getBlockState().getValue(SuperChestBlock.FACING);
        int rate = SSConfig.SUPER_CHEST_TRANSFER_RATE.get();
        for (RelativeSide side : RelativeSide.values()) {
            SideMode mode = config.getSide(side);
            if (mode == SideMode.NONE) {
                continue;
            }
            ResourceHandler<ItemResource> neighbour = neighbour(serverLevel, side, facing);
            if (neighbour == null) {
                continue;
            }
            if (mode.canOutput() && !stored.isEmpty() && (count > 0 || isCreative())) {
                ResourceHandlerUtil.move(storage, neighbour, resource -> true, rate, null);
            }
            if (mode.canInput()) {
                ResourceHandlerUtil.move(neighbour, storage, this::accepts, rate, null);
            }
        }
    }

    private @Nullable ResourceHandler<ItemResource> neighbour(ServerLevel serverLevel, RelativeSide side, Direction facing) {
        BlockCapabilityCache<ResourceHandler<ItemResource>, @Nullable Direction> cache = neighbours[side.ordinal()];
        if (cache == null) {
            Direction direction = side.toDirection(facing);
            cache = BlockCapabilityCache.create(Capabilities.Item.BLOCK, serverLevel, worldPosition.relative(direction), direction.getOpposite(), () -> !isRemoved(), () -> {});
            neighbours[side.ordinal()] = cache;
        }
        return cache.getCapability();
    }

    public SuperTier getTier() {
        return tier;
    }

    @Override
    public SuperConfig getConfig() {
        return config;
    }

    public ItemResource getStored() {
        return stored;
    }

    public long getCount() {
        return count;
    }

    @Override
    public long getAmount() {
        return count;
    }

    @Override
    public boolean isCreative() {
        return tier.isCreative();
    }

    public void setCreativeItem(ItemStack stack) {
        if (stack.isEmpty() || !StorageGuard.isAllowed(ItemResource.of(stack))) {
            return;
        }
        stored = ItemResource.of(stack);
        onChanged(true);
    }

    @Override
    public long getCapacity() {
        return tier.itemCapacity();
    }

    public ResourceHandler<ItemResource> getStorage() {
        return storage;
    }

    public @Nullable ResourceHandler<ItemResource> getItemHandler(@Nullable Direction side) {
        if (!config.isCapabilitiesEnabled()) {
            return null;
        }
        if (side == null) {
            return storage;
        }
        SideMode mode = config.getSide(RelativeSide.of(getBlockState().getValue(SuperChestBlock.FACING), side));
        return switch (mode) {
            case NONE -> null;
            case INPUT -> inputOnly;
            case OUTPUT -> outputOnly;
            case BOTH -> storage;
        };
    }

    public boolean accepts(ItemStack stack) {
        return !stack.isEmpty() && accepts(ItemResource.of(stack));
    }

    private boolean accepts(ItemResource resource) {
        if (resource.isEmpty() || !StorageGuard.isAllowed(resource)) {
            return false;
        }
        return isCreative() || stored.isEmpty() || stored.equals(resource);
    }

    public int insert(ItemStack stack) {
        int inserted = insertSilently(stack);
        if (inserted > 0) {
            playSound(INSERT_PITCH);
        }
        return inserted;
    }

    public int insertSilently(ItemStack stack) {
        if (!accepts(stack)) {
            return 0;
        }
        try (Transaction tx = Transaction.openRoot()) {
            int inserted = storage.insert(0, ItemResource.of(stack), stack.getCount(), tx);
            tx.commit();
            return inserted;
        }
    }

    public ItemStack extract(int amount) {
        if (stored.isEmpty() || (count == 0 && !isCreative())) {
            return ItemStack.EMPTY;
        }
        ItemResource resource = stored;
        try (Transaction tx = Transaction.openRoot()) {
            int extracted = storage.extract(0, resource, amount, tx);
            tx.commit();
            if (extracted > 0) {
                playSound(EXTRACT_PITCH);
            }
            return resource.toStack(extracted);
        }
    }

    private void playSound(float pitch) {
        if (level != null && !level.isClientSide()) {
            level.playSound(null, worldPosition, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, SOUND_VOLUME, pitch);
        }
    }

    public void insertFromHand(Player player, ItemStack held) {
        long now = level == null ? 0 : level.getGameTime();
        boolean doubleClick = isDoubleClick(player, now);
        recordInsert(player, now);
        if (doubleClick) {
            insertAllFromInventory(player);
        } else {
            held.shrink(insert(held));
        }
    }

    public boolean tryDoubleClickInsert(Player player) {
        if (isCreative()) {
            return false;
        }
        long now = level == null ? 0 : level.getGameTime();
        if (!isDoubleClick(player, now)) {
            return false;
        }
        recordInsert(player, now);
        insertAllFromInventory(player);
        return true;
    }

    private void recordInsert(Player player, long now) {
        lastInsertTicks.values().removeIf(last -> now - last > DOUBLE_CLICK_TICKS);
        lastInsertTicks.put(player.getUUID(), now);
    }

    private boolean isDoubleClick(Player player, long now) {
        Long last = lastInsertTicks.get(player.getUUID());
        return last != null && now - last <= DOUBLE_CLICK_TICKS;
    }

    private void insertAllFromInventory(Player player) {
        if (stored.isEmpty()) {
            return;
        }
        boolean insertedAny = false;
        for (int i = 0; i < Inventory.INVENTORY_SIZE; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && stored.matches(stack)) {
                int inserted = insertSilently(stack);
                if (inserted == 0) {
                    break;
                }
                stack.shrink(inserted);
                insertedAny = true;
            }
        }
        player.getInventory().setChanged();
        if (insertedAny) {
            playSound(INSERT_PITCH);
        }
    }

    @Override
    public void setLocked(boolean locked) {
        if (locked && stored.isEmpty()) {
            return;
        }
        config.setLocked(locked);
        if (!locked && count == 0 && !isCreative()) {
            stored = ItemResource.EMPTY;
        }
        onChanged(true);
    }

    @Override
    public void setVoidOverflow(boolean voidOverflow) {
        config.setVoidOverflow(voidOverflow);
        onChanged(true);
    }

    @Override
    public void setCapabilitiesEnabled(boolean enabled) {
        config.setCapabilitiesEnabled(enabled);
        invalidateCapabilities();
        onChanged(true);
    }

    @Override
    public void cycleSide(RelativeSide side) {
        config.setSide(side, config.getSide(side).next());
        invalidateCapabilities();
        onChanged(true);
    }

    public int getComparatorSignal() {
        if (isCreative()) {
            return stored.isEmpty() ? 0 : 15;
        }
        if (count == 0) {
            return 0;
        }
        return Mth.clamp(1 + (int) (count * 14 / tier.itemCapacity()), 1, 15);
    }

    @Override
    public void setBlockState(BlockState state) {
        boolean rotated = state.getValue(SuperChestBlock.FACING) != getBlockState().getValue(SuperChestBlock.FACING);
        super.setBlockState(state);
        if (rotated) {
            Arrays.fill(neighbours, null);
            if (level != null) {
                invalidateCapabilities();
            }
        }
    }

    private void syncCount() {
        if (countDirty && level != null && level.getGameTime() % COUNT_SYNC_TICKS == 0) {
            countDirty = false;
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    private void onChanged(boolean syncToClient) {
        setChanged();
        if (syncToClient && level != null && !level.isClientSide()) {
            countDirty = false;
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (!stored.isEmpty()) {
            output.store(ITEM_KEY, ItemResource.CODEC, stored);
        }
        output.putLong(COUNT_KEY, count);
        config.save(output.child(CONFIG_KEY));
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        stored = input.read(ITEM_KEY, ItemResource.CODEC).orElse(ItemResource.EMPTY);
        count = Math.max(0, input.getLongOr(COUNT_KEY, 0));
        config.load(input.childOrEmpty(CONFIG_KEY));
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);
        SuperChestContents contents = components.get(SSDataComponents.SUPER_CHEST_CONTENTS.get());
        if (contents != null) {
            stored = contents.item();
            count = Mth.clamp(contents.count(), 0, tier.itemCapacity());
        }
        config.fromSettings(components.getOrDefault(SSDataComponents.SUPER_SETTINGS.get(), SuperSettings.DEFAULT));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        if (!stored.isEmpty()) {
            components.set(SSDataComponents.SUPER_CHEST_CONTENTS.get(), new SuperChestContents(stored, count));
        }
        if (!config.isDefault()) {
            components.set(SSDataComponents.SUPER_SETTINGS.get(), config.toSettings());
        }
    }

    private record Snapshot(ItemResource stored, long count) {
    }

    private class Storage extends SnapshotJournal<Snapshot> implements ResourceHandler<ItemResource> {
        @Override
        public int size() {
            return 1;
        }

        @Override
        public ItemResource getResource(int index) {
            return index == 0 ? stored : ItemResource.EMPTY;
        }

        @Override
        public long getAmountAsLong(int index) {
            if (index != 0) {
                return 0;
            }
            return isCreative() ? (stored.isEmpty() ? 0 : Integer.MAX_VALUE) : count;
        }

        @Override
        public long getCapacityAsLong(int index, ItemResource resource) {
            return index == 0 ? tier.itemCapacity() : 0;
        }

        @Override
        public boolean isValid(int index, ItemResource resource) {
            return index == 0 && accepts(resource);
        }

        @Override
        public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
            if (isCreative()) {
                return index == 0 && amount > 0 && !resource.isEmpty() ? amount : 0;
            }
            if (index != 0 || amount <= 0 || !accepts(resource)) {
                return 0;
            }
            int toInsert = (int) Math.min(amount, tier.itemCapacity() - count);
            boolean voiding = config.isVoidOverflow();
            if (toInsert <= 0 && !voiding) {
                return 0;
            }
            updateSnapshots(transaction);
            if (stored.isEmpty()) {
                stored = resource;
            }
            count += Math.max(0, toInsert);
            return voiding ? amount : toInsert;
        }

        @Override
        public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
            if (isCreative()) {
                return index == 0 && amount > 0 && !stored.isEmpty() && stored.equals(resource) ? amount : 0;
            }
            if (index != 0 || amount <= 0 || count == 0 || stored.isEmpty() || !stored.equals(resource)) {
                return 0;
            }
            int extracted = (int) Math.min(amount, count);
            updateSnapshots(transaction);
            count -= extracted;
            if (count == 0 && !config.isLocked()) {
                stored = ItemResource.EMPTY;
            }
            return extracted;
        }

        @Override
        protected Snapshot createSnapshot() {
            return new Snapshot(stored, count);
        }

        @Override
        protected void revertToSnapshot(Snapshot snapshot) {
            stored = snapshot.stored();
            count = snapshot.count();
        }

        @Override
        protected void onRootCommit(Snapshot original) {
            if (original.stored().equals(stored)) {
                setChanged();
                countDirty = true;
            } else {
                onChanged(true);
            }
        }
    }

    private class Restricted implements ResourceHandler<ItemResource> {
        private final boolean canInsert;
        private final boolean canExtract;

        Restricted(boolean canInsert, boolean canExtract) {
            this.canInsert = canInsert;
            this.canExtract = canExtract;
        }

        @Override
        public int size() {
            return storage.size();
        }

        @Override
        public ItemResource getResource(int index) {
            return storage.getResource(index);
        }

        @Override
        public long getAmountAsLong(int index) {
            return storage.getAmountAsLong(index);
        }

        @Override
        public long getCapacityAsLong(int index, ItemResource resource) {
            return storage.getCapacityAsLong(index, resource);
        }

        @Override
        public boolean isValid(int index, ItemResource resource) {
            return canInsert && storage.isValid(index, resource);
        }

        @Override
        public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
            return canInsert ? storage.insert(index, resource, amount, transaction) : 0;
        }

        @Override
        public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
            return canExtract ? storage.extract(index, resource, amount, transaction) : 0;
        }
    }
}