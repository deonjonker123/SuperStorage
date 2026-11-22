package com.misterd.superstorage.item.custom;

import com.misterd.superstorage.component.FilterSettings;
import com.misterd.superstorage.gui.custom.FilterMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class FilterItem extends Item {
    public FilterItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide()) {
            ItemStack stack = player.getItemInHand(hand);
            player.openMenu(new SimpleMenuProvider((containerId, inventory, p) -> new FilterMenu(containerId, inventory, hand), stack.getHoverName()),
                    buf -> buf.writeEnum(hand));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, builder, flag);
        FilterSettings settings = FilterSettings.of(stack);
        builder.accept(Component.translatable(settings.blacklist() ? "gui.superstorage.filter.blacklist" : "gui.superstorage.filter.whitelist").withStyle(ChatFormatting.GRAY));
        builder.accept(Component.translatable("tooltip.superstorage.filter.entries", settings.count()).withStyle(ChatFormatting.GRAY));
    }
}