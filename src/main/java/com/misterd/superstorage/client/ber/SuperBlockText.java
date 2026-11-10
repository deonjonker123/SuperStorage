package com.misterd.superstorage.client.ber;

import com.misterd.superstorage.SuperStorage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.LightCoordsUtil;

public final class SuperBlockText {
    private static final Identifier WIDGETS = Identifier.fromNamespaceAndPath(SuperStorage.MODID, "textures/gui/widgets.png");
    private static final float TEXT_SCALE = 1.0F / 64.0F;
    private static final float TEXT_BOTTOM = 4.0F / 16.0F;
    private static final float GLASS_Z = 7.0F / 16.0F + 0.005F;
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final float ICON_SIZE = 1.5F / 16.0F;
    private static final float ICON_GAP = 1.0F / 16.0F;
    private static final float ICON_RIGHT = 5.0F / 16.0F;
    private static final float ICON_TOP = 13.0F / 16.0F;
    private static final int ICON_TEXELS = 6;
    private static final int LOCK_U = 36;
    private static final int LOCK_V = 39;
    private static final int VOID_U = 36;
    private static final int VOID_V = 45;

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

    public static void submitIcons(PoseStack poseStack, SubmitNodeCollector collector, boolean locked, boolean voidOverflow) {
        if (locked) {
            submitIcon(poseStack, collector, LOCK_U, LOCK_V, ICON_TOP);
        }
        if (voidOverflow) {
            submitIcon(poseStack, collector, VOID_U, VOID_V, ICON_TOP - ICON_SIZE - ICON_GAP);
        }
    }

    private static void submitIcon(PoseStack poseStack, SubmitNodeCollector collector, int u, int v, float top) {
        float x0 = ICON_RIGHT - ICON_SIZE;
        float x1 = ICON_RIGHT;
        float y0 = top - ICON_SIZE;
        float u0 = u / 256.0F;
        float v0 = v / 256.0F;
        float u1 = (u + ICON_TEXELS) / 256.0F;
        float v1 = (v + ICON_TEXELS) / 256.0F;
        collector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(WIDGETS), (pose, buffer) -> {
            vertex(buffer, pose, x0, y0, u0, v1);
            vertex(buffer, pose, x1, y0, u1, v1);
            vertex(buffer, pose, x1, top, u1, v0);
            vertex(buffer, pose, x0, top, u0, v0);
        });
    }

    private static void vertex(VertexConsumer buffer, PoseStack.Pose pose, float x, float y, float u, float v) {
        buffer.addVertex(pose, x, y, GLASS_Z)
                .setColor(0xFFFFFFFF)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightCoordsUtil.FULL_BRIGHT)
                .setNormal(pose, 0.0F, 0.0F, 1.0F);
    }
}