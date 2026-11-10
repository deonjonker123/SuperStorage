package com.misterd.superstorage.gui.custom;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ARGB;
import net.neoforged.neoforge.client.fluid.FluidTintSource;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

public final class FluidSprites {
    private FluidSprites() {
    }

    public static void draw(GuiGraphicsExtractor graphics, FluidResource fluid, int x, int y, int width, int height) {
        if (fluid.isEmpty()) {
            return;
        }
        FluidModel model = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(fluid.getFluid().defaultFluidState());
        TextureAtlasSprite sprite = model.stillMaterial().sprite();
        FluidTintSource tint = model.fluidTintSource();
        int color = tint == null ? -1 : ARGB.opaque(tint.colorAsStack(fluid.toStack(1000)));
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, width, height, color);
    }
}