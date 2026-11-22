package com.misterd.superstorage.gui.custom;

import com.misterd.superstorage.component.CompactingSettings;
import com.misterd.superstorage.component.SSDataComponents;
import com.misterd.superstorage.gui.SSMenuTypes;
import com.misterd.superstorage.util.UpgradeType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;

public class CompactingUpgradeMenu extends UpgradeSettingsMenu {
    public static final int BUTTON_REVERSIBLE = 0;
    public static final int BUTTON_SIZE = 1;

    private static final int[][] FILTER_SLOTS = {{8, 8}};
    private static final int PLAYER_INV_Y = 41;

    private int syncedFlags;

    public CompactingUpgradeMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf buf) {
        this(containerId, inventory, buf.readBlockPos());
    }

    public CompactingUpgradeMenu(int containerId, Inventory inventory, BlockPos pos) {
        super(SSMenuTypes.COMPACTING_UPGRADE_MENU.get(), containerId, inventory, pos, UpgradeType.COMPACTING, FILTER_SLOTS, PLAYER_INV_Y);
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return CompactingSettings.of(upgrade()).flags();
            }

            @Override
            public void set(int value) {
                syncedFlags = value;
            }
        });
    }

    public boolean hasFlag(int flag) {
        int flags = crate != null ? CompactingSettings.of(upgrade()).flags() : syncedFlags;
        return (flags & flag) != 0;
    }

    @Override
    protected NonNullList<ItemStack> readFilters(ItemStack upgrade) {
        return NonNullList.of(ItemStack.EMPTY, CompactingSettings.of(upgrade).filter());
    }

    @Override
    protected void writeFilters(ItemStack upgrade, NonNullList<ItemStack> filters) {
        upgrade.set(SSDataComponents.COMPACTING_SETTINGS.get(), CompactingSettings.of(upgrade).withFilter(filters.get(0)));
    }

    @Override
    protected boolean handleButton(int buttonId) {
        int flag = switch (buttonId) {
            case BUTTON_REVERSIBLE -> CompactingSettings.REVERSIBLE_ONLY;
            case BUTTON_SIZE -> CompactingSettings.THREE_BY_THREE;
            default -> 0;
        };
        if (flag == 0) {
            return false;
        }
        modify(upgrade -> {
            CompactingSettings settings = CompactingSettings.of(upgrade);
            upgrade.set(SSDataComponents.COMPACTING_SETTINGS.get(), settings.withFlags(settings.flags() ^ flag));
        });
        return true;
    }
}