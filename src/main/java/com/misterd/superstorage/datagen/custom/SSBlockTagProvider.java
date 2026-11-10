package com.misterd.superstorage.datagen.custom;

import com.misterd.superstorage.SuperStorage;
import com.misterd.superstorage.block.SSBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class SSBlockTagProvider extends BlockTagsProvider {
    public SSBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, SuperStorage.MODID);
    }

    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(SSBlocks.COPPER_CRATE.get())
                .add(SSBlocks.IRON_CRATE.get())
                .add(SSBlocks.GOLD_CRATE.get())
                .add(SSBlocks.EMERALD_CRATE.get())
                .add(SSBlocks.DIAMOND_CRATE.get())
                .add(SSBlocks.NETHERITE_CRATE.get())
                .add(SSBlocks.COPPER_DRUM.get())
                .add(SSBlocks.IRON_DRUM.get())
                .add(SSBlocks.GOLD_DRUM.get())
                .add(SSBlocks.EMERALD_DRUM.get())
                .add(SSBlocks.DIAMOND_DRUM.get())
                .add(SSBlocks.NETHERITE_DRUM.get())
                .add(SSBlocks.SUPER_CHEST_I.get())
                .add(SSBlocks.SUPER_CHEST_II.get())
                .add(SSBlocks.SUPER_CHEST_III.get())
                .add(SSBlocks.SUPER_CHEST_IV.get())
                .add(SSBlocks.CREATIVE_SUPER_CHEST.get())
                .add(SSBlocks.SUPER_TANK_I.get())
                .add(SSBlocks.SUPER_TANK_II.get())
                .add(SSBlocks.SUPER_TANK_III.get())
                .add(SSBlocks.SUPER_TANK_IV.get())
                .add(SSBlocks.CREATIVE_SUPER_TANK.get())
                .add(SSBlocks.SUPER_CONTROLLER.get())
                .add(SSBlocks.STORAGE_CASING.get());

        tag(BlockTags.MINEABLE_WITH_AXE)
                .add(SSBlocks.WOODEN_CRATE.get())
                .add(SSBlocks.WOODEN_DRUM.get());

        tag(BlockTags.WITHER_IMMUNE)
                .add(SSBlocks.CREATIVE_SUPER_CHEST.get())
                .add(SSBlocks.CREATIVE_SUPER_TANK.get());
    }
}