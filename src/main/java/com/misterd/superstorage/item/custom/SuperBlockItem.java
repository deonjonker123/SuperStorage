package com.misterd.superstorage.item.custom;

import com.misterd.superstorage.block.custom.SuperChestBlock;
import com.misterd.superstorage.block.custom.SuperTankBlock;
import com.misterd.superstorage.component.SSDataComponents;
import com.misterd.superstorage.component.SuperChestContents;
import com.misterd.superstorage.component.SuperTankContents;
import com.misterd.superstorage.util.FluidText;
import com.misterd.superstorage.util.ItemText;
import com.misterd.superstorage.util.TooltipDetails;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class SuperBlockItem extends BlockItem {
    public SuperBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, builder, flag);
        if (getBlock() instanceof SuperChestBlock chest && chest.getTier().isCreative()) {
            SuperChestContents contents = stack.get(SSDataComponents.SUPER_CHEST_CONTENTS.get());
            if (contents != null && !contents.item().isEmpty()) {
                builder.accept(ItemText.storedInfinite(contents.item()));
            }
            builder.accept(ItemText.capacityInfinite());
        } else if (getBlock() instanceof SuperTankBlock tank && tank.getTier().isCreative()) {
            SuperTankContents contents = stack.get(SSDataComponents.SUPER_TANK_CONTENTS.get());
            if (contents != null && !contents.fluid().isEmpty()) {
                builder.accept(FluidText.storedInfinite(contents.fluid().toStack(1000)));
            }
            builder.accept(FluidText.capacityInfinite());
        } else if (getBlock() instanceof SuperChestBlock chest) {
            SuperChestContents contents = stack.get(SSDataComponents.SUPER_CHEST_CONTENTS.get());
            if (contents != null && !contents.item().isEmpty() && contents.count() > 0) {
                builder.accept(ItemText.stored(contents.item(), contents.count()));
            }
            builder.accept(ItemText.capacity(chest.getTier().itemCapacity()));
            TooltipDetails.add(builder, flag, "super_chest", 5);
        } else if (getBlock() instanceof SuperTankBlock tank) {
            SuperTankContents contents = stack.get(SSDataComponents.SUPER_TANK_CONTENTS.get());
            if (contents != null && !contents.fluid().isEmpty() && contents.amount() > 0) {
                builder.accept(FluidText.stored(contents.fluid().toStack((int) contents.amount())));
            }
            builder.accept(FluidText.capacity(tank.getTier().fluidCapacity()));
            TooltipDetails.add(builder, flag, "super_tank", 3);
        }
    }
}