package com.misterd.superstorage.compat.jade;

import com.misterd.superstorage.block.custom.DrumBlock;
import com.misterd.superstorage.util.FluidText;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.fluids.FluidStack;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.fluid.JadeFluidObject;
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.JadeUI;
import snownee.jade.api.view.ProgressView;

public enum DrumComponentProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!(accessor.getBlock() instanceof DrumBlock drum)) {
            return;
        }
        CompoundTag data = accessor.getServerData();
        FluidStack fluid = data.contains(DrumDataProvider.FLUID_KEY) ? accessor.decodeFromNbt(FluidStack.OPTIONAL_STREAM_CODEC, data.get(DrumDataProvider.FLUID_KEY)).orElse(FluidStack.EMPTY) : FluidStack.EMPTY;
        int capacity = drum.getTier().capacity();
        ProgressView.Part part = fluid.isEmpty()
                ? ProgressView.Part.of(0.0F)
                : ProgressView.Part.of((float) fluid.getAmount() / capacity, JadeUI.fluid(JadeFluidObject.of(fluid.getFluid(), fluid.getAmount(), fluid.getComponentsPatch())));
        ProgressView view = new ProgressView(part, FluidText.bar(fluid), JadeUI.progressStyle().canDecrease(true), BoxStyle.nestedBox());
        tooltip.add(JadeUI.progress(view));
    }

    @Override
    public Identifier getUid() {
        return DrumDataProvider.UID;
    }
}