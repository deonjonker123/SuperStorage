package com.misterd.superstorage.gui.custom;

import com.misterd.superstorage.component.CollectorSettings;
import com.misterd.superstorage.component.SSDataComponents;
import com.misterd.superstorage.config.SSConfig;
import com.misterd.superstorage.gui.SSMenuTypes;
import com.misterd.superstorage.util.UpgradeType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

public class CollectorUpgradeMenu extends UpgradeSettingsMenu {
    public static final int BUTTON_RESET = 30;
    public static final int[] STEPS = {-1, 1, -5, 5};

    private static final int[][] FILTER_SLOTS = {{152, 22}};
    private static final int PLAYER_INV_Y = 77;

    private final int[] synced = CollectorSettings.DEFAULT_VALUES.stream().mapToInt(Integer::intValue).toArray();

    public CollectorUpgradeMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf buf) {
        this(containerId, inventory, buf.readBlockPos());
    }

    public CollectorUpgradeMenu(int containerId, Inventory inventory, BlockPos pos) {
        super(SSMenuTypes.COLLECTOR_UPGRADE_MENU.get(), containerId, inventory, pos, UpgradeType.COLLECTOR, FILTER_SLOTS, PLAYER_INV_Y);
        for (int i = 0; i < CollectorSettings.VALUE_COUNT; i++) {
            int index = i;
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return CollectorSettings.of(upgrade()).value(index);
                }

                @Override
                public void set(int value) {
                    synced[index] = value;
                }
            });
        }
    }

    public static int buttonId(int index, int step) {
        return index * STEPS.length + step;
    }

    public int getValue(int index) {
        return crate != null ? CollectorSettings.of(upgrade()).value(index) : synced[index];
    }

    public AABB area() {
        return CollectorSettings.area(pos,
                getValue(CollectorSettings.SIZE_X), getValue(CollectorSettings.SIZE_Y), getValue(CollectorSettings.SIZE_Z),
                getValue(CollectorSettings.OFFSET_X), getValue(CollectorSettings.OFFSET_Y), getValue(CollectorSettings.OFFSET_Z));
    }

    @Override
    protected NonNullList<ItemStack> readFilters(ItemStack upgrade) {
        return NonNullList.of(ItemStack.EMPTY, CollectorSettings.of(upgrade).filter());
    }

    @Override
    protected void writeFilters(ItemStack upgrade, NonNullList<ItemStack> filters) {
        upgrade.set(SSDataComponents.COLLECTOR_SETTINGS.get(), CollectorSettings.of(upgrade).withFilter(filters.get(0)));
    }

    @Override
    protected boolean handleButton(int buttonId) {
        if (buttonId == BUTTON_RESET) {
            modify(upgrade -> upgrade.set(SSDataComponents.COLLECTOR_SETTINGS.get(), CollectorSettings.of(upgrade).reset()));
            return true;
        }
        int index = buttonId / STEPS.length;
        if (buttonId < 0 || index >= CollectorSettings.VALUE_COUNT) {
            return false;
        }
        int step = STEPS[buttonId % STEPS.length];
        boolean size = CollectorSettings.isSize(index);
        int max = size ? SSConfig.COLLECTOR_MAX_SIZE.get() : SSConfig.COLLECTOR_MAX_OFFSET.get();
        int min = size ? 1 : -max;
        modify(upgrade -> {
            CollectorSettings settings = CollectorSettings.of(upgrade);
            upgrade.set(SSDataComponents.COLLECTOR_SETTINGS.get(), settings.withValue(index, Mth.clamp(settings.value(index) + step, min, max)));
        });
        return true;
    }
}