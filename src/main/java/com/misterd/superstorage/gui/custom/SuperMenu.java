package com.misterd.superstorage.gui.custom;

import com.misterd.superstorage.blockentity.custom.SuperBlockEntity;
import com.misterd.superstorage.util.RelativeSide;
import com.misterd.superstorage.util.SideMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jspecify.annotations.Nullable;

public abstract class SuperMenu extends AbstractContainerMenu {
    public static final int BUTTON_CAPABILITIES = 0;
    public static final int BUTTON_LOCK = 1;
    public static final int BUTTON_VOID = 2;
    public static final int BUTTON_EXTRACT = 3;
    public static final int BUTTON_SIDE = 10;

    public static final int PLAYER_INV_X = 8;
    public static final int PLAYER_INV_Y = 96;
    public static final int HOTBAR_Y = 155;

    private static final int AMOUNT_SLOTS = 3;

    protected final Level level;
    protected final BlockPos pos;
    protected final boolean server;
    protected final @Nullable SuperBlockEntity blockEntity;
    private final ContainerLevelAccess access;
    private long syncedAmount;
    private int syncedSettings;
    protected int playerSlotStart;

    protected SuperMenu(MenuType<?> type, int containerId, Inventory inventory, BlockPos pos) {
        super(type, containerId);
        this.level = inventory.player.level();
        this.pos = pos;
        this.server = !level.isClientSide();
        this.blockEntity = level.getBlockEntity(pos) instanceof SuperBlockEntity be ? be : null;
        this.access = ContainerLevelAccess.create(level, pos);
        for (int i = 0; i < AMOUNT_SLOTS; i++) {
            int shift = i * 16;
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return (int) ((serverAmount() >>> shift) & 0xFFFF);
                }

                @Override
                public void set(int value) {
                    syncedAmount = (syncedAmount & ~(0xFFFFL << shift)) | ((long) (value & 0xFFFF) << shift);
                }
            });
        }
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return encodeSettings();
            }

            @Override
            public void set(int value) {
                syncedSettings = value & 0xFFFF;
            }
        });
    }

    protected void addPlayerInventory(Inventory inventory) {
        playerSlotStart = slots.size();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9 + 9, PLAYER_INV_X + col * 18, PLAYER_INV_Y + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inventory, col, PLAYER_INV_X + col * 18, HOTBAR_Y));
        }
    }

    public BlockPos getPos() {
        return pos;
    }

    public Level getLevel() {
        return level;
    }

    public Direction getFacing() {
        BlockState state = level.getBlockState(pos);
        return state.hasProperty(BlockStateProperties.HORIZONTAL_FACING) ? state.getValue(BlockStateProperties.HORIZONTAL_FACING) : Direction.NORTH;
    }

    public long getAmount() {
        return server ? serverAmount() : syncedAmount;
    }

    public long getCapacity() {
        return blockEntity == null ? 0 : blockEntity.getCapacity();
    }

    public boolean isLocked() {
        return (settings() & 1) != 0;
    }

    public boolean isVoidOverflow() {
        return (settings() & 2) != 0;
    }

    public boolean isCapabilitiesEnabled() {
        return (settings() & 4) != 0;
    }

    public SideMode getSide(RelativeSide side) {
        return SideMode.values()[(settings() >> (3 + side.ordinal() * 2)) & 3];
    }

    private int settings() {
        return server ? encodeSettings() : syncedSettings;
    }

    private long serverAmount() {
        return blockEntity == null ? 0 : blockEntity.getAmount();
    }

    private int encodeSettings() {
        if (blockEntity == null) {
            return 0;
        }
        int bits = 0;
        if (blockEntity.getConfig().isLocked()) {
            bits |= 1;
        }
        if (blockEntity.getConfig().isVoidOverflow()) {
            bits |= 2;
        }
        if (blockEntity.getConfig().isCapabilitiesEnabled()) {
            bits |= 4;
        }
        for (RelativeSide side : RelativeSide.values()) {
            bits |= blockEntity.getConfig().getSide(side).ordinal() << (3 + side.ordinal() * 2);
        }
        return bits;
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        if (blockEntity == null) {
            return false;
        }
        if (buttonId == BUTTON_CAPABILITIES) {
            blockEntity.setCapabilitiesEnabled(!blockEntity.getConfig().isCapabilitiesEnabled());
            return true;
        }
        if (buttonId == BUTTON_LOCK) {
            blockEntity.setLocked(!blockEntity.getConfig().isLocked());
            return true;
        }
        if (buttonId == BUTTON_VOID) {
            blockEntity.setVoidOverflow(!blockEntity.getConfig().isVoidOverflow());
            return true;
        }
        int side = buttonId - BUTTON_SIDE;
        if (side >= 0 && side < RelativeSide.values().length) {
            blockEntity.cycleSide(RelativeSide.values()[side]);
            return true;
        }
        return false;
    }

    @Override
    public boolean stillValid(Player player) {
        return access.evaluate((lvl, p) -> lvl.getBlockEntity(p) instanceof SuperBlockEntity && player.isWithinBlockInteractionRange(p, 4.0), true);
    }
}