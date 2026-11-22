package com.misterd.superstorage.gui.custom;

import com.misterd.superstorage.SuperStorage;
import com.misterd.superstorage.util.FluidText;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

import java.util.List;

public class SuperTankScreen extends SuperScreen<SuperTankMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(SuperStorage.MODID, "textures/gui/super_tank_gui.png");
    private static final int INFO_WIDTH = 75;
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
        if (menu.getAmount() > 0 && !menu.getSlot(SuperTankMenu.FILL_SLOT).hasItem()) {
            FluidSprites.draw(graphics, menu.getStored(), leftPos + SuperTankMenu.OUT_X, topPos + SuperTankMenu.OUT_Y, DISPLAY_SIZE, DISPLAY_SIZE);
        }
    }

    @Override
    protected List<Component> infoLines() {
        FluidResource stored = menu.getStored();
        if (stored.isEmpty()) {
            return List.of(Component.translatable("gui.superstorage.empty"));
        }
        Component name = stored.toStack(1000).getHoverName().copy().withStyle(ChatFormatting.AQUA);
        if (menu.isCreative()) {
            return List.of(name, Component.translatable("tooltip.superstorage.infinite").withStyle(ChatFormatting.GREEN));
        }
        return List.of(name, Component.translatable("gui.superstorage.buckets", FluidText.buckets(menu.getAmount())).withStyle(ChatFormatting.GREEN));
    }

    @Override
    protected void extractLockedDisplay(GuiGraphicsExtractor graphics, int x, int y) {
        FluidSprites.draw(graphics, menu.getStored(), x, y, DISPLAY_SIZE, DISPLAY_SIZE);
    }

    @Override
    protected List<Component> slotTooltip(int slotIndex) {
        return switch (slotIndex) {
            case SuperTankMenu.DRAIN_SLOT -> slotLines("gui.superstorage.slot.input", "gui.superstorage.slot.tank_in", "gui.superstorage.slot.tank_in_click");
            case SuperTankMenu.FILL_SLOT -> slotLines("gui.superstorage.slot.output", "gui.superstorage.slot.tank_out", "gui.superstorage.slot.tank_out_click");
            default -> List.of();
        };
    }
}