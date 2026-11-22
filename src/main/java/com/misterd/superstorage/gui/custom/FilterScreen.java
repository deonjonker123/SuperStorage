package com.misterd.superstorage.gui.custom;

import com.misterd.superstorage.SuperStorage;
import com.misterd.superstorage.component.FilterSettings;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class FilterScreen extends AbstractContainerScreen<FilterMenu> {
    private static final Identifier WIDGETS = Identifier.fromNamespaceAndPath(SuperStorage.MODID, "textures/gui/widgets.png");
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(SuperStorage.MODID, "textures/gui/filter_gui.png");
    private static final int GUI_WIDTH = 176;
    private static final int GUI_HEIGHT = 182;
    private static final int TITLE_BAR_WIDTH = 145;
    private static final int TITLE_BAR_HEIGHT = 17;
    private static final int TITLE_TEXT_Y = 6;
    private static final int LABEL_COLOR = 0xFF404040;
    private static final int INVENTORY_LABEL_X = 8;
    private static final int INVENTORY_LABEL_Y = 88;
    private static final int TOGGLE_SIZE = 18;
    private static final int TOGGLE_Y = 7;
    private static final int TOGGLE_OFF_U = 68;
    private static final int TOGGLE_ON_U = 86;
    private static final int CLEAR_X = 155;
    private static final int CLEAR_Y = 11;
    private static final int CLEAR_SIZE = 10;
    private static final int CLEAR_U = 82;
    private static final int CLEAR_HOVER_U = 92;
    private static final int CLEAR_V = 39;
    private static final List<Toggle> TOGGLES = List.of(
            new Toggle(FilterSettings.BLACKLIST, 7, 51, "gui.superstorage.filter.whitelist", "gui.superstorage.filter.blacklist"),
            new Toggle(FilterSettings.MATCH_MOD, 61, 69, "gui.superstorage.filter.match_mod.off", "gui.superstorage.filter.match_mod.on"),
            new Toggle(FilterSettings.MATCH_NBT, 88, 105, "gui.superstorage.filter.match_nbt.off", "gui.superstorage.filter.match_nbt.on"),
            new Toggle(FilterSettings.MATCH_DURABILITY, 115, 87, "gui.superstorage.filter.match_durability.off", "gui.superstorage.filter.match_durability.on"));

    public FilterScreen(FilterMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, GUI_WIDTH, GUI_HEIGHT);
        this.inventoryLabelX = INVENTORY_LABEL_X;
        this.inventoryLabelY = INVENTORY_LABEL_Y;
    }

    @Override
    protected void init() {
        super.init();
        this.topPos = Math.max(TITLE_BAR_HEIGHT, (this.height - this.imageHeight + TITLE_BAR_HEIGHT) / 2);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, leftPos + (imageWidth - TITLE_BAR_WIDTH) / 2, topPos - TITLE_BAR_HEIGHT, 0, 0, TITLE_BAR_WIDTH, TITLE_BAR_HEIGHT, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
        for (Toggle toggle : TOGGLES) {
            int u = menu.hasFlag(toggle.flag()) ? TOGGLE_ON_U : TOGGLE_OFF_U;
            graphics.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, leftPos + toggle.x(), topPos + TOGGLE_Y, u, toggle.v(), TOGGLE_SIZE, TOGGLE_SIZE, 256, 256);
        }
        int clearU = isOverClear(mouseX - leftPos, mouseY - topPos) ? CLEAR_HOVER_U : CLEAR_U;
        graphics.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, leftPos + CLEAR_X, topPos + CLEAR_Y, clearU, CLEAR_V, CLEAR_SIZE, CLEAR_SIZE, 256, 256);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(font, title, (imageWidth - font.width(title)) / 2, TITLE_TEXT_Y - TITLE_BAR_HEIGHT, LABEL_COLOR, false);
        graphics.text(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, LABEL_COLOR, false);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        double x = mouseX - leftPos;
        double y = mouseY - topPos;
        if (isOverClear(x, y)) {
            graphics.setTooltipForNextFrame(Component.translatable("gui.superstorage.filter.clear"), mouseX, mouseY);
            return;
        }
        for (Toggle toggle : TOGGLES) {
            if (toggle.isHovered(x, y)) {
                graphics.setTooltipForNextFrame(Component.translatable(menu.hasFlag(toggle.flag()) ? toggle.onKey() : toggle.offKey()), mouseX, mouseY);
                return;
            }
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0) {
            double x = event.x() - leftPos;
            double y = event.y() - topPos;
            if (isOverClear(x, y)) {
                press(FilterMenu.BUTTON_CLEAR);
                return true;
            }
            for (Toggle toggle : TOGGLES) {
                if (toggle.isHovered(x, y)) {
                    press(Integer.numberOfTrailingZeros(toggle.flag()));
                    return true;
                }
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    private void press(int buttonId) {
        if (minecraft != null && minecraft.gameMode != null) {
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, buttonId);
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
    }

    private static boolean isOverClear(double x, double y) {
        return x >= CLEAR_X && x < CLEAR_X + CLEAR_SIZE && y >= CLEAR_Y && y < CLEAR_Y + CLEAR_SIZE;
    }

    private record Toggle(int flag, int x, int v, String offKey, String onKey) {
        boolean isHovered(double mouseX, double mouseY) {
            return mouseX >= x && mouseX < x + TOGGLE_SIZE && mouseY >= TOGGLE_Y && mouseY < TOGGLE_Y + TOGGLE_SIZE;
        }
    }
}