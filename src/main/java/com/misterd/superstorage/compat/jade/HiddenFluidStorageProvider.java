package com.misterd.superstorage.compat.jade;

import com.misterd.superstorage.SuperStorage;
import net.minecraft.resources.Identifier;
import snownee.jade.api.Accessor;
import snownee.jade.api.view.FluidView;
import snownee.jade.api.view.IServerExtensionProvider;
import snownee.jade.api.view.ViewGroup;

import java.util.List;

public enum HiddenFluidStorageProvider implements IServerExtensionProvider<FluidView.Data> {
    INSTANCE;

    private static final Identifier UID = Identifier.fromNamespaceAndPath(SuperStorage.MODID, "hidden_fluid_storage");

    @Override
    public List<ViewGroup<FluidView.Data>> getGroups(Accessor<?> accessor) {
        return List.of();
    }

    @Override
    public Identifier getUid() {
        return UID;
    }
}