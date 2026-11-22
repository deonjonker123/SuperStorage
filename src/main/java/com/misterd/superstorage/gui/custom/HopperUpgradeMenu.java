package com.misterd.superstorage.gui.custom;

import com.misterd.superstorage.block.custom.CrateBlock;
import com.misterd.superstorage.blockentity.custom.CrateBlockEntity;
import com.misterd.superstorage.component.HopperSettings;
import com.misterd.superstorage.component.SSDataComponents;
import com.misterd.superstorage.gui.SSMenuTypes;
import com.misterd.superstorage.item.custom.FilterItem;
import com.misterd.superstorage.util.SideMode;
import com.misterd.superstorage.util.UpgradeType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public class HopperUpgradeMenu extends AbstractContainerMenu {
    public static final int BUTTON_BACK = 10;

    private static final int INPUT_X = 8;
    private static final int OUTPUT_X = 152;
    private static final int FILTER_Y = 46;
    private static final int PLAYER_INV_X = 8;
    private static final int PLAYER_INV_Y = 79;
    private static final int HOTBAR_Y = 138;
    private static final int SLOT_SIZE = 18;

    private final Level level;
    private final BlockPos pos;
    private final @Nullable CrateBlockEntity crate;
    private final SimpleContainer filters = new SimpleContainer(HopperSettings.FILTER_COUNT) {
        @Override
        public void setChanged() {
            super.setChanged();
            saveFilters();
        }
    };
    private boolean loaded;
    private int syncedSides;

    public HopperUpgradeMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf buf) {
        this(containerId, inventory, buf.readBlockPos());
    }

    public HopperUpgradeMenu(int containerId, Inventory inventory, BlockPos pos) {
        super(SSMenuTypes.HOPPER_UPGRADE_MENU.get(), containerId);
        this.level = inventory.player.level();
        this.pos = pos;
        this.crate = !level.isClientSide() && level.getBlockEntity(pos) instanceof CrateBlockEntity be ? be : null;
        HopperSettings settings = settings();
        for (int i = 0; i < HopperSettings.FILTER_COUNT; i++) {
            filters.setItem(i, settings.filter(i));
        }
        this.loaded = true;
        addSlot(new FilterSlot(HopperSettings.INPUT_FILTER, INPUT_X, FILTER_Y));
        addSlot(new FilterSlot(HopperSettings.OUTPUT_FILTER, OUTPUT_X, FILTER_Y));
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9 + 9, PLAYER_INV_X + col * SLOT_SIZE, PLAYER_INV_Y + row * SLOT_SIZE));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inventory, col, PLAYER_INV_X + col * SLOT_SIZE, HOTBAR_Y));
        }
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return settings().encodeSides();
            }

            @Override
            public void set(int value) {
                syncedSides = value;
            }
        });
    }

    public static void open(Player player, CrateBlockEntity crate) {
        BlockPos pos = crate.getBlockPos();
        player.openMenu(new SimpleMenuProvider((containerId, inventory, p) -> new HopperUpgradeMenu(containerId, inventory, pos), crate.getUpgrades().get(UpgradeType.HOPPER).getHoverName()),
                buf -> buf.writeBlockPos(pos));
    }

    public BlockPos getPos() {
        return pos;
    }

    public SideMode getSide(Direction direction) {
        int bits = crate != null ? settings().encodeSides() : syncedSides;
        return SideMode.values()[(bits >> (direction.ordinal() * 2)) & 3];
    }

    private HopperSettings settings() {
        return crate == null ? HopperSettings.DEFAULT : HopperSettings.of(crate.getUpgrades().get(UpgradeType.HOPPER));
    }

    private void update(HopperSettings settings) {
        if (crate == null) {
            return;
        }
        ItemStack upgrade = crate.getUpgrades().get(UpgradeType.HOPPER);
        if (upgrade.isEmpty()) {
            return;
        }
        upgrade.set(SSDataComponents.HOPPER_SETTINGS.get(), settings);
        crate.getUpgrades().put(UpgradeType.HOPPER, upgrade);
    }

    private void saveFilters() {
        if (!loaded || crate == null) {
            return;
        }
        NonNullList<ItemStack> list = NonNullList.withSize(HopperSettings.FILTER_COUNT, ItemStack.EMPTY);
        for (int i = 0; i < HopperSettings.FILTER_COUNT; i++) {
            list.set(i, filters.getItem(i).copy());
        }
        update(settings().withFilters(list));
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
        if (buttonId < 0 || buttonId >= Direction.values().length) {
            return false;
        }
        Direction direction = Direction.values()[buttonId];
        HopperSettings settings = settings();
        update(settings.withSide(direction, settings.side(direction).next()));
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
        if (index < HopperSettings.FILTER_COUNT) {
            if (!moveItemStackTo(stack, HopperSettings.FILTER_COUNT, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (!(stack.getItem() instanceof FilterItem) || !moveItemStackTo(stack, 0, HopperSettings.FILTER_COUNT, false)) {
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
                && crate.getUpgrades().has(UpgradeType.HOPPER)
                && player.isWithinBlockInteractionRange(pos, 4.0);
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