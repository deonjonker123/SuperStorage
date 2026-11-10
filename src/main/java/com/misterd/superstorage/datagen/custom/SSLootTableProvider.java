package com.misterd.superstorage.datagen.custom;

import com.misterd.superstorage.block.SSBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

import java.util.Set;

public class SSLootTableProvider extends BlockLootSubProvider {
    public SSLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(SSBlocks.WOODEN_CRATE.get());
        dropSelf(SSBlocks.COPPER_CRATE.get());
        dropSelf(SSBlocks.IRON_CRATE.get());
        dropSelf(SSBlocks.GOLD_CRATE.get());
        dropSelf(SSBlocks.EMERALD_CRATE.get());
        dropSelf(SSBlocks.DIAMOND_CRATE.get());
        dropSelf(SSBlocks.NETHERITE_CRATE.get());
        dropSelf(SSBlocks.WOODEN_DRUM.get());
        dropSelf(SSBlocks.COPPER_DRUM.get());
        dropSelf(SSBlocks.IRON_DRUM.get());
        dropSelf(SSBlocks.GOLD_DRUM.get());
        dropSelf(SSBlocks.EMERALD_DRUM.get());
        dropSelf(SSBlocks.DIAMOND_DRUM.get());
        dropSelf(SSBlocks.NETHERITE_DRUM.get());
        dropSelf(SSBlocks.SUPER_CHEST_I.get());
        dropSelf(SSBlocks.SUPER_CHEST_II.get());
        dropSelf(SSBlocks.SUPER_CHEST_III.get());
        dropSelf(SSBlocks.SUPER_CHEST_IV.get());
        dropSelf(SSBlocks.SUPER_TANK_I.get());
        dropSelf(SSBlocks.SUPER_TANK_II.get());
        dropSelf(SSBlocks.SUPER_TANK_III.get());
        dropSelf(SSBlocks.SUPER_TANK_IV.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return SSBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
