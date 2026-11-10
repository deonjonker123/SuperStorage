package com.misterd.superstorage.gui.custom;

import com.misterd.superstorage.blockentity.custom.SuperChestBlockEntity;
import com.misterd.superstorage.gui.SSMenuTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.Nullable;

public class SuperChestMenu extends SuperMenu {
    public static final int IN_X = 86;
    public static final int IN_Y = 10;
    public static final int OUT_X = 86;
    public static final int OUT_Y = 29;

    private static final int IN_SLOT = 0;
    private static final int OUT_SLOT = 1;

    private final SimpleContainer input = new SimpleContainer(1);

    public SuperChestMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf buf) {
        super(SSMenuTypes.SUPER_CHEST_MENU.get(), containerId, inventory, buf.readBlockPos());
        addSlots(inventory);
    }

    public SuperChestMenu(int containerId, Inventory inventory, SuperChestBlockEntity chest) {
        super(SSMenuTypes.SUPER_CHEST_MENU.get(), containerId, inventory, chest.getBlockPos());
        addSlots(inventory);
    }

    private void addSlots(Inventory inventory) {
        addSlot(new InputSlot());
        addSlot(new OutputSlot());
        addPlayerInventory(inventory);
    }

    private @Nullable SuperChestBlockEntity chest() {
        return blockEntity instanceof SuperChestBlockEntity chest ? chest : null;
    }

    public ItemResource getStored() {
        SuperChestBlockEntity chest = chest();
        return chest == null ? ItemResource.EMPTY : chest.getStored();
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        if (buttonId != BUTTON_EXTRACT) {
            return super.clickMenuButton(player, buttonId);
        }
        SuperChestBlockEntity chest = chest();
        if (chest == null || chest.getStored().isEmpty()) {
            return false;
        }
        ItemStack extracted = chest.extract(chest.getStored().getMaxStackSize());
        player.getInventory().add(extracted);
        if (!extracted.isEmpty()) {
            chest.insert(extracted);
        }
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        SuperChestBlockEntity chest = chest();
        Slot slot = slots.get(index);
        if (chest == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        if (index == IN_SLOT) {
            ItemStack stack = slot.getItem();
            if (moveItemStackTo(stack, playerSlotStart, slots.size(), true)) {
                slot.setChanged();
            }
            return ItemStack.EMPTY;
        }
        if (index == OUT_SLOT) {
            ItemStack moving = slot.getItem().copy();
            int before = moving.getCount();
            moveItemStackTo(moving, playerSlotStart, slots.size(), true);
            int moved = before - moving.getCount();
            if (moved > 0 && server) {
                chest.extract(moved);
            }
            return ItemStack.EMPTY;
        }
        if (server) {
            ItemStack stack = slot.getItem();
            int inserted = chest.insert(stack);
            if (inserted > 0) {
                stack.shrink(inserted);
                slot.setChanged();
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        clearContainer(player, input);
    }

    private class InputSlot extends Slot {
        InputSlot() {
            super(input, 0, IN_X, IN_Y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            SuperChestBlockEntity chest = chest();
            return chest != null && chest.accepts(stack);
        }

        @Override
        public void setChanged() {
            super.setChanged();
            SuperChestBlockEntity chest = chest();
            ItemStack stack = getItem();
            if (server && chest != null && !stack.isEmpty()) {
                stack.shrink(chest.insert(stack));
                container.setChanged();
            }
        }
    }

    private class OutputSlot extends Slot {
        OutputSlot() {
            super(new SimpleContainer(1), 0, OUT_X, OUT_Y);
        }

        @Override
        public ItemStack getItem() {
            ItemResource stored = getStored();
            long amount = getAmount();
            if (stored.isEmpty() || amount <= 0) {
                return ItemStack.EMPTY;
            }
            return stored.toStack((int) Math.min(amount, stored.getMaxStackSize()));
        }

        @Override
        public boolean hasItem() {
            return !getItem().isEmpty();
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public void set(ItemStack stack) {
        }

        @Override
        public void setChanged() {
        }

        @Override
        public ItemStack remove(int amount) {
            ItemStack shown = getItem();
            int count = Math.min(amount, shown.getCount());
            if (count <= 0) {
                return ItemStack.EMPTY;
            }
            SuperChestBlockEntity chest = chest();
            if (server && chest != null) {
                return chest.extract(count);
            }
            return shown.copyWithCount(count);
        }
    }
}