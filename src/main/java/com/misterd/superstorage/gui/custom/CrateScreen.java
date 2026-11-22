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
    private static final Identifier WIDGETS = texture("widgets");
    private static final Identifier BACKGROUND = texture("crate_gui_bg");
    private static final Identifier BACKGROUND_SB = texture("crate_gui_bg_sb");
    private static final Identifier BACKGROUND_WOOD = texture("crate_gui_bg_wood");
    private static final int TITLE_BAR_WIDTH = 145;
    private static final int TITLE_BAR_HEIGHT = 17;
    private static final int TITLE_TEXT_Y = 6;
    private static final int TITLE_COLOR = 0xFF404040;
    private static final int INVENTORY_LABEL_OFFSET = 11;
    private static final int SLOT_ROW_V = 21;
    private static final int KNOB_U = 176;
    private static final int KNOB_V = 0;
    private static final int KNOB_WIDTH = 4;
    private static final int KNOB_HEIGHT = 8;
    private static final int WOOD_WIDTH = 176;
    private static final int WOOD_HEIGHT = 216;
    private static final int WOOD_INV_X = 8;
    private static final int WOOD_INV_Y = 131;
    private static final int WIDTH = 230;
    private static final int WIDTH_SB = 242;
    private static final int INV_X = 35;
    private static final int INV_X_SB = 41;
    private static final int INV_Y_BASE = 27;
    private static final int BASE_HEIGHT = 112;
    private static final int TOP_HEIGHT = 8;
    private static final int MIDDLE_V = 8;
    private static final int BOTTOM_V = 150;
    private static final int BOTTOM_HEIGHT = 106;
    private static final int BOTTOM_OFFSET = 6;
    private static final int TRACK_X = 228;
    private static final int TRACK_Y = 7;
    private static final int TRACK_WIDTH = 6;

    private final CrateTier tier;
    private int visibleRows;
    private boolean scrollbar;
    private boolean draggingScrollbar;

    public CrateScreen(CrateMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, WIDTH, BASE_HEIGHT);
        this.tier = menu.getTier();
    }

    private static Identifier texture(String name) {
        return Identifier.fromNamespaceAndPath(SuperStorage.MODID, "textures/gui/" + name + ".png");
    }

    @Override
    protected void init() {
        int invX;
        int invY;
        if (tier.fixedLayout()) {
            visibleRows = tier.rows();
            scrollbar = false;
            imageWidth = WOOD_WIDTH;
            imageHeight = WOOD_HEIGHT;
            invX = WOOD_INV_X;
            invY = WOOD_INV_Y;
        } else {
            visibleRows = Mth.clamp((height - TITLE_BAR_HEIGHT - BASE_HEIGHT) / CrateTier.SLOT_SIZE, 1, tier.rows());
            scrollbar = visibleRows < tier.rows();
            imageWidth = scrollbar ? WIDTH_SB : WIDTH;
            imageHeight = BASE_HEIGHT + visibleRows * CrateTier.SLOT_SIZE;
            invX = scrollbar ? INV_X_SB : INV_X;
            invY = INV_Y_BASE + visibleRows * CrateTier.SLOT_SIZE;
        }
        inventoryLabelX = invX;
        inventoryLabelY = invY - INVENTORY_LABEL_OFFSET;
        menu.setLayout(visibleRows, invX, invY);
        super.init();
        this.topPos = Math.max(TITLE_BAR_HEIGHT, (this.height - this.imageHeight + TITLE_BAR_HEIGHT) / 2);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, leftPos + (imageWidth - TITLE_BAR_WIDTH) / 2, topPos - TITLE_BAR_HEIGHT, 0, 0, TITLE_BAR_WIDTH, TITLE_BAR_HEIGHT, 256, 256);
        if (tier.fixedLayout()) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND_WOOD, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
            return;
        }
        Identifier background = scrollbar ? BACKGROUND_SB : BACKGROUND;
        int bottomY = visibleRows * CrateTier.SLOT_SIZE + BOTTOM_OFFSET;
        graphics.blit(RenderPipelines.GUI_TEXTURED, background, leftPos, topPos, 0, 0, imageWidth, TOP_HEIGHT, 256, 256);
        for (int y = TOP_HEIGHT; y < bottomY; ) {
            int height = Math.min(BOTTOM_V - MIDDLE_V, bottomY - y);
            graphics.blit(RenderPipelines.GUI_TEXTURED, background, leftPos, topPos + y, 0, MIDDLE_V, imageWidth, height, 256, 256);
            y += height;
        }
        graphics.blit(RenderPipelines.GUI_TEXTURED, background, leftPos, topPos + bottomY, 0, BOTTOM_V, imageWidth, BOTTOM_HEIGHT, 256, 256);
        for (int row = 0; row < visibleRows; row++) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, leftPos + CrateTier.SLOT_X - 1, topPos + CrateTier.SLOT_Y - 1 + row * CrateTier.SLOT_SIZE, 0, SLOT_ROW_V, tier.columns() * CrateTier.SLOT_SIZE, CrateTier.SLOT_SIZE, 256, 256);
        }
        if (scrollbar) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, leftPos + TRACK_X + 1, topPos + knobY(), KNOB_U, KNOB_V, KNOB_WIDTH, KNOB_HEIGHT, 256, 256);
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(font, title, (imageWidth - font.width(title)) / 2, TITLE_TEXT_Y - TITLE_BAR_HEIGHT, TITLE_COLOR, false);
        graphics.text(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, TITLE_COLOR, false);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (scrollbar && scrollY != 0 && minecraft != null && !minecraft.hasShiftDown()) {
            menu.setScrollRow(menu.getScrollRow() - (int) Math.signum(scrollY));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (scrollbar && event.button() == 0 && isOverTrack(event.x(), event.y())) {
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

    private int trackHeight() {
        return visibleRows * CrateTier.SLOT_SIZE;
    }

    private int knobTravel() {
        return trackHeight() - 2 - KNOB_HEIGHT;
    }

    private int knobY() {
        int hidden = menu.getHiddenRows();
        int offset = hidden == 0 ? 0 : menu.getScrollRow() * knobTravel() / hidden;
        return TRACK_Y + 1 + offset;
    }

    private boolean isOverTrack(double mouseX, double mouseY) {
        double x = mouseX - leftPos;
        double y = mouseY - topPos;
        return x >= TRACK_X && x < TRACK_X + TRACK_WIDTH && y >= TRACK_Y && y < TRACK_Y + trackHeight();
    }

    private void scrollToMouse(double mouseY) {
        double start = topPos + TRACK_Y + 1 + KNOB_HEIGHT / 2.0;
        double progress = Mth.clamp((mouseY - start) / knobTravel(), 0.0, 1.0);
        menu.setScrollRow((int) Math.round(progress * menu.getHiddenRows()));
    }
}