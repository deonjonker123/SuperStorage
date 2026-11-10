package com.misterd.superstorage.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;

import java.util.function.Consumer;

public final class TooltipDetails {
    private TooltipDetails() {
    }

    public static void add(Consumer<Component> builder, TooltipFlag flag, String key, int lines) {
        if (!flag.hasShiftDown()) {
            builder.accept(Component.translatable("tooltip.superstorage.hold_shift", Component.translatable("tooltip.superstorage.shift").withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
            return;
        }
        for (int i = 1; i <= lines; i++) {
            builder.accept(Component.translatable("tooltip.superstorage." + key + ".info." + i).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }
    }
}