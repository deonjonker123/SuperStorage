package com.misterd.superstorage.compat.jade;

import com.misterd.superstorage.SuperStorage;
import com.misterd.superstorage.blockentity.custom.SuperBlockEntity;
import com.misterd.superstorage.util.RelativeSide;
import com.misterd.superstorage.util.SideMode;
import com.misterd.superstorage.util.SuperConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import java.util.function.Predicate;

public enum SuperConfigComponentProvider implements IBlockComponentProvider {
    INSTANCE;

    private static final Identifier UID = Identifier.fromNamespaceAndPath(SuperStorage.MODID, "super_config");

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!(accessor.getBlockEntity() instanceof SuperBlockEntity superBlock)) {
            return;
        }
        SuperConfig settings = superBlock.getConfig();
        tooltip.add(Component.translatable("jade.superstorage.locked", yesNo(settings.isLocked())).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("jade.superstorage.void", yesNo(settings.isVoidOverflow())).withStyle(ChatFormatting.GRAY));
        if (!accessor.showDetails()) {
            return;
        }
        if (!settings.isCapabilitiesEnabled()) {
            tooltip.add(Component.translatable("jade.superstorage.automation_disabled").withStyle(ChatFormatting.GRAY));
            return;
        }
        Component inputs = sides(settings, SideMode::canInput, ChatFormatting.GREEN);
        if (inputs != null) {
            tooltip.add(Component.translatable("jade.superstorage.input", inputs).withStyle(ChatFormatting.GRAY));
        }
        Component outputs = sides(settings, SideMode::canOutput, ChatFormatting.RED);
        if (outputs != null) {
            tooltip.add(Component.translatable("jade.superstorage.output", outputs).withStyle(ChatFormatting.GRAY));
        }
    }

    private static Component yesNo(boolean value) {
        return Component.translatable(value ? "jade.superstorage.yes" : "jade.superstorage.no").withStyle(value ? ChatFormatting.GREEN : ChatFormatting.RED);
    }

    private static Component sides(SuperConfig settings, Predicate<SideMode> filter, ChatFormatting color) {
        MutableComponent result = null;
        for (RelativeSide side : RelativeSide.values()) {
            if (!filter.test(settings.getSide(side))) {
                continue;
            }
            Component name = Component.translatable("jade.superstorage.side." + side.name().toLowerCase()).withStyle(color);
            if (result == null) {
                result = Component.empty().append(name);
            } else {
                result.append(Component.literal(", ").withStyle(ChatFormatting.GRAY)).append(name);
            }
        }
        return result;
    }

    @Override
    public Identifier getUid() {
        return UID;
    }

    @Override
    public int getDefaultPriority() {
        return 5000;
    }
}