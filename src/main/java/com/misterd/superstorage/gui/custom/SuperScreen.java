package com.misterd.superstorage.gui.custom;

import com.misterd.superstorage.SuperStorage;
import com.misterd.superstorage.util.RelativeSide;
import com.misterd.superstorage.util.SideMode;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public abstract class SuperScreen<T extends SuperMenu> extends AbstractContainerScreen<T> {
    protected static final Identifier WIDGETS = Identifier.fromNamespaceAndPath(SuperStorage.MODID, "textures/gui/widgets.png");
    private static final int GUI_WIDTH = 176;
    private static final int GUI_HEIGHT = 179;
    private static final int TITLE_BAR_WIDTH = 176;
    private static final int TITLE_BAR_HEIGHT = 21;
    private static final int TITLE_TEXT_Y = 6;
    private static final int LABEL_COLOR = 0xFF404040;
    private static final int INFO_COLOR = 0xFFFFFFFF;
    private static final int INVENTORY_LABEL_OFFSET = 11;
    private static final int BUTTON_SIZE = 18;
    private static final int OFF_U = 0;
    private static final int ON_U = 18;
    private static final int CAPABILITIES_V = 39;
    private static final int LOCKED_V = 57;
    private static final int UNLOCKED_V = 75;
    private static final int VOID_V = 93;
    private static final int EXTRACT_V = 111;
    private static final int SIDE_NONE_V = 129;
    private static final int SIDE_OUTPUT_V = 147;
    private static final int INFO_X = 9;
    private static final int INFO_Y = 9;
    private static final int INFO_PADDING = 2;
    private static final int LINE_HEIGHT = 9;
    private static final int DISPLAY_X = 67;
    private static final int DISPLAY_Y = 48;
    private static final int GAUGE_X = 8;
    private static final int GAUGE_Y = 71;
    private static final int GAUGE_WIDTH = 160;
    private static final int GAUGE_HEIGHT = 8;
    private static final int GAUGE_V = 165;
    private static final int[][] SIDE_POSITIONS = {
            {132, 7},
            {132, 45},
            {132, 26},
            {151, 45},
            {113, 26},
            {151, 26}
    };

    private final Identifier texture;
    private final int infoWidth;
    private final List<Button> buttons = new ArrayList<>();

    protected SuperScreen(T menu, Inventory inventory, Component title, Identifier texture, int infoWidth) {
        super(menu, inventory, title, GUI_WIDTH, GUI_HEIGHT);
        this.texture = texture;
        this.infoWidth = infoWidth;
        this.inventoryLabelX = SuperMenu.PLAYER_INV_X;
        this.inventoryLabelY = SuperMenu.PLAYER_INV_Y - INVENTORY_LABEL_OFFSET;
        buttons.add(new Button(SuperMenu.BUTTON_CAPABILITIES, 9, 47));
        buttons.add(new Button(SuperMenu.BUTTON_LOCK, 28, 47));
        buttons.add(new Button(SuperMenu.BUTTON_VOID, 47, 47));
        if (hasExtractButton()) {
            buttons.add(new Button(SuperMenu.BUTTON_EXTRACT, 85, 47));
        }
        for (RelativeSide side : RelativeSide.values()) {
            int[] position = SIDE_POSITIONS[side.ordinal()];
            buttons.add(new Button(SuperMenu.BUTTON_SIDE + side.ordinal(), position[0], position[1]));
        }
    }

    protected abstract boolean hasExtractButton();

    protected abstract List<Component> infoLines();

    protected abstract void extractLockedDisplay(GuiGraphicsExtractor graphics, int x, int y);

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
        for (Button button : buttons) {
            int[] uv = buttonUv(button, button.isHovered(mouseX - leftPos, mouseY - topPos));
            graphics.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, leftPos + button.x, topPos + button.y, uv[0], uv[1], BUTTON_SIZE, BUTTON_SIZE, 256, 256);
            RelativeSide side = button.side();
            if (side != null) {
                extractNeighbour(graphics, side, leftPos + button.x, topPos + button.y);
            }
        }
        if (menu.isLocked()) {
            extractLockedDisplay(graphics, leftPos + DISPLAY_X, topPos + DISPLAY_Y);
        }
        int filled = (int) Math.round(GAUGE_WIDTH * fillRatio());
        if (filled > 0) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, leftPos + GAUGE_X, topPos + GAUGE_Y, 0, GAUGE_V, filled, GAUGE_HEIGHT, 256, 256);
        }
    }

    private double fillRatio() {
        long capacity = menu.getCapacity();
        return capacity <= 0 ? 0.0 : Math.min(1.0, (double) menu.getAmount() / capacity);
    }

    private boolean isOverGauge(double mouseX, double mouseY) {
        return mouseX >= GAUGE_X && mouseX < GAUGE_X + GAUGE_WIDTH && mouseY >= GAUGE_Y && mouseY < GAUGE_Y + GAUGE_HEIGHT;
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(font, title, (imageWidth - font.width(title)) / 2, TITLE_TEXT_Y - TITLE_BAR_HEIGHT, LABEL_COLOR, false);
        graphics.text(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, LABEL_COLOR, false);
        List<Component> lines = infoLines();
        for (int i = 0; i < lines.size(); i++) {
            String line = font.plainSubstrByWidth(lines.get(i).getString(), infoWidth - INFO_PADDING * 2);
            graphics.text(font, line, INFO_X + INFO_PADDING, INFO_Y + INFO_PADDING + i * LINE_HEIGHT, INFO_COLOR, false);
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        if (isOverGauge(mouseX - leftPos, mouseY - topPos)) {
            graphics.setTooltipForNextFrame(Component.translatable("gui.superstorage.fill", String.format("%.1f", fillRatio() * 100.0)), mouseX, mouseY);
            return;
        }
        for (Button button : buttons) {
            if (button.isHovered(mouseX - leftPos, mouseY - topPos)) {
                graphics.setComponentTooltipForNextFrame(font, buttonTooltip(button), mouseX, mouseY);
                return;
            }
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0) {
            for (Button button : buttons) {
                if (button.isHovered(event.x() - leftPos, event.y() - topPos)) {
                    if (minecraft != null && minecraft.gameMode != null) {
                        minecraft.gameMode.handleInventoryButtonClick(menu.containerId, button.id);
                        minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    }
                    return true;
                }
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    private int[] buttonUv(Button button, boolean hovered) {
        RelativeSide side = button.side();
        if (side != null) {
            SideMode mode = menu.getSide(side);
            return new int[]{mode.canInput() ? ON_U : OFF_U, mode.canOutput() ? SIDE_OUTPUT_V : SIDE_NONE_V};
        }
        return switch (button.id) {
            case SuperMenu.BUTTON_CAPABILITIES -> new int[]{menu.isCapabilitiesEnabled() ? ON_U : OFF_U, CAPABILITIES_V};
            case SuperMenu.BUTTON_LOCK -> menu.isLocked() ? new int[]{ON_U, LOCKED_V} : new int[]{OFF_U, UNLOCKED_V};
            case SuperMenu.BUTTON_VOID -> new int[]{menu.isVoidOverflow() ? ON_U : OFF_U, VOID_V};
            default -> new int[]{hovered ? ON_U : OFF_U, EXTRACT_V};
        };
    }

    private List<Component> buttonTooltip(Button button) {
        RelativeSide side = button.side();
        if (side != null) {
            List<Component> lines = new ArrayList<>();
            lines.add(Component.translatable("gui.superstorage.side." + side.name().toLowerCase(), Component.translatable("gui.superstorage.mode." + menu.getSide(side).getSerializedName())));
            ItemStack neighbour = neighbourStack(side);
            if (!neighbour.isEmpty()) {
                lines.add(neighbour.getHoverName().copy().withStyle(ChatFormatting.GRAY));
            }
            return lines;
        }
        return switch (button.id) {
            case SuperMenu.BUTTON_CAPABILITIES -> List.of(Component.translatable(menu.isCapabilitiesEnabled() ? "gui.superstorage.capabilities.on" : "gui.superstorage.capabilities.off"));
            case SuperMenu.BUTTON_LOCK -> List.of(Component.translatable(menu.isLocked() ? "gui.superstorage.lock.on" : "gui.superstorage.lock.off"));
            case SuperMenu.BUTTON_VOID -> List.of(Component.translatable(menu.isVoidOverflow() ? "gui.superstorage.void.on" : "gui.superstorage.void.off"));
            default -> List.of(Component.translatable("gui.superstorage.extract"));
        };
    }

    private void extractNeighbour(GuiGraphicsExtractor graphics, RelativeSide side, int x, int y) {
        ItemStack stack = neighbourStack(side);
        if (stack.isEmpty()) {
            return;
        }
        graphics.pose().pushMatrix();
        graphics.pose().translate(x + 3, y + 3);
        graphics.pose().scale(0.75F, 0.75F);
        graphics.item(stack, 0, 0);
        graphics.pose().popMatrix();
    }

    private ItemStack neighbourStack(RelativeSide side) {
        if (Minecraft.getInstance().level == null) {
            return ItemStack.EMPTY;
        }
        BlockState state = Minecraft.getInstance().level.getBlockState(menu.getPos().relative(side.toDirection(menu.getFacing())));
        return state.isAir() ? ItemStack.EMPTY : new ItemStack(state.getBlock());
    }

    private record Button(int id, int x, int y) {
        boolean isHovered(double mouseX, double mouseY) {
            return mouseX >= x && mouseX < x + BUTTON_SIZE && mouseY >= y && mouseY < y + BUTTON_SIZE;
        }

        RelativeSide side() {
            int index = id - SuperMenu.BUTTON_SIDE;
            return index >= 0 && index < RelativeSide.values().length ? RelativeSide.values()[index] : null;
        }
    }
}