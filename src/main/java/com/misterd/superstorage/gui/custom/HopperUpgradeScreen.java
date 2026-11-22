package com.misterd.superstorage.gui.custom;

import com.misterd.superstorage.SuperStorage;
import com.misterd.superstorage.component.HopperSettings;
import com.misterd.superstorage.util.SideMode;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class HopperUpgradeScreen extends UpgradeSettingsScreen<HopperUpgradeMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(SuperStorage.MODID, "textures/gui/hopper_upgrade_settings_gui.png");
    private static final int GUI_WIDTH = 176;
    private static final int GUI_HEIGHT = 162;
    private static final int BUTTON_SIZE = 18;
    private static final int OFF_U = 0;
    private static final int ON_U = 18;
    private static final int SIDE_NONE_V = 129;
    private static final int SIDE_OUTPUT_V = 147;
    private static final int[][] SIDE_POSITIONS = {
            {79, 45},
            {79, 7},
            {79, 26},
            {98, 45},
            {60, 26},
            {98, 26}
    };

    public HopperUpgradeScreen(HopperUpgradeMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, TEXTURE, GUI_WIDTH, GUI_HEIGHT);
    }

    @Override
    protected Component filterTooltip(int index) {
        return Component.translatable(index == HopperSettings.INPUT_FILTER ? "gui.superstorage.slot.pull_filter" : "gui.superstorage.slot.push_filter");
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        for (Direction direction : Direction.values()) {
            int[] position = SIDE_POSITIONS[direction.ordinal()];
            SideMode mode = menu.getSide(direction);
            int x = leftPos + position[0];
            int y = topPos + position[1];
            graphics.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, x, y, mode.canInput() ? ON_U : OFF_U, mode.canOutput() ? SIDE_OUTPUT_V : SIDE_NONE_V, BUTTON_SIZE, BUTTON_SIZE, 256, 256);
            ItemStack neighbour = neighbourStack(direction);
            if (!neighbour.isEmpty()) {
                graphics.pose().pushMatrix();
                graphics.pose().translate(x + 3, y + 3);
                graphics.pose().scale(0.75F, 0.75F);
                graphics.item(neighbour, 0, 0);
                graphics.pose().popMatrix();
            }
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        Direction direction = sideAt(mouseX - leftPos, mouseY - topPos);
        if (direction == null) {
            return;
        }
        List<Component> lines = new ArrayList<>();
        lines.add(Component.translatable("gui.superstorage.direction." + direction.getSerializedName(), Component.translatable("gui.superstorage.mode." + menu.getSide(direction).getSerializedName())));
        ItemStack neighbour = neighbourStack(direction);
        if (!neighbour.isEmpty()) {
            lines.add(neighbour.getHoverName().copy().withStyle(ChatFormatting.GRAY));
        }
        graphics.setComponentTooltipForNextFrame(font, lines, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0) {
            Direction direction = sideAt(event.x() - leftPos, event.y() - topPos);
            if (direction != null) {
                click(direction.ordinal());
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    private @Nullable Direction sideAt(double mouseX, double mouseY) {
        for (Direction direction : Direction.values()) {
            int[] position = SIDE_POSITIONS[direction.ordinal()];
            if (within(mouseX, mouseY, position[0], position[1], BUTTON_SIZE, BUTTON_SIZE)) {
                return direction;
            }
        }
        return null;
    }

    private ItemStack neighbourStack(Direction direction) {
        if (Minecraft.getInstance().level == null) {
            return ItemStack.EMPTY;
        }
        BlockState state = Minecraft.getInstance().level.getBlockState(menu.getPos().relative(direction));
        return state.isAir() ? ItemStack.EMPTY : new ItemStack(state.getBlock());
    }
}