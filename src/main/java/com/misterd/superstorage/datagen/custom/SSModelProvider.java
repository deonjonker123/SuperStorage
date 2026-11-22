package com.misterd.superstorage.datagen.custom;

import com.misterd.superstorage.SuperStorage;
import com.misterd.superstorage.block.SSBlocks;
import com.misterd.superstorage.item.SSItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;

import java.util.stream.Stream;

public class SSModelProvider extends ModelProvider {
    public SSModelProvider(PackOutput output) {
        super(output, SuperStorage.MODID);
    }

    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        return Stream.empty();
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        blockModels.createTrivialCube(SSBlocks.WOODEN_CRATE.get());
        blockModels.createTrivialCube(SSBlocks.COPPER_CRATE.get());
        blockModels.createTrivialCube(SSBlocks.IRON_CRATE.get());
        blockModels.createTrivialCube(SSBlocks.GOLD_CRATE.get());
        blockModels.createTrivialCube(SSBlocks.EMERALD_CRATE.get());
        blockModels.createTrivialCube(SSBlocks.DIAMOND_CRATE.get());
        blockModels.createTrivialCube(SSBlocks.NETHERITE_CRATE.get());

        itemModels.generateFlatItem(SSItems.UPGRADE_BASE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(SSItems.WOOD_TO_COPPER_KIT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(SSItems.COPPER_TO_IRON_KIT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(SSItems.IRON_TO_GOLD_KIT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(SSItems.GOLD_TO_EMERALD_KIT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(SSItems.EMERALD_TO_DIAMOND_KIT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(SSItems.DIAMOND_TO_NETHERITE_KIT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(SSItems.I_TO_II_KIT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(SSItems.II_TO_III_KIT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(SSItems.III_TO_IV_KIT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(SSItems.HOPPER_UPGRADE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(SSItems.COLLECTOR_UPGRADE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(SSItems.COMPACTING_UPGRADE.get(), ModelTemplates.FLAT_ITEM);

    }
}