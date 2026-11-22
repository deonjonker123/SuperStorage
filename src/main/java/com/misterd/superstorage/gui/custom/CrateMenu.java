package com.misterd.superstorage.gui.custom;

import com.misterd.superstorage.blockentity.custom.CrateBlockEntity;
import com.misterd.superstorage.gui.SSMenuTypes;
import com.misterd.superstorage.item.custom.UpgradeItem;
import com.misterd.superstorage.util.CrateTier;
import com.misterd.superstorage.util.CrateUpgrades;
import com.misterd.superstorage.util.StorageGuard;
import com.misterd.superstorage.util.UpgradeType;
import net.minecraft.network.RegistryFriendlyByteBuf;
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
    public static final int UPGRADE_X = -20;
    public static final int UPGRADE_Y = 12;

    private static final int PLAYER_SLOT_COUNT = 36;
    private static final int HOTBAR_OFFSET = 59;

    private final CrateTier tier;
    private final ItemStacksResourceHandler itemHandler;
    private final CrateUpgrades upgrades;
    private final ContainerLevelAccess access;
    private final int crateSlotCount;
    private final int upgradeSlotStart;
    private int visibleRows;
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
        }, new CrateUpgrades(() -> {}), ContainerLevelAccess.NULL);
    }

    public CrateMenu(int containerId, Inventory inventory, CrateBlockEntity crate) {
        this(containerId, inventory, crate.getTier(), crate.getItemHandler(), crate.getUpgrades(), ContainerLevelAccess.create(crate.getLevel(), crate.getBlockPos()));
    }

    private CrateMenu(int containerId, Inventory inventory, CrateTier tier, ItemStacksResourceHandler itemHandler, CrateUpgrades upgrades, ContainerLevelAccess access) {
        super(SSMenuTypes.CRATE_MENU.get(), containerId);
        this.tier = tier;
        this.itemHandler = itemHandler;
        this.upgrades = upgrades;
        this.access = access;
        this.crateSlotCount = tier.slotCount();
        this.visibleRows = tier.rows();
        for (int index = 0; index < crateSlotCount; index++) {
            addSlot(new CrateSlot(index));
        }
        for (int index = 9; index < PLAYER_SLOT_COUNT; index++) {
            addSlot(new Slot(inventory, index, 0, 0));
        }
        for (int index = 0; index < 9; index++) {
            addSlot(new Slot(inventory, index, 0, 0));
        }
        this.upgradeSlotStart = slots.size();
        for (UpgradeType type : UpgradeType.values()) {
            addSlot(new ResourceHandlerSlot(upgrades, upgrades::set, type.ordinal(), UPGRADE_X, UPGRADE_Y + type.ordinal() * CrateTier.SLOT_SIZE));
        }
    }

    public CrateTier getTier() {
        return tier;
    }

    public int getScrollRow() {
        return scrollRow;
    }

    public int getHiddenRows() {
        return tier.rows() - visibleRows;
    }

    public ItemStack getUpgrade(UpgradeType type) {
        return slots.get(upgradeSlotStart + type.ordinal()).getItem();
    }

    public void setLayout(int visibleRows, int playerInvX, int playerInvY) {
        this.visibleRows = Mth.clamp(visibleRows, 1, tier.rows());
        for (int i = 0; i < PLAYER_SLOT_COUNT; i++) {
            Slot slot = slots.get(crateSlotCount + i);
            slot.x = playerInvX + (i % 9) * CrateTier.SLOT_SIZE;
            slot.y = i < 27 ? playerInvY + (i / 9) * CrateTier.SLOT_SIZE : playerInvY + HOTBAR_OFFSET;
        }
        setScrollRow(scrollRow);
    }

    public void setScrollRow(int row) {
        this.scrollRow = Mth.clamp(row, 0, getHiddenRows());
        for (int index = 0; index < crateSlotCount; index++) {
            ((CrateSlot) slots.get(index)).updatePosition();
        }
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        if (buttonId < 0 || buttonId >= UpgradeType.values().length) {
            return false;
        }
        UpgradeType type = UpgradeType.values()[buttonId];
        return access.evaluate((level, pos) -> {
            if (!(level.getBlockEntity(pos) instanceof CrateBlockEntity crate) || !crate.getUpgrades().has(type)) {
                return false;
            }
            return switch (type) {
                case HOPPER -> {
                    HopperUpgradeMenu.open(player, crate);
                    yield true;
                }
                default -> false;
            };
        }, false);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        int playerEnd = crateSlotCount + PLAYER_SLOT_COUNT;
        if (index < crateSlotCount || index >= playerEnd) {
            if (!moveItemStackTo(stack, crateSlotCount, playerEnd, true)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveToUpgradeSlot(stack)) {
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

    private boolean moveToUpgradeSlot(ItemStack stack) {
        if (!(stack.getItem() instanceof UpgradeItem upgrade)) {
            return false;
        }
        int target = upgradeSlotStart + upgrade.getType().ordinal();
        return moveItemStackTo(stack, target, target + 1, false);
    }

    @Override
    public boolean stillValid(Player player) {
        return access.evaluate((level, pos) -> level.getBlockEntity(pos) instanceof CrateBlockEntity crate
                && crate.getItemHandler() == itemHandler
                && player.isWithinBlockInteractionRange(pos, 4.0), true);
    }

    private class CrateSlot extends ResourceHandlerSlot {
        private final int row;

        CrateSlot(int index) {
            super(itemHandler, itemHandler::set, index, CrateTier.SLOT_X + (index % tier.columns()) * CrateTier.SLOT_SIZE, CrateTier.SLOT_Y);
            this.row = index / tier.columns();
            updatePosition();
        }

        void updatePosition() {
            y = CrateTier.SLOT_Y + (row - scrollRow) * CrateTier.SLOT_SIZE;
        }

        @Override
        public boolean isActive() {
            return row >= scrollRow && row < scrollRow + visibleRows;
        }
    }
}