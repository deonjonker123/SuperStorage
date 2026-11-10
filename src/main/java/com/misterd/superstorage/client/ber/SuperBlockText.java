package com.misterd.superstorage.client.ber;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.LightCoordsUtil;

public final class SuperBlockText {
    private static final float TEXT_SCALE = 1.0F / 64.0F;
    private static final float TEXT_BOTTOM = 4.0F / 16.0F;
    private static final float GLASS_Z = 7.0F / 16.0F + 0.005F;
    private static final int TEXT_COLOR = 0xFFFFFFFF;

    private SuperBlockText() {
    }

    public static void submitCount(PoseStack poseStack, SubmitNodeCollector collector, Font font, String text) {
        FormattedCharSequence sequence = Component.literal(text).getVisualOrderText();
        poseStack.pushPose();
        poseStack.translate(0.0F, TEXT_BOTTOM + font.lineHeight * TEXT_SCALE, GLASS_Z);
        poseStack.scale(TEXT_SCALE, -TEXT_SCALE, TEXT_SCALE);
        collector.submitText(poseStack, -font.width(sequence) / 2.0F, 0.0F, sequence, false, Font.DisplayMode.POLYGON_OFFSET, LightCoordsUtil.FULL_BRIGHT, TEXT_COLOR, 0, 0);
        poseStack.popPose();
    }
}