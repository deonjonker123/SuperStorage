package com.misterd.superstorage.blockentity.custom;

import com.misterd.superstorage.block.custom.CrateBlock;
import com.misterd.superstorage.util.CrateTier;
import com.misterd.superstorage.blockentity.SSBlockEntities;
import com.misterd.superstorage.util.StorageGuard;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

public class CrateBlockEntity extends BlockEntity {
    private static final String INVENTORY_KEY = "inventory";

    private final CrateTier tier;
    private final CrateItemHandler itemHandler;

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

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        itemHandler.serialize(output.child(INVENTORY_KEY));
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        itemHandler.deserialize(input.childOrEmpty(INVENTORY_KEY));
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (level != null) {
            Containers.dropContents(level, pos, itemHandler.copyToList());
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