package com.misterd.superstorage.gui.custom;

import com.misterd.superstorage.blockentity.custom.SuperTankBlockEntity;
import com.misterd.superstorage.component.SSDataComponents;
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
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import org.jspecify.annotations.Nullable;

public class SuperTankMenu extends SuperMenu {
    public static final int IN_X = 86;
    public static final int IN_Y = 29;
    public static final int OUT_X = 86;
    public static final int OUT_Y = 48;
    public static final int FILL_SLOT = SuperTankBlockEntity.FILL_SLOT;
    public static final int DRAIN_SLOT = SuperTankBlockEntity.DRAIN_SLOT;

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
        ItemStacksResourceHandler io = server && tank != null ? tank.getIoSlot() : new ItemStacksResourceHandler(2) {
            @Override
            public boolean isValid(int index, ItemResource resource) {
                return resource.getItem() instanceof DrumBlockItem;
            }

            @Override
            protected int getCapacity(int index, ItemResource resource) {
                return 1;
            }
        };
        addSlot(new ResourceHandlerSlot(io, io::set, FILL_SLOT, OUT_X, OUT_Y));
        addSlot(new ResourceHandlerSlot(io, io::set, DRAIN_SLOT, IN_X, IN_Y));
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
        boolean ioSlot = slotIndex == FILL_SLOT || slotIndex == DRAIN_SLOT;
        if (ioSlot && containerInput == ContainerInput.PICKUP && !carried.isEmpty()) {
            boolean drum = carried.getItem() instanceof DrumBlockItem;
            ResourceHandler<FluidResource> item = ItemAccess.forPlayerCursor(player, this).oneByOne().getCapability(Capabilities.Fluid.ITEM);
            if (item != null && (!drum || buttonNum == 1)) {
                SuperTankBlockEntity tank = tank();
                if (server && tank != null) {
                    if (slotIndex == FILL_SLOT) {
                        FluidTransfer.fillItem(tank.getStorage(), item, level, pos, player);
                    } else if (tank.isCreative()) {
                        tank.setCreativeFluid(FluidTransfer.firstFluid(item));
                    } else {
                        FluidTransfer.emptyItem(tank.getStorage(), item, level, pos, player);
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
        if (index == FILL_SLOT || index == DRAIN_SLOT) {
            if (!moveItemStackTo(stack, playerSlotStart, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!(stack.getItem() instanceof DrumBlockItem)) {
                return ItemStack.EMPTY;
            }
            int target = stack.getOrDefault(SSDataComponents.FLUID_CONTENT.get(), SimpleFluidContent.EMPTY).isEmpty() ? FILL_SLOT : DRAIN_SLOT;
            if (!moveItemStackTo(stack, target, target + 1, false)) {
                return ItemStack.EMPTY;
            }
        }
        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return original;
    }
}