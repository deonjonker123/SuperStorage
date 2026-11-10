package com.misterd.superstorage.compat.jade;

import com.misterd.superstorage.block.custom.SuperTankBlock;
import com.misterd.superstorage.util.FluidText;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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

public enum SuperTankComponentProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!(accessor.getBlock() instanceof SuperTankBlock tank)) {
            return;
        }
        CompoundTag data = accessor.getServerData();
        FluidStack fluid = data.contains(SuperTankDataProvider.FLUID_KEY) ? accessor.decodeFromNbt(FluidStack.OPTIONAL_STREAM_CODEC, data.get(SuperTankDataProvider.FLUID_KEY)).orElse(FluidStack.EMPTY) : FluidStack.EMPTY;
        boolean creative = tank.getTier().isCreative();
        long capacity = tank.getTier().fluidCapacity();
        float ratio = creative ? 1.0F : (float) ((double) fluid.getAmount() / capacity);
        ProgressView.Part part = fluid.isEmpty()
                ? ProgressView.Part.of(0.0F)
                : ProgressView.Part.of(ratio, JadeUI.fluid(JadeFluidObject.of(fluid.getFluid(), fluid.getAmount(), fluid.getComponentsPatch())));
        Component text = creative && !fluid.isEmpty() ? FluidText.barInfinite(fluid) : FluidText.bar(fluid);
        ProgressView view = new ProgressView(part, text, JadeUI.progressStyle().canDecrease(true), BoxStyle.nestedBox());
        tooltip.add(JadeUI.progress(view));
    }

    @Override
    public Identifier getUid() {
        return SuperTankDataProvider.UID;
    }
}