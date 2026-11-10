package com.misterd.superstorage.datagen.custom;

import com.misterd.superstorage.SuperStorage;
import com.misterd.superstorage.block.SSBlocks;
import com.misterd.superstorage.block.custom.SuperControllerBlock;
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
        blockModels.createTrivialCube(SSBlocks.STORAGE_CASING.get());
        createController(blockModels);
    }

    private void createController(BlockModelGenerators blockModels) {
        Block controller = SSBlocks.SUPER_CONTROLLER.get();
        TextureMapping off = new TextureMapping()
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(SSBlocks.STORAGE_CASING.get()))
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(SSBlocks.STORAGE_CASING.get()))
                .put(TextureSlot.FRONT, TextureMapping.getBlockTexture(controller, "_front"));
        TextureMapping on = off.copyAndUpdate(TextureSlot.FRONT, TextureMapping.getBlockTexture(controller, "_front_on"));
        MultiVariant offModel = BlockModelGenerators.plainVariant(ModelTemplates.CUBE_ORIENTABLE.create(controller, off, blockModels.modelOutput));
        MultiVariant onModel = BlockModelGenerators.plainVariant(ModelTemplates.CUBE_ORIENTABLE.createWithSuffix(controller, "_on", on, blockModels.modelOutput));
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(controller)
                .with(BlockModelGenerators.createBooleanModelDispatch(SuperControllerBlock.ACTIVE, onModel, offModel))
                .with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING));
    }
}