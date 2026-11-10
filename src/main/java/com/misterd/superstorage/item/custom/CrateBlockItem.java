package com.misterd.superstorage.item.custom;

import com.misterd.superstorage.block.custom.CrateBlock;
import com.misterd.superstorage.util.ItemText;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class CrateBlockItem extends BlockItem {
    private final CrateBlock crate;

    public CrateBlockItem(CrateBlock crate, Properties properties) {
        super(crate, properties);
        this.crate = crate;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, builder, flag);
        builder.accept(ItemText.slots(crate.getTier().slotCount()));
    }
}