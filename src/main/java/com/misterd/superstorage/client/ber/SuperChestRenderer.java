package com.misterd.superstorage.client.ber;

import com.misterd.superstorage.block.custom.SuperChestBlock;
import com.misterd.superstorage.blockentity.custom.SuperChestBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class SuperChestRenderer implements BlockEntityRenderer<SuperChestBlockEntity, SuperChestRenderState> {
    private static final float ITEM_Y = 8.0F / 16.0F;
    private static final float ITEM_Z = 1.0F / 16.0F;
    private static final float ITEM_SCALE = 0.5F;
    private static final float DEGREES_PER_TICK = 4.0F;

    private final ItemModelResolver itemModelResolver;
    private final Font font;

    public SuperChestRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
        this.font = context.font();
    }

    @Override
    public SuperChestRenderState createRenderState() {
        return new SuperChestRenderState();
    }

    @Override
    public void extractRenderState(SuperChestBlockEntity chest, SuperChestRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(chest, state, partialTicks, cameraPosition, breakProgress);
        state.facing = chest.getBlockState().getValue(SuperChestBlock.FACING);
        state.visible = !chest.getStored().isEmpty();
        if (!state.visible) {
            state.item.clear();
            return;
        }
        state.count = CountFormat.items(chest.getCount());
        state.time = chest.getLevel() == null ? 0 : chest.getLevel().getGameTime() + partialTicks;
        itemModelResolver.updateForTopItem(state.item, chest.getStored().toStack(), ItemDisplayContext.FIXED, chest.getLevel(), null, (int) chest.getBlockPos().asLong());
    }

    @Override
    public void submit(SuperChestRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (!state.visible) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.0F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-state.facing.toYRot()));
        if (!state.item.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.0F, ITEM_Y, ITEM_Z);
            poseStack.mulPose(Axis.YP.rotationDegrees(state.time * DEGREES_PER_TICK));
            poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);
            state.item.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
        SuperBlockText.submitCount(poseStack, collector, font, state.count);
        poseStack.popPose();
    }
}