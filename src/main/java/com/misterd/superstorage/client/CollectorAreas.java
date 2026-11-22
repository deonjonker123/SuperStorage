package com.misterd.superstorage.client;

import com.misterd.superstorage.SuperStorage;
import com.misterd.superstorage.blockentity.custom.CrateBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = SuperStorage.MODID, value = Dist.CLIENT)
public final class CollectorAreas {
    private static final int LINE_COLOR = 0xFF4CCCFF;
    private static final float LINE_WIDTH = 2.0F;

    private static final Map<BlockPos, AABB> AREAS = new HashMap<>();

    private CollectorAreas() {
    }

    public static boolean isShown(BlockPos pos) {
        return AREAS.containsKey(pos);
    }

    public static void toggle(BlockPos pos, AABB area) {
        if (AREAS.remove(pos) == null) {
            AREAS.put(pos.immutable(), area);
        }
    }

    public static void update(BlockPos pos, AABB area) {
        AREAS.computeIfPresent(pos, (key, old) -> area);
    }

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel().isClientSide()) {
            AREAS.clear();
        }
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent.AfterTranslucentBlocks event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (AREAS.isEmpty() || minecraft.level == null) {
            return;
        }
        AREAS.keySet().removeIf(pos -> !(minecraft.level.getBlockEntity(pos) instanceof CrateBlockEntity));
        Vec3 camera = minecraft.gameRenderer.getMainCamera().position();
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource buffers = minecraft.renderBuffers().bufferSource();
        VertexConsumer lines = buffers.getBuffer(RenderTypes.lines());
        for (AABB area : AREAS.values()) {
            ShapeRenderer.renderShape(poseStack, lines, Shapes.create(area), -camera.x, -camera.y, -camera.z, LINE_COLOR, LINE_WIDTH);
        }
        buffers.endBatch(RenderTypes.lines());
    }
}