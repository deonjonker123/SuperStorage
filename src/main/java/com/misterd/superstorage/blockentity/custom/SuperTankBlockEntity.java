package com.misterd.superstorage.blockentity.custom;

import com.misterd.superstorage.block.custom.SuperTankBlock;
import com.misterd.superstorage.blockentity.SSBlockEntities;
import com.misterd.superstorage.component.SSDataComponents;
import com.misterd.superstorage.component.SuperSettings;
import com.misterd.superstorage.component.SuperTankContents;
import com.misterd.superstorage.config.SSConfig;
import com.misterd.superstorage.item.custom.DrumBlockItem;
import com.misterd.superstorage.util.RelativeSide;
import com.misterd.superstorage.util.SideMode;
import com.misterd.superstorage.util.SuperConfig;
import com.misterd.superstorage.util.SuperTier;
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
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
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
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.Nullable;

public class SuperTankBlockEntity extends BlockEntity implements SuperBlockEntity {
    private static final String FLUID_KEY = "fluid";
    private static final String AMOUNT_KEY = "amount";
    private static final String CONFIG_KEY = "config";
    private static final String IO_KEY = "io";
    private static final int DRUM_FILL_RATE = 4_000;
    private static final int COUNT_SYNC_TICKS = 4;

    private final SuperTier tier;
    private final SuperConfig config = new SuperConfig();
    private final Storage storage = new Storage();
    private final ResourceHandler<FluidResource> inputOnly = new Restricted(true, false);
    private final ResourceHandler<FluidResource> outputOnly = new Restricted(false, true);
    private final IoSlot ioSlot = new IoSlot();
    @SuppressWarnings("unchecked")
    private final BlockCapabilityCache<ResourceHandler<FluidResource>, @Nullable Direction>[] neighbours = new BlockCapabilityCache[RelativeSide.values().length];
    private FluidResource stored = FluidResource.EMPTY;
    private long amount;
    private boolean countDirty;

    public SuperTankBlockEntity(BlockPos pos, BlockState state) {
        super(SSBlockEntities.SUPER_TANK_BE.get(), pos, state);
        this.tier = state.getBlock() instanceof SuperTankBlock tank ? tank.getTier() : SuperTier.I;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, SuperTankBlockEntity tank) {
        tank.fillDrum();
        if (level instanceof ServerLevel serverLevel && level.getGameTime() % SSConfig.TRANSFER_INTERVAL.get() == 0) {
            tank.transfer(serverLevel);
        }
        tank.syncCount();
    }

    private void transfer(ServerLevel serverLevel) {
        if (!config.isCapabilitiesEnabled()) {
            return;
        }
        Direction facing = getBlockState().getValue(SuperTankBlock.FACING);
        int rate = SSConfig.SUPER_TANK_TRANSFER_RATE.get();
        for (RelativeSide side : RelativeSide.values()) {
            SideMode mode = config.getSide(side);
            if (mode == SideMode.NONE) {
                continue;
            }
            ResourceHandler<FluidResource> neighbour = neighbour(serverLevel, side, facing);
            if (neighbour == null) {
                continue;
            }
            if (mode.canOutput() && !stored.isEmpty() && amount > 0) {
                ResourceHandlerUtil.move(storage, neighbour, resource -> true, rate, null);
            }
            if (mode.canInput()) {
                ResourceHandlerUtil.move(neighbour, storage, this::accepts, rate, null);
            }
        }
    }

    private @Nullable ResourceHandler<FluidResource> neighbour(ServerLevel serverLevel, RelativeSide side, Direction facing) {
        BlockCapabilityCache<ResourceHandler<FluidResource>, @Nullable Direction> cache = neighbours[side.ordinal()];
        if (cache == null) {
            Direction direction = side.toDirection(facing);
            cache = BlockCapabilityCache.create(Capabilities.Fluid.BLOCK, serverLevel, worldPosition.relative(direction), direction.getOpposite(), () -> !isRemoved(), () -> {});
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

    public FluidResource getStored() {
        return stored;
    }

    @Override
    public long getAmount() {
        return amount;
    }

    @Override
    public long getCapacity() {
        return tier.capacity();
    }

    public ResourceHandler<FluidResource> getStorage() {
        return storage;
    }

    public ItemStacksResourceHandler getIoSlot() {
        return ioSlot;
    }

    public @Nullable ResourceHandler<FluidResource> getFluidHandler(@Nullable Direction side) {
        if (!config.isCapabilitiesEnabled()) {
            return null;
        }
        if (side == null) {
            return storage;
        }
        SideMode mode = config.getSide(RelativeSide.of(getBlockState().getValue(SuperTankBlock.FACING), side));
        return switch (mode) {
            case NONE -> null;
            case INPUT -> inputOnly;
            case OUTPUT -> outputOnly;
            case BOTH -> storage;
        };
    }

    private boolean accepts(FluidResource resource) {
        if (resource.isEmpty()) {
            return false;
        }
        return stored.isEmpty() || stored.equals(resource);
    }

    private void fillDrum() {
        if (stored.isEmpty() || amount == 0 || ioSlot.getResource(0).isEmpty()) {
            return;
        }
        ResourceHandler<FluidResource> drum = ItemAccess.forHandlerIndex(ioSlot, 0).getCapability(Capabilities.Fluid.ITEM);
        if (drum != null) {
            ResourceHandlerUtil.moveFirst(storage, drum, resource -> true, DRUM_FILL_RATE, null);
        }
    }

    @Override
    public void setLocked(boolean locked) {
        if (locked && stored.isEmpty()) {
            return;
        }
        config.setLocked(locked);
        if (!locked && amount == 0) {
            stored = FluidResource.EMPTY;
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
        if (amount == 0) {
            return 0;
        }
        return Mth.clamp(1 + (int) (amount * 14 / tier.capacity()), 1, 15);
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
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (level != null) {
            Containers.dropContents(level, pos, ioSlot.copyToList());
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (!stored.isEmpty()) {
            output.store(FLUID_KEY, FluidResource.CODEC, stored);
        }
        output.putLong(AMOUNT_KEY, amount);
        config.save(output.child(CONFIG_KEY));
        ioSlot.serialize(output.child(IO_KEY));
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        stored = input.read(FLUID_KEY, FluidResource.CODEC).orElse(FluidResource.EMPTY);
        amount = Math.max(0, input.getLongOr(AMOUNT_KEY, 0));
        config.load(input.childOrEmpty(CONFIG_KEY));
        ioSlot.deserialize(input.childOrEmpty(IO_KEY));
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
        SuperTankContents contents = components.get(SSDataComponents.SUPER_TANK_CONTENTS.get());
        if (contents != null) {
            stored = contents.fluid();
            amount = Mth.clamp(contents.amount(), 0, tier.capacity());
        }
        config.fromSettings(components.getOrDefault(SSDataComponents.SUPER_SETTINGS.get(), SuperSettings.DEFAULT));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        if (!stored.isEmpty()) {
            components.set(SSDataComponents.SUPER_TANK_CONTENTS.get(), new SuperTankContents(stored, amount));
        }
        if (!config.isDefault()) {
            components.set(SSDataComponents.SUPER_SETTINGS.get(), config.toSettings());
        }
    }

    private record Snapshot(FluidResource stored, long amount) {
    }

    private class Storage extends SnapshotJournal<Snapshot> implements ResourceHandler<FluidResource> {
        @Override
        public int size() {
            return 1;
        }

        @Override
        public FluidResource getResource(int index) {
            return index == 0 ? stored : FluidResource.EMPTY;
        }

        @Override
        public long getAmountAsLong(int index) {
            return index == 0 ? amount : 0;
        }

        @Override
        public long getCapacityAsLong(int index, FluidResource resource) {
            return index == 0 ? tier.capacity() : 0;
        }

        @Override
        public boolean isValid(int index, FluidResource resource) {
            return index == 0 && accepts(resource);
        }

        @Override
        public int insert(int index, FluidResource resource, int toInsertAmount, TransactionContext transaction) {
            if (index != 0 || toInsertAmount <= 0 || !accepts(resource)) {
                return 0;
            }
            int toInsert = (int) Math.min(toInsertAmount, tier.capacity() - amount);
            boolean voiding = config.isVoidOverflow();
            if (toInsert <= 0 && !voiding) {
                return 0;
            }
            updateSnapshots(transaction);
            if (stored.isEmpty()) {
                stored = resource;
            }
            amount += Math.max(0, toInsert);
            return voiding ? toInsertAmount : toInsert;
        }

        @Override
        public int extract(int index, FluidResource resource, int toExtractAmount, TransactionContext transaction) {
            if (index != 0 || toExtractAmount <= 0 || amount == 0 || stored.isEmpty() || !stored.equals(resource)) {
                return 0;
            }
            int extracted = (int) Math.min(toExtractAmount, amount);
            updateSnapshots(transaction);
            amount -= extracted;
            if (amount == 0 && !config.isLocked()) {
                stored = FluidResource.EMPTY;
            }
            return extracted;
        }

        @Override
        protected Snapshot createSnapshot() {
            return new Snapshot(stored, amount);
        }

        @Override
        protected void revertToSnapshot(Snapshot snapshot) {
            stored = snapshot.stored();
            amount = snapshot.amount();
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

    private class Restricted implements ResourceHandler<FluidResource> {
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
        public FluidResource getResource(int index) {
            return storage.getResource(index);
        }

        @Override
        public long getAmountAsLong(int index) {
            return storage.getAmountAsLong(index);
        }

        @Override
        public long getCapacityAsLong(int index, FluidResource resource) {
            return storage.getCapacityAsLong(index, resource);
        }

        @Override
        public boolean isValid(int index, FluidResource resource) {
            return canInsert && storage.isValid(index, resource);
        }

        @Override
        public int insert(int index, FluidResource resource, int toInsertAmount, TransactionContext transaction) {
            return canInsert ? storage.insert(index, resource, toInsertAmount, transaction) : 0;
        }

        @Override
        public int extract(int index, FluidResource resource, int toExtractAmount, TransactionContext transaction) {
            return canExtract ? storage.extract(index, resource, toExtractAmount, transaction) : 0;
        }
    }

    private class IoSlot extends ItemStacksResourceHandler {
        IoSlot() {
            super(1);
        }

        @Override
        public boolean isValid(int index, ItemResource resource) {
            return resource.getItem() instanceof DrumBlockItem;
        }

        @Override
        protected int getCapacity(int index, ItemResource resource) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int index, ItemStack previousContents) {
            setChanged();
        }
    }
}