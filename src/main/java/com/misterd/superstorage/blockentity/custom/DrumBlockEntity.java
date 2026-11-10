package com.misterd.superstorage.blockentity.custom;

import com.misterd.superstorage.block.custom.DrumBlock;
import com.misterd.superstorage.blockentity.SSBlockEntities;
import com.misterd.superstorage.component.SSDataComponents;
import com.misterd.superstorage.util.DrumTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;

public class DrumBlockEntity extends BlockEntity {
    private static final String TANK_KEY = "tank";

    private final DrumTier tier;
    private final DrumFluidHandler fluidHandler;

    public DrumBlockEntity(BlockPos pos, BlockState state) {
        super(SSBlockEntities.DRUM_BE.get(), pos, state);
        this.tier = state.getBlock() instanceof DrumBlock drum ? drum.getTier() : DrumTier.WOODEN;
        this.fluidHandler = new DrumFluidHandler(tier.capacity());
    }

    public DrumTier getTier() {
        return tier;
    }

    public FluidStacksResourceHandler getFluidHandler() {
        return fluidHandler;
    }

    public FluidStack getFluid() {
        return fluidHandler.copyToList().getFirst();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        fluidHandler.serialize(output.child(TANK_KEY));
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        fluidHandler.deserialize(input.childOrEmpty(TANK_KEY));
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);
        FluidStack stack = components.getOrDefault(SSDataComponents.FLUID_CONTENT.get(), SimpleFluidContent.EMPTY).copy();
        if (stack.isEmpty()) {
            fluidHandler.set(0, FluidResource.EMPTY, 0);
        } else {
            fluidHandler.set(0, FluidResource.of(stack), Math.min(stack.getAmount(), tier.capacity()));
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        FluidStack stack = getFluid();
        if (!stack.isEmpty()) {
            components.set(SSDataComponents.FLUID_CONTENT.get(), SimpleFluidContent.copyOf(stack));
        }
    }

    private class DrumFluidHandler extends FluidStacksResourceHandler {
        DrumFluidHandler(int capacity) {
            super(1, capacity);
        }

        @Override
        protected void onContentsChanged(int index, FluidStack previousContents) {
            setChanged();
        }
    }
}