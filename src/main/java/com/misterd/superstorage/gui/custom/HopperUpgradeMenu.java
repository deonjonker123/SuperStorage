package com.misterd.superstorage.gui.custom;

import com.misterd.superstorage.component.HopperSettings;
import com.misterd.superstorage.component.SSDataComponents;
import com.misterd.superstorage.gui.SSMenuTypes;
import com.misterd.superstorage.util.SideMode;
import com.misterd.superstorage.util.UpgradeType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;

public class HopperUpgradeMenu extends UpgradeSettingsMenu {
    private static final int[][] FILTER_SLOTS = {{8, 46}, {152, 46}};
    private static final int PLAYER_INV_Y = 79;

    private int syncedSides;

    public HopperUpgradeMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf buf) {
        this(containerId, inventory, buf.readBlockPos());
    }

    public HopperUpgradeMenu(int containerId, Inventory inventory, BlockPos pos) {
        super(SSMenuTypes.HOPPER_UPGRADE_MENU.get(), containerId, inventory, pos, UpgradeType.HOPPER, FILTER_SLOTS, PLAYER_INV_Y);
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

    public SideMode getSide(Direction direction) {
        int bits = crate != null ? settings().encodeSides() : syncedSides;
        return SideMode.values()[(bits >> (direction.ordinal() * 2)) & 3];
    }

    private HopperSettings settings() {
        return HopperSettings.of(upgrade());
    }

    @Override
    protected NonNullList<ItemStack> readFilters(ItemStack upgrade) {
        return HopperSettings.of(upgrade).filterList();
    }

    @Override
    protected void writeFilters(ItemStack upgrade, NonNullList<ItemStack> filters) {
        upgrade.set(SSDataComponents.HOPPER_SETTINGS.get(), HopperSettings.of(upgrade).withFilters(filters));
    }

    @Override
    protected boolean handleButton(int buttonId) {
        if (buttonId < 0 || buttonId >= Direction.values().length) {
            return false;
        }
        Direction direction = Direction.values()[buttonId];
        modify(upgrade -> {
            HopperSettings settings = HopperSettings.of(upgrade);
            upgrade.set(SSDataComponents.HOPPER_SETTINGS.get(), settings.withSide(direction, settings.side(direction).next()));
        });
        return true;
    }
}