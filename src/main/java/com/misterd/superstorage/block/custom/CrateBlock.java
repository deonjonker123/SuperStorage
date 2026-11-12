package com.misterd.superstorage.block.custom;

import com.misterd.superstorage.gui.custom.CrateMenu;
import com.misterd.superstorage.util.CrateTier;
import com.misterd.superstorage.blockentity.custom.CrateBlockEntity;
import com.misterd.superstorage.util.MenuTitles;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import org.jspecify.annotations.Nullable;

public class CrateBlock extends BaseEntityBlock {
    public static final MapCodec<CrateBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CrateTier.CODEC.fieldOf("tier").forGetter(CrateBlock::getTier),
            propertiesCodec()
    ).apply(instance, CrateBlock::new));

    private final CrateTier tier;

    public CrateBlock(CrateTier tier, Properties properties) {
        super(properties);
        this.tier = tier;
    }

    public CrateTier getTier() {
        return tier;
    }

    @Override
    protected MapCodec<CrateBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CrateBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof CrateBlockEntity crate) {
            player.openMenu(new SimpleMenuProvider((containerId, inventory, p) -> new CrateMenu(containerId, inventory, crate), MenuTitles.of(crate, this)),
                    buf -> buf.writeVarInt(tier.ordinal()));
            level.playSound(null, pos, SoundEvents.BARREL_OPEN, SoundSource.BLOCKS, 0.5F, level.getRandom().nextFloat() * 0.1F + 0.9F);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        Containers.updateNeighboursAfterDestroy(state, level, pos);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        if (level.getBlockEntity(pos) instanceof CrateBlockEntity crate) {
            return ResourceHandlerUtil.getRedstoneSignalFromResourceHandler(crate.getItemHandler());
        }
        return 0;
    }
}