package com.misterd.superstorage.client.ber;

import com.misterd.superstorage.block.custom.SuperTankBlock;
import com.misterd.superstorage.blockentity.custom.SuperTankBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ARGB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.fluid.FluidTintSource;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import org.jspecify.annotations.Nullable;

public class SuperTankRenderer implements BlockEntityRenderer<SuperTankBlockEntity, SuperTankRenderState> {
    private static final float INSET = 0.002F;
    private static final float MIN_X = -6.0F / 16.0F + INSET;
    private static final float MAX_X = 6.0F / 16.0F - INSET;
    private static final float MIN_Z = -6.0F / 16.0F + INSET;
    private static final float MAX_Z = 7.0F / 16.0F - INSET;
    private static final float MIN_Y = 2.0F / 16.0F + INSET;
    private static final float MAX_Y = 14.0F / 16.0F - INSET;

    private final Font font;

    public SuperTankRenderer(BlockEntityRendererProvider.Context context) {
        this.font = context.font();
    }

    @Override
    public SuperTankRenderState createRenderState() {
        return new SuperTankRenderState();
    }

    @Override
    public void extractRenderState(SuperTankBlockEntity tank, SuperTankRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(tank, state, partialTicks, cameraPosition, breakProgress);
        state.facing = tank.getBlockState().getValue(SuperTankBlock.FACING);
        FluidResource fluid = tank.getStored();
        state.visible = !fluid.isEmpty();
        state.sprite = null;
        if (!state.visible) {
            return;
        }
        state.count = CountFormat.buckets(tank.getAmount());
        state.fill = tank.getCapacity() == 0 ? 0 : (float) Math.min(1.0, (double) tank.getAmount() / tank.getCapacity());
        if (state.fill > 0) {
            FluidModel model = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(fluid.getFluid().defaultFluidState());
            state.sprite = model.stillMaterial().sprite();
            FluidTintSource tint = model.fluidTintSource();
            state.color = tint == null ? -1 : ARGB.opaque(tint.colorAsStack(fluid.toStack(1000)));
        }
    }

    @Override
    public void submit(SuperTankRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (!state.visible) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.0F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-state.facing.toYRot()));
        TextureAtlasSprite sprite = state.sprite;
        if (sprite != null) {
            float top = MIN_Y + (MAX_Y - MIN_Y) * state.fill;
            int color = state.color;
            int light = state.lightCoords;
            collector.submitCustomGeometry(poseStack, RenderTypes.entityTranslucent(TextureAtlas.LOCATION_BLOCKS), (pose, buffer) -> {
                float u0 = sprite.getU0();
                float u1 = sprite.getU1();
                float v0 = sprite.getV0();
                float v1 = sprite.getV1();
                float vSide = v0 + (v1 - v0) * (top - MIN_Y);
                quad(buffer, pose, light, color, 0, 1, 0, MIN_X, top, MIN_Z, MIN_X, top, MAX_Z, MAX_X, top, MAX_Z, MAX_X, top, MIN_Z, u0, v0, u1, v1);
                quad(buffer, pose, light, color, 0, 0, 1, MIN_X, MIN_Y, MAX_Z, MAX_X, MIN_Y, MAX_Z, MAX_X, top, MAX_Z, MIN_X, top, MAX_Z, u0, vSide, u1, v0);
                quad(buffer, pose, light, color, 0, 0, -1, MAX_X, MIN_Y, MIN_Z, MIN_X, MIN_Y, MIN_Z, MIN_X, top, MIN_Z, MAX_X, top, MIN_Z, u0, vSide, u1, v0);
                quad(buffer, pose, light, color, 1, 0, 0, MAX_X, MIN_Y, MAX_Z, MAX_X, MIN_Y, MIN_Z, MAX_X, top, MIN_Z, MAX_X, top, MAX_Z, u0, vSide, u1, v0);
                quad(buffer, pose, light, color, -1, 0, 0, MIN_X, MIN_Y, MIN_Z, MIN_X, MIN_Y, MAX_Z, MIN_X, top, MAX_Z, MIN_X, top, MIN_Z, u0, vSide, u1, v0);
            });
        }
        SuperBlockText.submitCount(poseStack, collector, font, state.count);
        poseStack.popPose();
    }

    private static void quad(VertexConsumer buffer, PoseStack.Pose pose, int light, int color, float nx, float ny, float nz,
                             float x0, float y0, float z0, float x1, float y1, float z1,
                             float x2, float y2, float z2, float x3, float y3, float z3,
                             float uStart, float vStart, float uEnd, float vEnd) {
        vertex(buffer, pose, light, color, nx, ny, nz, x0, y0, z0, uStart, vStart);
        vertex(buffer, pose, light, color, nx, ny, nz, x1, y1, z1, uEnd, vStart);
        vertex(buffer, pose, light, color, nx, ny, nz, x2, y2, z2, uEnd, vEnd);
        vertex(buffer, pose, light, color, nx, ny, nz, x3, y3, z3, uStart, vEnd);
    }

    private static void vertex(VertexConsumer buffer, PoseStack.Pose pose, int light, int color, float nx, float ny, float nz, float x, float y, float z, float u, float v) {
        buffer.addVertex(pose, x, y, z)
                .setColor(color)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, nx, ny, nz);
    }
}