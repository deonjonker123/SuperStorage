package com.misterd.superstorage.gui.custom;

import com.misterd.superstorage.SuperStorage;
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
        long amount = menu.getAmount();
        long capacity = menu.getCapacity();
        return List.of(
                stored.toStack().getHoverName(),
                Component.literal(String.format("%,d", amount)),
                Component.translatable("gui.superstorage.of", String.format("%,d", capacity)),
                Component.literal(String.format("%.1f%%", capacity == 0 ? 0.0 : amount * 100.0 / capacity)));
    }

    @Override
    protected void extractLockedDisplay(GuiGraphicsExtractor graphics, int x, int y) {
        ItemResource stored = menu.getStored();
        if (!stored.isEmpty()) {
            graphics.item(stored.toStack(), x, y);
        }
    }
}