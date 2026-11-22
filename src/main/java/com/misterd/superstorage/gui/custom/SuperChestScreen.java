package com.misterd.superstorage.gui.custom;

import com.misterd.superstorage.SuperStorage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.transfer.item.ItemResource;

import java.util.List;

public class SuperChestScreen extends SuperScreen<SuperChestMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(SuperStorage.MODID, "textures/gui/super_chest_gui.png");
    private static final int INFO_WIDTH = 75;

    public SuperChestScreen(SuperChestMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, TEXTURE, INFO_WIDTH);
    }

    @Override
    protected boolean hasExtractButton() {
        return true;
    }

    @Override
    protected List<Component> infoLines() {
        ItemResource stored = menu.getStored();
        if (stored.isEmpty()) {
            return List.of(Component.translatable("gui.superstorage.empty"));
        }
        Component name = stored.toStack().getHoverName().copy().withStyle(ChatFormatting.GOLD);
        if (menu.isCreative()) {
            return List.of(name, Component.translatable("tooltip.superstorage.infinite").withStyle(ChatFormatting.GREEN));
        }
        return List.of(name, Component.literal(String.format("%,d", menu.getAmount())).withStyle(ChatFormatting.GREEN));
    }

    @Override
    protected void extractLockedDisplay(GuiGraphicsExtractor graphics, int x, int y) {
        ItemResource stored = menu.getStored();
        if (!stored.isEmpty()) {
            graphics.item(stored.toStack(), x, y);
        }
    }

    @Override
    protected List<Component> slotTooltip(int slotIndex) {
        return switch (slotIndex) {
            case SuperChestMenu.IN_SLOT -> slotLines("gui.superstorage.slot.input", "gui.superstorage.slot.chest_in");
            case SuperChestMenu.OUT_SLOT -> slotLines("gui.superstorage.slot.output", "gui.superstorage.slot.chest_out");
            default -> List.of();
        };
    }
}