package com.misterd.superstorage.block;

import com.misterd.superstorage.SuperStorage;
import com.misterd.superstorage.block.custom.CrateBlock;
import com.misterd.superstorage.block.custom.DrumBlock;
import com.misterd.superstorage.util.CrateTier;
import com.misterd.superstorage.util.DrumTier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

public class SSBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SuperStorage.MODID);

    public static final DeferredBlock<CrateBlock> WOODEN_CRATE = registerBlock("wooden_crate",
            id -> new CrateBlock(CrateTier.WOODEN, BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, id))
                    .strength(2F, 3F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.WOOD)));

    public static final DeferredBlock<CrateBlock> COPPER_CRATE = registerBlock("copper_crate",
            id -> new CrateBlock(CrateTier.COPPER, BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, id))
                    .strength(3F, 50F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)));

    public static final DeferredBlock<CrateBlock> IRON_CRATE = registerBlock("iron_crate",
            id -> new CrateBlock(CrateTier.IRON, BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, id))
                    .strength(3F, 50F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)));

    public static final DeferredBlock<CrateBlock> GOLD_CRATE = registerBlock("gold_crate",
            id -> new CrateBlock(CrateTier.GOLD, BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, id))
                    .strength(3F, 50F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)));

    public static final DeferredBlock<CrateBlock> DIAMOND_CRATE = registerBlock("diamond_crate",
            id -> new CrateBlock(CrateTier.DIAMOND, BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, id))
                    .strength(3F, 50F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)));

    public static final DeferredBlock<CrateBlock> EMERALD_CRATE = registerBlock("emerald_crate",
            id -> new CrateBlock(CrateTier.EMERALD, BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, id))
                    .strength(3F, 50F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)));

    public static final DeferredBlock<CrateBlock> NETHERITE_CRATE = registerBlock("netherite_crate",
            id -> new CrateBlock(CrateTier.NETHERITE, BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, id))
                    .strength(3F, 50F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)));

    public static final DeferredBlock<DrumBlock> WOODEN_DRUM = registerBlock("wooden_drum",
            id -> new DrumBlock(DrumTier.WOODEN, BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, id))
                    .strength(2F, 3F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.WOOD)));

    public static final DeferredBlock<DrumBlock> COPPER_DRUM = registerBlock("copper_drum",
            id -> new DrumBlock(DrumTier.COPPER, BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, id))
                    .strength(3F, 50F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)));

    public static final DeferredBlock<DrumBlock> IRON_DRUM = registerBlock("iron_drum",
            id -> new DrumBlock(DrumTier.IRON, BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, id))
                    .strength(3F, 50F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)));

    public static final DeferredBlock<DrumBlock> GOLD_DRUM = registerBlock("gold_drum",
            id -> new DrumBlock(DrumTier.GOLD, BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, id))
                    .strength(3F, 50F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)));

    public static final DeferredBlock<DrumBlock> DIAMOND_DRUM = registerBlock("diamond_drum",
            id -> new DrumBlock(DrumTier.DIAMOND, BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, id))
                    .strength(3F, 50F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)));

    public static final DeferredBlock<DrumBlock> EMERALD_DRUM = registerBlock("emerald_drum",
            id -> new DrumBlock(DrumTier.EMERALD, BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, id))
                    .strength(3F, 50F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)));

    public static final DeferredBlock<DrumBlock> NETHERITE_DRUM = registerBlock("netherite_drum",
            id -> new DrumBlock(DrumTier.NETHERITE, BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, id))
                    .strength(3F, 50F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)));

    public static final DeferredBlock<Block> SUPER_CHEST_I = registerBlock("super_chest_i",
            id -> new Block(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, id))
                    .strength(3F, 50F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)));

    public static final DeferredBlock<Block> SUPER_CHEST_II = registerBlock("super_chest_ii",
            id -> new Block(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, id))
                    .strength(3F, 50F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)));

    public static final DeferredBlock<Block> SUPER_CHEST_III = registerBlock("super_chest_iii",
            id -> new Block(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, id))
                    .strength(3F, 50F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)));

    public static final DeferredBlock<Block> SUPER_CHEST_IV = registerBlock("super_chest_iv",
            id -> new Block(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, id))
                    .strength(3F, 50F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)));

    public static final DeferredBlock<Block> SUPER_TANK_I = registerBlock("super_tank_i",
            id -> new Block(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, id))
                    .strength(3F, 50F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)));

    public static final DeferredBlock<Block> SUPER_TANK_II = registerBlock("super_tank_ii",
            id -> new Block(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, id))
                    .strength(3F, 50F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)));

    public static final DeferredBlock<Block> SUPER_TANK_III = registerBlock("super_tank_iii",
            id -> new Block(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, id))
                    .strength(3F, 50F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)));

    public static final DeferredBlock<Block> SUPER_TANK_IV = registerBlock("super_tank_iv",
            id -> new Block(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, id))
                    .strength(3F, 50F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Function<Identifier, T> factory) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, factory);
        return toReturn;
    }

    public static void register (IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}