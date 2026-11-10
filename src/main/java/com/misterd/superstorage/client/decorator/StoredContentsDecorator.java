package com.misterd.superstorage.client.decorator;

import com.misterd.superstorage.component.SSDataComponents;
import com.misterd.superstorage.component.SuperChestContents;
import com.misterd.superstorage.component.SuperTankContents;
import com.misterd.superstorage.gui.custom.FluidSprites;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.IItemDecorator;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

public enum StoredContentsDecorator implements IItemDecorator {
    INSTANCE;

    private static final int OFFSET = 8;
    private static final int SIZE = 8;
    private static final float ITEM_SCALE = 0.5F;

    @Override
    public boolean render(GuiGraphicsExtractor graphics, Font font, ItemStack stack, int xOffset, int yOffset) {
        SuperChestContents chest = stack.get(SSDataComponents.SUPER_CHEST_CONTENTS.get());
        if (chest != null && !chest.item().isEmpty()) {
            graphics.nextStratum();
            graphics.pose().pushMatrix();
            graphics.pose().translate(xOffset + OFFSET, yOffset + OFFSET);
            graphics.pose().scale(ITEM_SCALE, ITEM_SCALE);
            graphics.item(chest.item().toStack(), 0, 0);
            graphics.pose().popMatrix();
            return true;
        }
        FluidResource fluid = fluid(stack);
        if (!fluid.isEmpty()) {
            graphics.nextStratum();
            FluidSprites.draw(graphics, fluid, xOffset + OFFSET, yOffset + OFFSET, SIZE, SIZE);
            return true;
        }
        return false;
    }

    private static FluidResource fluid(ItemStack stack) {
        SuperTankContents tank = stack.get(SSDataComponents.SUPER_TANK_CONTENTS.get());
        if (tank != null && tank.amount() > 0) {
            return tank.fluid();
        }
        FluidStack drum = stack.getOrDefault(SSDataComponents.FLUID_CONTENT.get(), SimpleFluidContent.EMPTY).copy();
        return drum.isEmpty() ? FluidResource.EMPTY : FluidResource.of(drum);
    }
}