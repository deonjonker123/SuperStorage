package com.misterd.superstorage.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueInput;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TierKitItem extends Item {
    private final List<Upgrade> upgrades;

    public TierKitItem(Properties properties, Upgrade... upgrades) {
        super(properties);
        this.upgrades = List.of(upgrades);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Block target = targetFor(state.getBlock());
        if (target == null) {
            return InteractionResult.PASS;
        }
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }
        BlockEntity current = level.getBlockEntity(pos);
        if (current == null) {
            return InteractionResult.PASS;
        }
        CompoundTag data = current.saveWithoutMetadata(level.registryAccess());
        BlockState upgradedState = target.withPropertiesOf(state);
        level.setBlock(pos, upgradedState, Block.UPDATE_ALL | Block.UPDATE_SKIP_BLOCK_ENTITY_SIDEEFFECTS);
        BlockEntity upgraded = level.getBlockEntity(pos);
        if (upgraded != null) {
            upgraded.loadWithComponents(TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), data));
            upgraded.setChanged();
            level.sendBlockUpdated(pos, upgradedState, upgradedState, Block.UPDATE_CLIENTS);
        }
        for (ServerPlayer viewer : serverLevel.players()) {
            if (!viewer.containerMenu.stillValid(viewer)) {
                viewer.closeContainer();
            }
        }
        level.playSound(null, pos, SoundEvents.SMITHING_TABLE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
        Player player = context.getPlayer();
        if (player == null || !player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return InteractionResult.SUCCESS;
    }

    private @Nullable Block targetFor(Block block) {
        for (Upgrade upgrade : upgrades) {
            if (upgrade.from().get() == block) {
                return upgrade.to().get();
            }
        }
        return null;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, builder, flag);
        for (Upgrade upgrade : upgrades) {
            builder.accept(Component.translatable("tooltip.superstorage.kit", upgrade.from().get().getName(), upgrade.to().get().getName()).withStyle(ChatFormatting.GRAY));
        }
    }

    public record Upgrade(Supplier<? extends Block> from, Supplier<? extends Block> to) {
    }
}