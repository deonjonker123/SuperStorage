package com.misterd.superstorage.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.transfer.item.ItemResource;

public final class ItemText {
    private ItemText() {
    }

    public static Component slots(int slots) {
        return Component.translatable("tooltip.superstorage.item_slots").withStyle(ChatFormatting.GOLD)
                .append(Component.literal(String.format("%,d", slots)).withStyle(ChatFormatting.WHITE));
    }

    public static Component stored(ItemResource item, long count) {
        return Component.translatable("tooltip.superstorage.item_stored").withStyle(ChatFormatting.LIGHT_PURPLE)
                .append(Component.translatable("tooltip.superstorage.item_value", item.toStack().getHoverName(), String.format("%,d", count)).withStyle(ChatFormatting.WHITE));
    }

    public static Component capacity(long capacity) {
        return Component.translatable("tooltip.superstorage.item_capacity").withStyle(ChatFormatting.GOLD)
                .append(Component.literal(String.format("%,d", capacity)).withStyle(ChatFormatting.WHITE));
    }
}