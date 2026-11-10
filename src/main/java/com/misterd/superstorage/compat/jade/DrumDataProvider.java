package com.misterd.superstorage.compat.jade;

import com.misterd.superstorage.SuperStorage;
import com.misterd.superstorage.blockentity.custom.DrumBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.fluids.FluidStack;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

public enum DrumDataProvider implements IServerDataProvider<BlockAccessor> {
    INSTANCE;

    public static final Identifier UID = Identifier.fromNamespaceAndPath(SuperStorage.MODID, "drum");
    public static final String FLUID_KEY = "fluid";

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        if (accessor.getBlockEntity() instanceof DrumBlockEntity drum) {
            data.put(FLUID_KEY, accessor.encodeAsNbt(FluidStack.OPTIONAL_STREAM_CODEC, drum.getFluid()));
        }
    }

    @Override
    public Identifier getUid() {
        return UID;
    }
}