package com.misterd.superstorage.component;

import com.misterd.superstorage.SuperStorage;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SSDataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, SuperStorage.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SimpleFluidContent>> FLUID_CONTENT = DATA_COMPONENTS.registerComponentType("fluid_content",
            builder -> builder.persistent(SimpleFluidContent.CODEC).networkSynchronized(SimpleFluidContent.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SuperChestContents>> SUPER_CHEST_CONTENTS = DATA_COMPONENTS.registerComponentType("super_chest_contents",
            builder -> builder.persistent(SuperChestContents.CODEC).networkSynchronized(SuperChestContents.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SuperSettings>> SUPER_SETTINGS = DATA_COMPONENTS.registerComponentType("super_settings",
            builder -> builder.persistent(SuperSettings.CODEC).networkSynchronized(SuperSettings.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SuperTankContents>> SUPER_TANK_CONTENTS = DATA_COMPONENTS.registerComponentType("super_tank_contents",
            builder -> builder.persistent(SuperTankContents.CODEC).networkSynchronized(SuperTankContents.STREAM_CODEC));

    public static void register(IEventBus eventBus) {
        DATA_COMPONENTS.register(eventBus);
    }
}