package com.misterd.superstorage.gui.custom;

import com.misterd.superstorage.blockentity.custom.SuperTankBlockEntity;
import com.misterd.superstorage.gui.SSMenuTypes;
import com.misterd.superstorage.item.custom.DrumBlockItem;
import com.misterd.superstorage.util.FluidTransfer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import org.jspecify.annotations.Nullable;

public class SuperTankMenu extends SuperMenu {
    public static final int IO_X = 67;
    public static final int IO_Y = 29;

    private static final int IO_SLOT = 0;

    public SuperTankMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf buf) {
        super(SSMenuTypes.SUPER_TANK_MENU.get(), containerId, inventory, buf.readBlockPos());
        addSlots(inventory);
    }

    public SuperTankMenu(int containerId, Inventory inventory, SuperTankBlockEntity tank) {
        super(SSMenuTypes.SUPER_TANK_MENU.get(), containerId, inventory, tank.getBlockPos());
        addSlots(inventory);
    }

    private void addSlots(Inventory inventory) {
        SuperTankBlockEntity tank = tank();
        ItemStacksResourceHandler io = server && tank != null ? tank.getIoSlot() : new ItemStacksResourceHandler(1) {
            @Override
            public boolean isValid(int index, ItemResource resource) {
                return resource.getItem() instanceof DrumBlockItem;
            }

            @Override
            protected int getCapacity(int index, ItemResource resource) {
                return 1;
            }
        };
        addSlot(new ResourceHandlerSlot(io, io::set, 0, IO_X, IO_Y));
        addPlayerInventory(inventory);
    }

    private @Nullable SuperTankBlockEntity tank() {
        return blockEntity instanceof SuperTankBlockEntity tank ? tank : null;
    }

    public FluidResource getStored() {
        SuperTankBlockEntity tank = tank();
        return tank == null ? FluidResource.EMPTY : tank.getStored();
    }

    @Override
    public void clicked(int slotIndex, int buttonNum, ContainerInput containerInput, Player player) {
        ItemStack carried = getCarried();
        if (slotIndex == IO_SLOT && containerInput == ContainerInput.PICKUP && !carried.isEmpty()) {
            boolean drum = carried.getItem() instanceof DrumBlockItem;
            ResourceHandler<FluidResource> item = ItemAccess.forPlayerCursor(player, this).oneByOne().getCapability(Capabilities.Fluid.ITEM);
            if (item != null && (!drum || buttonNum == 1)) {
                SuperTankBlockEntity tank = tank();
                if (server && tank != null) {
                    FluidResource fluid = tank.isCreative() ? FluidTransfer.firstFluid(item) : FluidResource.EMPTY;
                    if (!fluid.isEmpty()) {
                        tank.setCreativeFluid(fluid);
                    } else {
                        FluidTransfer.moveBucket(tank.getStorage(), item, level, pos, player);
                    }
                }
                return;
            }
        }
        super.clicked(slotIndex, buttonNum, containerInput, player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (index == IO_SLOT) {
            if (!moveItemStackTo(stack, playerSlotStart, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (!(stack.getItem() instanceof DrumBlockItem) || !moveItemStackTo(stack, IO_SLOT, IO_SLOT + 1, false)) {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return original;
    }
}