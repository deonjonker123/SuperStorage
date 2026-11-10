package com.misterd.superstorage.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.fluids.FluidStack;

import java.text.DecimalFormat;

public final class FluidText {
    private static final DecimalFormat BUCKETS = new DecimalFormat("#,##0.###");

    private FluidText() {
    }

    public static Component amount(FluidStack fluid, long capacity) {
        if (fluid.isEmpty()) {
            return Component.translatable("tooltip.superstorage.fluid.empty").withStyle(ChatFormatting.RED);
        }
        return Component.translatable("tooltip.superstorage.fluid.amount",
                fluid.getHoverName().copy().withStyle(ChatFormatting.WHITE),
                Component.literal(buckets(fluid.getAmount())).withStyle(ChatFormatting.WHITE),
                Component.literal(buckets(capacity)).withStyle(ChatFormatting.WHITE)
        ).withStyle(ChatFormatting.GRAY);
    }

    public static String buckets(long millibuckets) {
        return BUCKETS.format(millibuckets / 1000.0);
    }
}