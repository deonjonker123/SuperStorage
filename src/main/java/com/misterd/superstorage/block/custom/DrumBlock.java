package com.misterd.superstorage.block.custom;

import com.misterd.superstorage.blockentity.custom.DrumBlockEntity;
import com.misterd.superstorage.util.DrumTier;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import org.jspecify.annotations.Nullable;

public class DrumBlock extends BaseEntityBlock {
    public static final MapCodec<DrumBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DrumTier.CODEC.fieldOf("tier").forGetter(DrumBlock::getTier),
            propertiesCodec()
    ).apply(instance, DrumBlock::new));

    private final DrumTier tier;

    public DrumBlock(DrumTier tier, Properties properties) {
        super(properties);
        this.tier = tier;
    }

    public DrumTier getTier() {
        return tier;
    }

    @Override
    protected MapCodec<DrumBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DrumBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof DrumBlockEntity drum)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        if (level.isClientSide()) {
            return ItemAccess.forStack(stack).getCapability(Capabilities.Fluid.ITEM) != null ? InteractionResult.SUCCESS : InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        return FluidUtil.interactWithFluidHandler(player, hand, pos, drum.getFluidHandler(), null) ? InteractionResult.SUCCESS : InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        if (level.getBlockEntity(pos) instanceof DrumBlockEntity drum) {
            return ResourceHandlerUtil.getRedstoneSignalFromResourceHandler(drum.getFluidHandler());
        }
        return 0;
    }
}