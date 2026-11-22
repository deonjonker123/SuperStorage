package com.misterd.superstorage.gui.custom;

import com.misterd.superstorage.component.FilterSettings;
import com.misterd.superstorage.component.SSDataComponents;
import com.misterd.superstorage.gui.SSMenuTypes;
import com.misterd.superstorage.item.custom.FilterItem;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class FilterMenu extends AbstractContainerMenu {
    public static final int BUTTON_CLEAR = 4;

    private static final int SLOT_X = 8;
    private static final int SLOT_Y = 30;
    private static final int PLAYER_INV_X = 8;
    private static final int PLAYER_INV_Y = 99;
    private static final int HOTBAR_Y = 158;
    private static final int SLOT_SIZE = 18;

    private final Player player;
    private final InteractionHand hand;
    private final SimpleContainer ghosts = new SimpleContainer(FilterSettings.SIZE);
    private final int lockedSlot;
    private int flags;

    public FilterMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf buf) {
        this(containerId, inventory, buf.readEnum(InteractionHand.class));
    }

    public FilterMenu(int containerId, Inventory inventory, InteractionHand hand) {
        super(SSMenuTypes.FILTER_MENU.get(), containerId);
        this.player = inventory.player;
        this.hand = hand;
        this.lockedSlot = hand == InteractionHand.MAIN_HAND ? inventory.getSelectedSlot() : -1;
        NonNullList<ItemStack> entries = FilterSettings.of(filter()).list();
        for (int i = 0; i < FilterSettings.SIZE; i++) {
            ghosts.setItem(i, entries.get(i));
            addSlot(new GhostSlot(i, SLOT_X + (i % 9) * SLOT_SIZE, SLOT_Y + (i / 9) * SLOT_SIZE));
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9 + 9, PLAYER_INV_X + col * SLOT_SIZE, PLAYER_INV_Y + row * SLOT_SIZE));
            }
        }
        for (int col = 0; col < 9; col++) {
            int x = PLAYER_INV_X + col * SLOT_SIZE;
            addSlot(col == lockedSlot ? new LockedSlot(inventory, col, x, HOTBAR_Y) : new Slot(inventory, col, x, HOTBAR_Y));
        }
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return FilterSettings.of(filter()).flags();
            }

            @Override
            public void set(int value) {
                flags = value;
            }
        });
    }

    private ItemStack filter() {
        return player.getItemInHand(hand);
    }

    public boolean hasFlag(int flag) {
        int current = player.level().isClientSide() ? flags : FilterSettings.of(filter()).flags();
        return (current & flag) != 0;
    }

    @Override
    public void clicked(int slotIndex, int buttonNum, ContainerInput containerInput, Player player) {
        if (slotIndex >= 0 && slotIndex < FilterSettings.SIZE) {
            if (containerInput == ContainerInput.PICKUP || containerInput == ContainerInput.QUICK_MOVE) {
                ItemStack carried = getCarried();
                ghosts.setItem(slotIndex, carried.isEmpty() ? ItemStack.EMPTY : carried.copyWithCount(1));
                save();
            }
            return;
        }
        if (containerInput == ContainerInput.SWAP && buttonNum == lockedSlot) {
            return;
        }
        super.clicked(slotIndex, buttonNum, containerInput, player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index < FilterSettings.SIZE) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slots.get(index).getItem();
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        int empty = -1;
        for (int i = 0; i < FilterSettings.SIZE; i++) {
            ItemStack entry = ghosts.getItem(i);
            if (entry.isEmpty()) {
                if (empty < 0) {
                    empty = i;
                }
            } else if (ItemStack.isSameItemSameComponents(entry, stack)) {
                return ItemStack.EMPTY;
            }
        }
        if (empty >= 0) {
            ghosts.setItem(empty, stack.copyWithCount(1));
            save();
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        if (buttonId == BUTTON_CLEAR) {
            ghosts.clearContent();
            filter().remove(SSDataComponents.FILTER.get());
            return true;
        }
        if (buttonId < 0 || buttonId > 3) {
            return false;
        }
        FilterSettings settings = FilterSettings.of(filter());
        update(settings.withFlags(settings.flags() ^ (1 << buttonId)));
        return true;
    }

    private void save() {
        if (player.level().isClientSide()) {
            return;
        }
        NonNullList<ItemStack> entries = NonNullList.withSize(FilterSettings.SIZE, ItemStack.EMPTY);
        for (int i = 0; i < FilterSettings.SIZE; i++) {
            entries.set(i, ghosts.getItem(i).copy());
        }
        update(FilterSettings.of(filter()).withItems(entries));
    }

    private void update(FilterSettings settings) {
        if (settings.equals(FilterSettings.DEFAULT)) {
            filter().remove(SSDataComponents.FILTER.get());
        } else {
            filter().set(SSDataComponents.FILTER.get(), settings);
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return player.getItemInHand(hand).getItem() instanceof FilterItem;
    }

    private class GhostSlot extends Slot {
        GhostSlot(int index, int x, int y) {
            super(ghosts, index, x, y);
        }

        @Override
        public boolean mayPickup(Player player) {
            return false;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }
    }

    private static class LockedSlot extends Slot {
        LockedSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean mayPickup(Player player) {
            return false;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }
    }
}