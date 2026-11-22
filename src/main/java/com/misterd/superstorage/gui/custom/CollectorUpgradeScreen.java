package com.misterd.superstorage.gui.custom;

import com.misterd.superstorage.SuperStorage;
import com.misterd.superstorage.client.CollectorAreas;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;

public class CollectorUpgradeScreen extends UpgradeSettingsScreen<CollectorUpgradeMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(SuperStorage.MODID, "textures/gui/collector_upgrade_settings_gui.png");
    private static final int GUI_WIDTH = 176;
    private static final int GUI_HEIGHT = 160;
    private static final int BUTTON_SIZE = 10;
    private static final int BUTTON_V = 39;
    private static final int DECREASE_U = 122;
    private static final int DECREASE_HOVER_U = 132;
    private static final int INCREASE_U = 102;
    private static final int INCREASE_HOVER_U = 112;
    private static final int RESET_U = 62;
    private static final int RESET_HOVER_U = 72;
    private static final int AREA_OFF_U = 42;
    private static final int AREA_ON_U = 52;
    private static final int RESET_X = 138;
    private static final int RESET_Y = 21;
    private static final int AREA_X = 138;
    private static final int AREA_Y = 51;
    private static final int VALUE_WIDTH = 16;
    private static final int VALUE_COLOR = 0xFF000000;
    private static final int LABEL_COLOR = 0xFF404040;
    private static final float VALUE_SCALE = 0.8F;
    private static final int VALUE_HEIGHT = 10;
    private static final int HEADER_Y = 11;
    private static final int SIZE_CENTER_X = 52;
    private static final int OFFSET_CENTER_X = 115;
    private static final int AXIS_GAP = 2;
    private static final String[] AXES = {"X", "Y", "Z"};
    private static final int[] DECREASE_X = {32, 32, 32, 95, 95, 95};
    private static final int[] VALUE_X = {44, 44, 44, 107, 107, 107};
    private static final int[] INCREASE_X = {62, 62, 62, 125, 125, 125};
    private static final int[] ROW_Y = {21, 36, 51, 21, 36, 51};
    private static final String[] VALUE_KEYS = {"size_x", "size_y", "size_z", "offset_x", "offset_y", "offset_z"};

    public CollectorUpgradeScreen(CollectorUpgradeMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, TEXTURE, GUI_WIDTH, GUI_HEIGHT);
    }

    @Override
    protected Component filterTooltip(int index) {
        return Component.translatable("gui.superstorage.slot.collector_filter");
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        double x = mouseX - leftPos;
        double y = mouseY - topPos;
        for (int i = 0; i < ROW_Y.length; i++) {
            button(graphics, DECREASE_X[i], ROW_Y[i], within(x, y, DECREASE_X[i], ROW_Y[i], BUTTON_SIZE, BUTTON_SIZE) ? DECREASE_HOVER_U : DECREASE_U);
            button(graphics, INCREASE_X[i], ROW_Y[i], within(x, y, INCREASE_X[i], ROW_Y[i], BUTTON_SIZE, BUTTON_SIZE) ? INCREASE_HOVER_U : INCREASE_U);
        }
        button(graphics, RESET_X, RESET_Y, within(x, y, RESET_X, RESET_Y, BUTTON_SIZE, BUTTON_SIZE) ? RESET_HOVER_U : RESET_U);
        button(graphics, AREA_X, AREA_Y, CollectorAreas.isShown(menu.getPos()) ? AREA_ON_U : AREA_OFF_U);
    }

    private void button(GuiGraphicsExtractor graphics, int x, int y, int u) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, leftPos + x, topPos + y, u, BUTTON_V, BUTTON_SIZE, BUTTON_SIZE, 256, 256);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);
        Component size = Component.translatable("gui.superstorage.collector.size");
        Component offset = Component.translatable("gui.superstorage.collector.offset");
        graphics.text(font, size, SIZE_CENTER_X - font.width(size) / 2, HEADER_Y, LABEL_COLOR, false);
        graphics.text(font, offset, OFFSET_CENTER_X - font.width(offset) / 2, HEADER_Y, LABEL_COLOR, false);
        for (int i = 0; i < AXES.length; i++) {
            graphics.text(font, AXES[i], DECREASE_X[i] - AXIS_GAP - font.width(AXES[i]), ROW_Y[i] + 1, LABEL_COLOR, false);
        }
        for (int i = 0; i < ROW_Y.length; i++) {
            String value = String.valueOf(menu.getValue(i));
            float x = VALUE_X[i] + (VALUE_WIDTH - font.width(value) * VALUE_SCALE) / 2.0F;
            float y = ROW_Y[i] + (VALUE_HEIGHT - font.lineHeight * VALUE_SCALE) / 2.0F + 1.0F;
            graphics.pose().pushMatrix();
            graphics.pose().translate(x, y);
            graphics.pose().scale(VALUE_SCALE, VALUE_SCALE);
            graphics.text(font, value, 0, 0, VALUE_COLOR, false);
            graphics.pose().popMatrix();
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        CollectorAreas.update(menu.getPos(), menu.area());
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        double x = mouseX - leftPos;
        double y = mouseY - topPos;
        if (within(x, y, RESET_X, RESET_Y, BUTTON_SIZE, BUTTON_SIZE)) {
            graphics.setTooltipForNextFrame(Component.translatable("gui.superstorage.collector.reset"), mouseX, mouseY);
            return;
        }
        if (within(x, y, AREA_X, AREA_Y, BUTTON_SIZE, BUTTON_SIZE)) {
            graphics.setTooltipForNextFrame(Component.translatable(CollectorAreas.isShown(menu.getPos()) ? "gui.superstorage.collector.hide_area" : "gui.superstorage.collector.show_area"), mouseX, mouseY);
            return;
        }
        for (int i = 0; i < ROW_Y.length; i++) {
            if (within(x, y, VALUE_X[i], ROW_Y[i], VALUE_WIDTH, BUTTON_SIZE)) {
                graphics.setTooltipForNextFrame(Component.translatable("gui.superstorage.collector." + VALUE_KEYS[i]), mouseX, mouseY);
                return;
            }
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0) {
            double x = event.x() - leftPos;
            double y = event.y() - topPos;
            boolean shift = minecraft != null && minecraft.hasShiftDown();
            for (int i = 0; i < ROW_Y.length; i++) {
                if (within(x, y, DECREASE_X[i], ROW_Y[i], BUTTON_SIZE, BUTTON_SIZE)) {
                    click(CollectorUpgradeMenu.buttonId(i, shift ? 2 : 0));
                    return true;
                }
                if (within(x, y, INCREASE_X[i], ROW_Y[i], BUTTON_SIZE, BUTTON_SIZE)) {
                    click(CollectorUpgradeMenu.buttonId(i, shift ? 3 : 1));
                    return true;
                }
            }
            if (within(x, y, RESET_X, RESET_Y, BUTTON_SIZE, BUTTON_SIZE)) {
                click(CollectorUpgradeMenu.BUTTON_RESET);
                return true;
            }
            if (within(x, y, AREA_X, AREA_Y, BUTTON_SIZE, BUTTON_SIZE)) {
                CollectorAreas.toggle(menu.getPos(), menu.area());
                if (minecraft != null) {
                    minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                }
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }
}