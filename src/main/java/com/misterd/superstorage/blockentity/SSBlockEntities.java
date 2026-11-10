package com.misterd.superstorage.blockentity;

import com.misterd.superstorage.SuperStorage;
import com.misterd.superstorage.block.SSBlocks;
import com.misterd.superstorage.blockentity.custom.CrateBlockEntity;
import com.misterd.superstorage.blockentity.custom.DrumBlockEntity;
import com.misterd.superstorage.blockentity.custom.SuperChestBlockEntity;
import com.misterd.superstorage.blockentity.custom.SuperTankBlockEntity;
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

    public static final Supplier<BlockEntityType<DrumBlockEntity>> DRUM_BE = BLOCK_ENTITIES.register("drum_be",
            () -> new BlockEntityType<>(DrumBlockEntity::new,
                    SSBlocks.WOODEN_DRUM.get(),
                    SSBlocks.COPPER_DRUM.get(),
                    SSBlocks.IRON_DRUM.get(),
                    SSBlocks.GOLD_DRUM.get(),
                    SSBlocks.EMERALD_DRUM.get(),
                    SSBlocks.DIAMOND_DRUM.get(),
                    SSBlocks.NETHERITE_DRUM.get()));

    public static final Supplier<BlockEntityType<SuperChestBlockEntity>> SUPER_CHEST_BE = BLOCK_ENTITIES.register("super_chest_be",
            () -> new BlockEntityType<>(SuperChestBlockEntity::new,
                    SSBlocks.SUPER_CHEST_I.get(),
                    SSBlocks.SUPER_CHEST_II.get(),
                    SSBlocks.SUPER_CHEST_III.get(),
                    SSBlocks.SUPER_CHEST_IV.get(),
                    SSBlocks.CREATIVE_SUPER_CHEST.get()));

    public static final Supplier<BlockEntityType<SuperTankBlockEntity>> SUPER_TANK_BE = BLOCK_ENTITIES.register("super_tank_be",
            () -> new BlockEntityType<>(SuperTankBlockEntity::new,
                    SSBlocks.SUPER_TANK_I.get(),
                    SSBlocks.SUPER_TANK_II.get(),
                    SSBlocks.SUPER_TANK_III.get(),
                    SSBlocks.SUPER_TANK_IV.get(),
                    SSBlocks.CREATIVE_SUPER_TANK.get()));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}