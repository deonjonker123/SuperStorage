package com.misterd.superstorage.datagen.custom;

import com.misterd.superstorage.SuperStorage;
import com.misterd.superstorage.block.SSBlocks;
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
    }
}