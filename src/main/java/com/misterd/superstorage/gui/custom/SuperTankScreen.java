package com.misterd.superstorage.gui.custom;

import com.misterd.superstorage.SuperStorage;
import com.misterd.superstorage.util.FluidText;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

import java.util.List;

public class SuperTankScreen extends SuperScreen<SuperTankMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(SuperStorage.MODID, "textures/gui/super_tank_gui.png");
    private static final int INFO_WIDTH = 56;
    private static final int DISPLAY_SIZE = 16;

    public SuperTankScreen(SuperTankMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, TEXTURE, INFO_WIDTH);
    }

    @Override
    protected boolean hasExtractButton() {
        return false;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        if (menu.getAmount() > 0 && !menu.getSlot(0).hasItem()) {
            FluidSprites.draw(graphics, menu.getStored(), leftPos + SuperTankMenu.IO_X, topPos + SuperTankMenu.IO_Y, DISPLAY_SIZE, DISPLAY_SIZE);
        }
    }

    @Override
    protected List<Component> infoLines() {
        FluidResource stored = menu.getStored();
        if (stored.isEmpty()) {
            return List.of(Component.translatable("gui.superstorage.empty"));
        }
        long amount = menu.getAmount();
        long capacity = menu.getCapacity();
        return List.of(
                stored.toStack(1000).getHoverName(),
                Component.translatable("gui.superstorage.buckets", FluidText.buckets(amount)),
                Component.translatable("gui.superstorage.of_buckets", FluidText.buckets(capacity)),
                Component.literal(String.format("%.1f%%", capacity == 0 ? 0.0 : amount * 100.0 / capacity)));
    }

    @Override
    protected void extractLockedDisplay(GuiGraphicsExtractor graphics, int x, int y) {
        FluidSprites.draw(graphics, menu.getStored(), x, y, DISPLAY_SIZE, DISPLAY_SIZE);
    }
}