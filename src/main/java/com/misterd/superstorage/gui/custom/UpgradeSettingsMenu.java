package com.misterd.superstorage.gui.custom;

import com.misterd.superstorage.block.custom.CrateBlock;
import com.misterd.superstorage.blockentity.custom.CrateBlockEntity;
import com.misterd.superstorage.item.custom.FilterItem;
import com.misterd.superstorage.util.UpgradeType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public abstract class UpgradeSettingsMenu extends AbstractContainerMenu {
    public static final int BUTTON_BACK = 99;

    private static final int PLAYER_INV_X = 8;
    private static final int HOTBAR_OFFSET = 59;
    private static final int SLOT_SIZE = 18;

    protected final Level level;
    protected final BlockPos pos;
    protected final @Nullable CrateBlockEntity crate;
    private final UpgradeType type;
    private final int filterCount;
    private final int playerInvY;
    private final SimpleContainer filters;
    private boolean loaded;

    protected UpgradeSettingsMenu(MenuType<?> menuType, int containerId, Inventory inventory, BlockPos pos, UpgradeType type, int[][] filterSlots, int playerInvY) {
        super(menuType, containerId);
        this.level = inventory.player.level();
        this.pos = pos;
        this.type = type;
        this.filterCount = filterSlots.length;
        this.playerInvY = playerInvY;
        this.crate = !level.isClientSide() && level.getBlockEntity(pos) instanceof CrateBlockEntity be ? be : null;
        this.filters = new SimpleContainer(filterCount) {
            @Override
            public void setChanged() {
                super.setChanged();
                saveFilters();
            }
        };
        NonNullList<ItemStack> stored = readFilters(upgrade());
        for (int i = 0; i < filterCount; i++) {
            filters.setItem(i, stored.get(i));
        }
        this.loaded = true;
        for (int i = 0; i < filterCount; i++) {
            addSlot(new FilterSlot(i, filterSlots[i][0], filterSlots[i][1]));
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9 + 9, PLAYER_INV_X + col * SLOT_SIZE, playerInvY + row * SLOT_SIZE));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inventory, col, PLAYER_INV_X + col * SLOT_SIZE, playerInvY + HOTBAR_OFFSET));
        }
    }

    public static void open(Player player, CrateBlockEntity crate, UpgradeType type, Factory factory) {
        BlockPos pos = crate.getBlockPos();
        player.openMenu(new SimpleMenuProvider((containerId, inventory, p) -> factory.create(containerId, inventory, pos), crate.getUpgrades().get(type).getHoverName()),
                buf -> buf.writeBlockPos(pos));
    }

    public BlockPos getPos() {
        return pos;
    }

    public int getPlayerInvY() {
        return playerInvY;
    }

    public int getFilterCount() {
        return filterCount;
    }

    protected ItemStack upgrade() {
        return crate == null ? ItemStack.EMPTY : crate.getUpgrades().get(type);
    }

    protected void modify(Consumer<ItemStack> change) {
        if (crate == null) {
            return;
        }
        ItemStack upgrade = upgrade();
        if (upgrade.isEmpty()) {
            return;
        }
        change.accept(upgrade);
        crate.getUpgrades().put(type, upgrade);
    }

    protected abstract NonNullList<ItemStack> readFilters(ItemStack upgrade);

    protected abstract void writeFilters(ItemStack upgrade, NonNullList<ItemStack> filters);

    protected abstract boolean handleButton(int buttonId);

    private void saveFilters() {
        if (!loaded || crate == null) {
            return;
        }
        NonNullList<ItemStack> list = NonNullList.withSize(filterCount, ItemStack.EMPTY);
        for (int i = 0; i < filterCount; i++) {
            list.set(i, filters.getItem(i).copy());
        }
        modify(upgrade -> writeFilters(upgrade, list));
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        if (crate == null) {
            return false;
        }
        if (buttonId == BUTTON_BACK) {
            if (stillValid(player)) {
                CrateBlock.openMenu(player, crate);
            } else {
                player.closeContainer();
            }
            return true;
        }
        return handleButton(buttonId);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (index < filterCount) {
            if (!moveItemStackTo(stack, filterCount, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (!(stack.getItem() instanceof FilterItem) || !moveItemStackTo(stack, 0, filterCount, false)) {
            return ItemStack.EMPTY;
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
        if (crate == null) {
            return true;
        }
        return level.getBlockEntity(pos) == crate
                && crate.getUpgrades().has(type)
                && player.isWithinBlockInteractionRange(pos, 4.0);
    }

    @FunctionalInterface
    public interface Factory {
        UpgradeSettingsMenu create(int containerId, Inventory inventory, BlockPos pos);
    }

    private class FilterSlot extends Slot {
        FilterSlot(int index, int x, int y) {
            super(filters, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.getItem() instanceof FilterItem;
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }
}