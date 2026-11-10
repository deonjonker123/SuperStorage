package com.misterd.superstorage.blockentity;

import com.misterd.superstorage.SuperStorage;
import com.misterd.superstorage.block.SSBlocks;
import com.misterd.superstorage.blockentity.custom.CrateBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SSBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, SuperStorage.MODID);

    public static final Supplier<BlockEntityType<CrateBlockEntity>> CRATE_BE = BLOCK_ENTITIES.register("crate_be",
            () -> new BlockEntityType<>(CrateBlockEntity::new,
                    SSBlocks.WOODEN_CRATE.get(),
                    SSBlocks.COPPER_CRATE.get(),
                    SSBlocks.IRON_CRATE.get(),
                    SSBlocks.GOLD_CRATE.get(),
                    SSBlocks.EMERALD_CRATE.get(),
                    SSBlocks.DIAMOND_CRATE.get(),
                    SSBlocks.NETHERITE_CRATE.get()));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}