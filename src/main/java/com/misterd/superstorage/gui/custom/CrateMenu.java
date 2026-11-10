package com.misterd.superstorage.gui.custom;

import com.misterd.superstorage.blockentity.custom.CrateBlockEntity;
import com.misterd.superstorage.gui.SSMenuTypes;
import com.misterd.superstorage.util.CrateTier;
import com.misterd.superstorage.util.StorageGuard;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

public class CrateMenu extends AbstractContainerMenu {
    private final CrateTier tier;
    private final ItemStacksResourceHandler itemHandler;
    private final ContainerLevelAccess access;
    private final int crateSlotCount;
    private int scrollRow;

    public CrateMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf buf) {
        this(containerId, inventory, CrateTier.byId(buf.readVarInt()));
    }

    private CrateMenu(int containerId, Inventory inventory, CrateTier tier) {
        this(containerId, inventory, tier, new ItemStacksResourceHandler(tier.slotCount()) {
            @Override
            public boolean isValid(int index, ItemResource resource) {
                return StorageGuard.isAllowed(resource);
            }
        }, ContainerLevelAccess.NULL);
    }

    public CrateMenu(int containerId, Inventory inventory, CrateBlockEntity crate) {
        this(containerId, inventory, crate.getTier(), crate.getItemHandler(), ContainerLevelAccess.create(crate.getLevel(), crate.getBlockPos()));
    }

    private CrateMenu(int containerId, Inventory inventory, CrateTier tier, ItemStacksResourceHandler itemHandler, ContainerLevelAccess access) {
        super(SSMenuTypes.CRATE_MENU.get(), containerId);
        this.tier = tier;
        this.itemHandler = itemHandler;
        this.access = access;
        this.crateSlotCount = tier.columns() * tier.visibleRows();
        for (int row = 0; row < tier.visibleRows(); row++) {
            for (int col = 0; col < tier.columns(); col++) {
                addSlot(new CrateSlot(row * tier.columns() + col,
                        CrateTier.SLOT_X + col * CrateTier.SLOT_SIZE,
                        CrateTier.SLOT_Y + row * CrateTier.SLOT_SIZE));
            }
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9 + 9,
                        tier.playerInvX() + col * CrateTier.SLOT_SIZE,
                        tier.playerInvY() + row * CrateTier.SLOT_SIZE));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inventory, col, tier.playerInvX() + col * CrateTier.SLOT_SIZE, tier.hotbarY()));
        }
    }

    public CrateTier getTier() {
        return tier;
    }

    public int getScrollRow() {
        return scrollRow;
    }

    public void setScrollRow(int row) {
        this.scrollRow = Mth.clamp(row, 0, tier.hiddenRows());
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        if (buttonId < 0 || buttonId > tier.hiddenRows()) {
            return false;
        }
        setScrollRow(buttonId);
        if (player instanceof ServerPlayer) {
            sendAllDataToRemote();
        }
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (index < crateSlotCount) {
            if (!moveItemStackTo(stack, crateSlotCount, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            int inserted = ResourceHandlerUtil.insertStacking(itemHandler, ItemResource.of(stack), stack.getCount(), null);
            if (inserted == 0) {
                return ItemStack.EMPTY;
            }
            stack.shrink(inserted);
        }
        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return original;
    }

    @Override
    public boolean stillValid(Player player) {
        return access.evaluate((level, pos) -> level.getBlockEntity(pos) instanceof CrateBlockEntity
                && player.isWithinBlockInteractionRange(pos, 4.0), true);
    }

    private class CrateSlot extends ResourceHandlerSlot {
        private final int baseIndex;

        CrateSlot(int baseIndex, int x, int y) {
            super(itemHandler, itemHandler::set, baseIndex, x, y);
            this.baseIndex = baseIndex;
        }

        @Override
        public int getSlotIndex() {
            return baseIndex + scrollRow * tier.columns();
        }

        @Override
        public int getContainerSlot() {
            return getSlotIndex();
        }
    }
}