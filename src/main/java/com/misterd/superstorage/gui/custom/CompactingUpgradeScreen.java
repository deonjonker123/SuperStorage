package com.misterd.superstorage.gui.custom;

import com.misterd.superstorage.SuperStorage;
import com.misterd.superstorage.component.CompactingSettings;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class CompactingUpgradeScreen extends UpgradeSettingsScreen<CompactingUpgradeMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(SuperStorage.MODID, "textures/gui/compacting_upgrade_settings_gui.png");
    private static final int GUI_WIDTH = 176;
    private static final int GUI_HEIGHT = 124;
    private static final int TOGGLE_SIZE = 18;
    private static final int TOGGLE_Y = 7;
    private static final int ON_U = 68;
    private static final int OFF_U = 86;
    private static final List<Toggle> TOGGLES = List.of(
            new Toggle(CompactingUpgradeMenu.BUTTON_REVERSIBLE, CompactingSettings.REVERSIBLE_ONLY, 124, 123, "gui.superstorage.compacting.reversible", "gui.superstorage.compacting.any"),
            new Toggle(CompactingUpgradeMenu.BUTTON_SIZE, CompactingSettings.THREE_BY_THREE, 151, 141, "gui.superstorage.compacting.three", "gui.superstorage.compacting.two"));

    public CompactingUpgradeScreen(CompactingUpgradeMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, TEXTURE, GUI_WIDTH, GUI_HEIGHT);
    }

    @Override
    protected Component filterTooltip(int index) {
        return Component.translatable("gui.superstorage.slot.compacting_filter");
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        for (Toggle toggle : TOGGLES) {
            int u = menu.hasFlag(toggle.flag()) ? ON_U : OFF_U;
            graphics.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, leftPos + toggle.x(), topPos + TOGGLE_Y, u, toggle.v(), TOGGLE_SIZE, TOGGLE_SIZE, 256, 256);
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        for (Toggle toggle : TOGGLES) {
            if (within(mouseX - leftPos, mouseY - topPos, toggle.x(), TOGGLE_Y, TOGGLE_SIZE, TOGGLE_SIZE)) {
                graphics.setTooltipForNextFrame(Component.translatable(menu.hasFlag(toggle.flag()) ? toggle.onKey() : toggle.offKey()), mouseX, mouseY);
                return;
            }
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0) {
            for (Toggle toggle : TOGGLES) {
                if (within(event.x() - leftPos, event.y() - topPos, toggle.x(), TOGGLE_Y, TOGGLE_SIZE, TOGGLE_SIZE)) {
                    click(toggle.button());
                    return true;
                }
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    private record Toggle(int button, int flag, int x, int v, String onKey, String offKey) {
    }
}