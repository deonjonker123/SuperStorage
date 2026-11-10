package com.misterd.superstorage.block.custom;

import com.misterd.superstorage.blockentity.SSBlockEntities;
import com.misterd.superstorage.blockentity.custom.SuperTankBlockEntity;
import com.misterd.superstorage.gui.custom.SuperTankMenu;
import com.misterd.superstorage.util.FluidTransfer;
import com.misterd.superstorage.util.MenuTitles;
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
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import org.jspecify.annotations.Nullable;

public class SuperTankBlock extends BaseEntityBlock {
    public static final MapCodec<SuperTankBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SuperTier.CODEC.fieldOf("tier").forGetter(SuperTankBlock::getTier),
            propertiesCodec()
    ).apply(instance, SuperTankBlock::new));
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    private final SuperTier tier;

    public SuperTankBlock(SuperTier tier, Properties properties) {
        super(properties);
        this.tier = tier;
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public SuperTier getTier() {
        return tier;
    }

    @Override
    protected MapCodec<SuperTankBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SuperTankBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(type, SSBlockEntities.SUPER_TANK_BE.get(), SuperTankBlockEntity::serverTick);
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
        if (stack.isEmpty() || hitResult.getDirection() != state.getValue(FACING) || !(level.getBlockEntity(pos) instanceof SuperTankBlockEntity tank)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        ResourceHandler<FluidResource> item = ItemAccess.forPlayerInteraction(player, hand).oneByOne().getCapability(Capabilities.Fluid.ITEM);
        if (tank.isCreative()) {
            FluidResource fluid = FluidTransfer.firstFluid(item);
            if (!fluid.isEmpty()) {
                tank.setCreativeFluid(fluid);
                return InteractionResult.SUCCESS;
            }
        }
        if (!FluidTransfer.moveBucket(tank.getStorage(), item, level, pos, player)) {
            openMenu(player, pos, tank);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof SuperTankBlockEntity tank) {
            openMenu(player, pos, tank);
        }
        return InteractionResult.SUCCESS;
    }

    private void openMenu(Player player, BlockPos pos, SuperTankBlockEntity tank) {
        player.openMenu(new SimpleMenuProvider((containerId, inventory, p) -> new SuperTankMenu(containerId, inventory, tank), MenuTitles.of(tank, this)),
                buf -> buf.writeBlockPos(pos));
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        return level.getBlockEntity(pos) instanceof SuperTankBlockEntity tank ? tank.getComparatorSignal() : 0;
    }
}