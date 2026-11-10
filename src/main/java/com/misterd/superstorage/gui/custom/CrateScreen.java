package com.misterd.superstorage.gui.custom;

import com.misterd.superstorage.SuperStorage;
import com.misterd.superstorage.util.CrateTier;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

public class CrateScreen extends AbstractContainerScreen<CrateMenu> {
    private static final Identifier WIDGETS = Identifier.fromNamespaceAndPath(SuperStorage.MODID, "textures/gui/widgets.png");
    private static final int TITLE_BAR_WIDTH = 176;
    private static final int TITLE_BAR_HEIGHT = 21;
    private static final int TITLE_TEXT_Y = 6;
    private static final int TITLE_COLOR = 0xFF404040;
    private static final int SLOT_ROW_V = 21;
    private static final int KNOB_U = 176;
    private static final int KNOB_V = 0;
    public static final int SCROLL_KNOB_WIDTH = 8;
    private static final int INVENTORY_LABEL_OFFSET = 11;

    private final CrateTier tier;
    private boolean draggingScrollbar;

    public CrateScreen(CrateMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, menu.getTier().guiWidth(), menu.getTier().guiHeight());
        this.tier = menu.getTier();
        this.inventoryLabelX = tier.playerInvX();
        this.inventoryLabelY = tier.playerInvY() - INVENTORY_LABEL_OFFSET;
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
        graphics.blit(RenderPipelines.GUI_TEXTURED, tier.texture(), leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
        if (tier.scrolls()) {
            for (int row = 0; row < tier.visibleRows(); row++) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, leftPos + CrateTier.SLOT_X - 1, topPos + CrateTier.SLOT_Y - 1 + row * CrateTier.SLOT_SIZE, 0, SLOT_ROW_V, tier.columns() * CrateTier.SLOT_SIZE, CrateTier.SLOT_SIZE, 256, 256);
            }
            graphics.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, leftPos + CrateTier.SCROLL_TRACK_X + 1, topPos + knobY(), KNOB_U, KNOB_V, SCROLL_KNOB_WIDTH, CrateTier.SCROLL_KNOB_HEIGHT, 256, 256);
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(font, title, (imageWidth - font.width(title)) / 2, TITLE_TEXT_Y - TITLE_BAR_HEIGHT, TITLE_COLOR, false);
        graphics.text(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, TITLE_COLOR, false);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (tier.scrolls() && scrollY != 0) {
            scrollTo(menu.getScrollRow() - (int) Math.signum(scrollY));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (tier.scrolls() && event.button() == 0 && isOverTrack(event.x(), event.y())) {
            draggingScrollbar = true;
            scrollToMouse(event.y());
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (draggingScrollbar) {
            scrollToMouse(event.y());
            return true;
        }
        return super.mouseDragged(event, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (event.button() == 0) {
            draggingScrollbar = false;
        }
        return super.mouseReleased(event);
    }

    private int knobTravel() {
        return CrateTier.SCROLL_TRACK_HEIGHT - 2 - CrateTier.SCROLL_KNOB_HEIGHT;
    }

    private int knobY() {
        int hidden = tier.hiddenRows();
        int offset = hidden == 0 ? 0 : menu.getScrollRow() * knobTravel() / hidden;
        return CrateTier.SCROLL_TRACK_Y + 1 + offset;
    }

    private boolean isOverTrack(double mouseX, double mouseY) {
        double x = mouseX - leftPos;
        double y = mouseY - topPos;
        return x >= CrateTier.SCROLL_TRACK_X && x < CrateTier.SCROLL_TRACK_X + 10
                && y >= CrateTier.SCROLL_TRACK_Y && y < CrateTier.SCROLL_TRACK_Y + CrateTier.SCROLL_TRACK_HEIGHT;
    }

    private void scrollToMouse(double mouseY) {
        double start = topPos + CrateTier.SCROLL_TRACK_Y + 1 + CrateTier.SCROLL_KNOB_HEIGHT / 2.0;
        double progress = Mth.clamp((mouseY - start) / knobTravel(), 0.0, 1.0);
        scrollTo((int) Math.round(progress * tier.hiddenRows()));
    }

    private void scrollTo(int row) {
        int clamped = Mth.clamp(row, 0, tier.hiddenRows());
        if (clamped == menu.getScrollRow()) {
            return;
        }
        menu.setScrollRow(clamped);
        if (minecraft != null && minecraft.gameMode != null) {
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, clamped);
        }
    }
}