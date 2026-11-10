package com.misterd.superstorage.compat.jade;

import com.misterd.superstorage.SuperStorage;
import com.misterd.superstorage.blockentity.custom.SuperTankBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.fluids.FluidStack;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

public enum SuperTankDataProvider implements IServerDataProvider<BlockAccessor> {
    INSTANCE;

    public static final Identifier UID = Identifier.fromNamespaceAndPath(SuperStorage.MODID, "super_tank");
    public static final String FLUID_KEY = "fluid";

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        if (accessor.getBlockEntity() instanceof SuperTankBlockEntity tank && !tank.getStored().isEmpty() && tank.getAmount() > 0) {
            FluidStack fluid = tank.getStored().toStack((int) tank.getAmount());
            data.put(FLUID_KEY, accessor.encodeAsNbt(FluidStack.OPTIONAL_STREAM_CODEC, fluid));
        }
    }

    @Override
    public Identifier getUid() {
        return UID;
    }
}