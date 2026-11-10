package com.misterd.superstorage.datagen.custom;

import com.misterd.superstorage.block.SSBlocks;
import com.misterd.superstorage.component.SSDataComponents;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

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
        add(SSBlocks.WOODEN_DRUM.get(), this::createDrumTable);
        add(SSBlocks.COPPER_DRUM.get(), this::createDrumTable);
        add(SSBlocks.IRON_DRUM.get(), this::createDrumTable);
        add(SSBlocks.GOLD_DRUM.get(), this::createDrumTable);
        add(SSBlocks.EMERALD_DRUM.get(), this::createDrumTable);
        add(SSBlocks.DIAMOND_DRUM.get(), this::createDrumTable);
        add(SSBlocks.NETHERITE_DRUM.get(), this::createDrumTable);
        dropSelf(SSBlocks.SUPER_CHEST_I.get());
        dropSelf(SSBlocks.SUPER_CHEST_II.get());
        dropSelf(SSBlocks.SUPER_CHEST_III.get());
        dropSelf(SSBlocks.SUPER_CHEST_IV.get());
        dropSelf(SSBlocks.SUPER_TANK_I.get());
        dropSelf(SSBlocks.SUPER_TANK_II.get());
        dropSelf(SSBlocks.SUPER_TANK_III.get());
        dropSelf(SSBlocks.SUPER_TANK_IV.get());
    }

    private LootTable.Builder createDrumTable(Block block) {
        return LootTable.lootTable()
                .withPool(applyExplosionCondition(block, LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(block)
                                .apply(CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY)
                                        .include(SSDataComponents.FLUID_CONTENT.get())))));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return SSBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}