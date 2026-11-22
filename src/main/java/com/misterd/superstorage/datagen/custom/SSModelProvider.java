package com.misterd.superstorage.datagen.custom;

import com.misterd.superstorage.SuperStorage;
import com.misterd.superstorage.block.SSBlocks;
import com.misterd.superstorage.block.custom.CrateBlock;
import com.misterd.superstorage.component.SSDataComponents;
import com.misterd.superstorage.item.SSItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

import java.util.Optional;
import java.util.stream.Stream;

public class SSModelProvider extends ModelProvider {
    private static final TextureSlot CRATE = TextureSlot.create("crate");
    private static final ModelTemplate TAPED_CRATE = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(SuperStorage.MODID, "block/taped_crate")), Optional.empty(), CRATE);

    public SSModelProvider(PackOutput output) {
        super(output, SuperStorage.MODID);
    }

    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        return Stream.empty();
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        crate(blockModels, itemModels, SSBlocks.WOODEN_CRATE.get());
        crate(blockModels, itemModels, SSBlocks.COPPER_CRATE.get());
        crate(blockModels, itemModels, SSBlocks.IRON_CRATE.get());
        crate(blockModels, itemModels, SSBlocks.GOLD_CRATE.get());
        crate(blockModels, itemModels, SSBlocks.EMERALD_CRATE.get());
        crate(blockModels, itemModels, SSBlocks.DIAMOND_CRATE.get());
        crate(blockModels, itemModels, SSBlocks.NETHERITE_CRATE.get());

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
        itemModels.generateFlatItem(SSItems.FILTER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(SSItems.TAPE.get(), ModelTemplates.FLAT_ITEM);
    }

    private void crate(BlockModelGenerators blockModels, ItemModelGenerators itemModels, Block block) {
        Identifier base = ModelTemplates.CUBE_ALL.create(block, TextureMapping.cube(block), blockModels.modelOutput);
        Identifier taped = TAPED_CRATE.createWithSuffix(block, "_taped", new TextureMapping().put(CRATE, TextureMapping.getBlockTexture(block)), blockModels.modelOutput);
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(block).with(PropertyDispatch.initial(CrateBlock.TAPED)
                .select(false, BlockModelGenerators.plainVariant(base))
                .select(true, BlockModelGenerators.plainVariant(taped))));
        itemModels.itemModelOutput.accept(block.asItem(), ItemModelUtils.conditional(
                ItemModelUtils.hasComponent(SSDataComponents.PACKED_CRATE.get()),
                ItemModelUtils.plainModel(taped),
                ItemModelUtils.plainModel(base)));
    }
}