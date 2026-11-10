package com.misterd.superstorage.block.custom;

import com.misterd.superstorage.blockentity.SSBlockEntities;
import com.misterd.superstorage.blockentity.custom.SuperChestBlockEntity;
import com.misterd.superstorage.gui.custom.SuperChestMenu;
import com.misterd.superstorage.util.SuperTier;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

public class SuperChestBlock extends BaseEntityBlock {
    public static final MapCodec<SuperChestBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SuperTier.CODEC.fieldOf("tier").forGetter(SuperChestBlock::getTier),
            propertiesCodec()
    ).apply(instance, SuperChestBlock::new));
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    private final SuperTier tier;

    public SuperChestBlock(SuperTier tier, Properties properties) {
        super(properties);
        this.tier = tier;
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(type, SSBlockEntities.SUPER_CHEST_BE.get(), SuperChestBlockEntity::serverTick);
    }

    public SuperTier getTier() {
        return tier;
    }

    @Override
    protected MapCodec<SuperChestBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SuperChestBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (hitResult.getDirection() != state.getValue(FACING) || !(level.getBlockEntity(pos) instanceof SuperChestBlockEntity chest) || !chest.accepts(stack)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        if (!level.isClientSide()) {
            chest.insertFromHand(player, stack);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide() || !(level.getBlockEntity(pos) instanceof SuperChestBlockEntity chest)) {
            return InteractionResult.SUCCESS;
        }
        boolean front = hitResult.getDirection() == state.getValue(FACING);
        if (front && player.getMainHandItem().isEmpty() && chest.tryDoubleClickInsert(player)) {
            return InteractionResult.SUCCESS;
        }
        player.openMenu(new SimpleMenuProvider((containerId, inventory, p) -> new SuperChestMenu(containerId, inventory, chest), getName()),
                buf -> buf.writeBlockPos(pos));
        return InteractionResult.SUCCESS;
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData, Player player) {
        if (!player.level().isClientSide() && !player.isCreative() && isLookingAtFront(state, pos, player)
                && level.getBlockEntity(pos) instanceof SuperChestBlockEntity chest && !chest.getStored().isEmpty() && chest.getCount() > 0) {
            ItemStack extracted = chest.extract(chest.getStored().getMaxStackSize());
            ItemStack picked = extracted.copy();
            player.getInventory().add(extracted);
            if (!extracted.isEmpty()) {
                chest.insert(extracted);
            }
            return picked;
        }
        return super.getCloneItemStack(level, pos, state, includeData, player);
    }

    @Override
    protected void attack(BlockState state, Level level, BlockPos pos, Player player) {
        if (level.isClientSide() || !isLookingAtFront(state, pos, player) || !(level.getBlockEntity(pos) instanceof SuperChestBlockEntity chest)) {
            return;
        }
        ItemStack extracted = chest.extract(1);
        if (!extracted.isEmpty() && !player.getInventory().add(extracted)) {
            player.drop(extracted, false);
        }
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, ItemStack toolStack, boolean willHarvest, FluidState fluid) {
        if (player.isCreative() && isLookingAtFront(state, pos, player)) {
            return false;
        }
        return super.onDestroyedByPlayer(state, level, pos, player, toolStack, willHarvest, fluid);
    }

    private boolean isLookingAtFront(BlockState state, BlockPos pos, Player player) {
        return player.pick(player.blockInteractionRange(), 1.0F, false) instanceof BlockHitResult hit
                && hit.getBlockPos().equals(pos)
                && hit.getDirection() == state.getValue(FACING);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        return level.getBlockEntity(pos) instanceof SuperChestBlockEntity chest ? chest.getComparatorSignal() : 0;
    }
}