package com.misterd.superstorage.gui.custom;

import com.misterd.superstorage.SuperStorage;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public abstract class UpgradeSettingsScreen<T extends UpgradeSettingsMenu> extends AbstractContainerScreen<T> {
    protected static final Identifier WIDGETS = Identifier.fromNamespaceAndPath(SuperStorage.MODID, "textures/gui/widgets.png");
    private static final int TITLE_BAR_WIDTH = 145;
    private static final int TITLE_BAR_HEIGHT = 17;
    private static final int TITLE_TEXT_Y = 6;
    private static final int LABEL_COLOR = 0xFF404040;
    private static final int INVENTORY_LABEL_X = 8;
    private static final int INVENTORY_LABEL_OFFSET = 11;

    private final Identifier texture;

    protected UpgradeSettingsScreen(T menu, Inventory inventory, Component title, Identifier texture, int width, int height) {
        super(menu, inventory, title, width, height);
        this.texture = texture;
        this.inventoryLabelX = INVENTORY_LABEL_X;
        this.inventoryLabelY = menu.getPlayerInvY() - INVENTORY_LABEL_OFFSET;
    }

    protected abstract Component filterTooltip(int index);

    @Override
    protected void init() {
        super.init();
        this.topPos = Math.max(TITLE_BAR_HEIGHT, (this.height - this.imageHeight + TITLE_BAR_HEIGHT) / 2);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, leftPos + (imageWidth - TITLE_BAR_WIDTH) / 2, topPos - TITLE_BAR_HEIGHT, 0, 0, TITLE_BAR_WIDTH, TITLE_BAR_HEIGHT, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(font, title, (imageWidth - font.width(title)) / 2, TITLE_TEXT_Y - TITLE_BAR_HEIGHT, LABEL_COLOR, false);
        graphics.text(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, LABEL_COLOR, false);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        Slot slot = hoveredSlot;
        if (slot != null && !slot.hasItem() && menu.getCarried().isEmpty() && slot.index < menu.getFilterCount()) {
            graphics.setTooltipForNextFrame(filterTooltip(slot.index), mouseX, mouseY);
        }
    }

    @Override
    public void onClose() {
        press(UpgradeSettingsMenu.BUTTON_BACK);
    }

    protected void click(int buttonId) {
        press(buttonId);
        if (minecraft != null) {
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
    }

    private void press(int buttonId) {
        if (minecraft != null && minecraft.gameMode != null) {
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, buttonId);
        }
    }

    protected static boolean within(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }
}