package com.misterd.superstorage.item.custom;

import com.misterd.superstorage.block.custom.DrumBlock;
import com.misterd.superstorage.component.SSDataComponents;
import com.misterd.superstorage.util.FluidText;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;

import java.util.function.Consumer;

public class DrumBlockItem extends BlockItem {
    private final DrumBlock drum;

    public DrumBlockItem(DrumBlock drum, Properties properties) {
        super(drum, properties);
        this.drum = drum;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, builder, flag);
        FluidStack fluid = stack.getOrDefault(SSDataComponents.FLUID_CONTENT.get(), SimpleFluidContent.EMPTY).copy();
        if (!fluid.isEmpty()) {
            builder.accept(FluidText.stored(fluid));
        }
        builder.accept(FluidText.capacity(drum.getTier().capacity()));
    }
}