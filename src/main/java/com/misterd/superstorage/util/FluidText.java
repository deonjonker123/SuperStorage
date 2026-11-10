package com.misterd.superstorage.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.fluids.FluidStack;

import java.text.DecimalFormat;

public final class FluidText {
    private static final DecimalFormat BUCKETS = new DecimalFormat("#,##0.###");

    private FluidText() {
    }

    public static Component stored(FluidStack fluid) {
        return Component.translatable("tooltip.superstorage.fluid_stored").withStyle(ChatFormatting.LIGHT_PURPLE)
                .append(Component.translatable("tooltip.superstorage.fluid_value", fluid.getHoverName(), millibuckets(fluid.getAmount())).withStyle(ChatFormatting.WHITE));
    }

    public static Component capacity(long capacity) {
        return Component.translatable("tooltip.superstorage.fluid_capacity").withStyle(ChatFormatting.GOLD)
                .append(Component.translatable("tooltip.superstorage.fluid_amount", millibuckets(capacity)).withStyle(ChatFormatting.WHITE));
    }

    public static Component bar(FluidStack fluid) {
        if (fluid.isEmpty()) {
            return Component.translatable("tooltip.superstorage.fluid.empty").withStyle(ChatFormatting.GRAY);
        }
        return Component.translatable("jade.superstorage.fluid",
                fluid.getHoverName().copy().withStyle(ChatFormatting.WHITE),
                Component.literal(millibuckets(fluid.getAmount())).withStyle(ChatFormatting.WHITE)
        ).withStyle(ChatFormatting.WHITE);
    }

    public static String millibuckets(long millibuckets) {
        return String.format("%,d", millibuckets);
    }

    public static String buckets(long millibuckets) {
        return BUCKETS.format(millibuckets / 1000.0);
    }

    public static Component storedInfinite(FluidStack fluid) {
        return Component.translatable("tooltip.superstorage.fluid_stored").withStyle(ChatFormatting.LIGHT_PURPLE)
                .append(Component.translatable("tooltip.superstorage.fluid_value_infinite", fluid.getHoverName()).withStyle(ChatFormatting.WHITE));
    }

    public static Component capacityInfinite() {
        return Component.translatable("tooltip.superstorage.fluid_capacity").withStyle(ChatFormatting.GOLD)
                .append(Component.translatable("tooltip.superstorage.infinite").withStyle(ChatFormatting.WHITE));
    }

    public static Component barInfinite(FluidStack fluid) {
        return Component.translatable("tooltip.superstorage.fluid_value_infinite", fluid.getHoverName()).withStyle(ChatFormatting.WHITE);
    }
}