package com.misterd.superstorage.item.custom;

import com.misterd.superstorage.block.custom.CrateBlock;
import com.misterd.superstorage.component.PackedCrate;
import com.misterd.superstorage.component.SSDataComponents;
import com.misterd.superstorage.util.ItemText;
import net.minecraft.ChatFormatting;
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
        PackedCrate packed = stack.get(SSDataComponents.PACKED_CRATE.get());
        if (packed != null) {
            builder.accept(Component.translatable("tooltip.superstorage.packed", packed.items().size()).withStyle(ChatFormatting.GOLD));
            int upgrades = packed.upgradeCount();
            if (upgrades > 0) {
                builder.accept(Component.translatable("tooltip.superstorage.packed_upgrades", upgrades).withStyle(ChatFormatting.GOLD));
            }
        }
        builder.accept(ItemText.slots(crate.getTier().slotCount()));
    }

    @Override
    public boolean canFitInsideContainerItems(ItemStack stack) {
        return !stack.has(SSDataComponents.PACKED_CRATE.get());
    }
}