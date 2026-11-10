package com.misterd.superstorage.datagen.custom;

import com.misterd.superstorage.block.SSBlocks;
import com.misterd.superstorage.component.SSDataComponents;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
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
        add(SSBlocks.WOODEN_CRATE.get(), this::createCrateTable);
        add(SSBlocks.COPPER_CRATE.get(), this::createCrateTable);
        add(SSBlocks.IRON_CRATE.get(), this::createCrateTable);
        add(SSBlocks.GOLD_CRATE.get(), this::createCrateTable);
        add(SSBlocks.EMERALD_CRATE.get(), this::createCrateTable);
        add(SSBlocks.DIAMOND_CRATE.get(), this::createCrateTable);
        add(SSBlocks.NETHERITE_CRATE.get(), this::createCrateTable);
        add(SSBlocks.WOODEN_DRUM.get(), this::createDrumTable);
        add(SSBlocks.COPPER_DRUM.get(), this::createDrumTable);
        add(SSBlocks.IRON_DRUM.get(), this::createDrumTable);
        add(SSBlocks.GOLD_DRUM.get(), this::createDrumTable);
        add(SSBlocks.EMERALD_DRUM.get(), this::createDrumTable);
        add(SSBlocks.DIAMOND_DRUM.get(), this::createDrumTable);
        add(SSBlocks.NETHERITE_DRUM.get(), this::createDrumTable);
        add(SSBlocks.SUPER_CHEST_I.get(), this::createSuperChestTable);
        add(SSBlocks.SUPER_CHEST_II.get(), this::createSuperChestTable);
        add(SSBlocks.SUPER_CHEST_III.get(), this::createSuperChestTable);
        add(SSBlocks.SUPER_CHEST_IV.get(), this::createSuperChestTable);
        add(SSBlocks.SUPER_TANK_I.get(), this::createSuperTankTable);
        add(SSBlocks.SUPER_TANK_II.get(), this::createSuperTankTable);
        add(SSBlocks.SUPER_TANK_III.get(), this::createSuperTankTable);
        add(SSBlocks.SUPER_TANK_IV.get(), this::createSuperTankTable);
    }

    private LootTable.Builder createCrateTable(Block block) {
        return createComponentTable(block, CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY)
                .include(DataComponents.CUSTOM_NAME));
    }

    private LootTable.Builder createDrumTable(Block block) {
        return createComponentTable(block, CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY)
                .include(SSDataComponents.FLUID_CONTENT.get())
                .include(DataComponents.CUSTOM_NAME));
    }

    private LootTable.Builder createSuperChestTable(Block block) {
        return createComponentTable(block, CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY)
                .include(SSDataComponents.SUPER_CHEST_CONTENTS.get())
                .include(SSDataComponents.SUPER_SETTINGS.get())
                .include(DataComponents.CUSTOM_NAME));
    }

    private LootTable.Builder createSuperTankTable(Block block) {
        return createComponentTable(block, CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY)
                .include(SSDataComponents.SUPER_TANK_CONTENTS.get())
                .include(SSDataComponents.SUPER_SETTINGS.get())
                .include(DataComponents.CUSTOM_NAME));
    }

    private LootTable.Builder createComponentTable(Block block, CopyComponentsFunction.Builder copy) {
        return LootTable.lootTable()
                .withPool(applyExplosionCondition(block, LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(block).apply(copy))));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return SSBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}