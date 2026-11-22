package com.misterd.superstorage.datagen.custom;

import com.misterd.superstorage.block.SSBlocks;
import com.misterd.superstorage.item.SSItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class SSRecipeProvider extends RecipeProvider {
    public SSRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
        super(provider, recipeOutput);
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> provider) {
            super(packOutput, provider);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
            return new SSRecipeProvider(provider, recipeOutput);
        }

        @Override
        public String getName() {
            return "My Recipes";
        }
    }

    @Override
    protected void buildRecipes() {
        shaped(RecipeCategory.MISC, SSBlocks.WOODEN_CRATE.get())
                .pattern("PSP")
                .pattern("PCP")
                .pattern("PSP")
                .define('P', ItemTags.PLANKS)
                .define('S', ItemTags.WOODEN_SLABS)
                .define('C', Tags.Items.CHESTS)
                .unlockedBy("has_chest", has(Tags.Items.CHESTS))
                .save(output);

        shaped(RecipeCategory.MISC, SSBlocks.COPPER_CRATE.get())
                .pattern("PSP")
                .pattern("SCS")
                .pattern("PSP")
                .define('P', Items.COPPER_INGOT)
                .define('S', Tags.Items.RODS)
                .define('C', Tags.Items.CHESTS)
                .unlockedBy("has_copper_ingot", has(Items.COPPER_INGOT))
                .save(output);

        shaped(RecipeCategory.MISC, SSBlocks.IRON_CRATE.get())
                .pattern("PSP")
                .pattern("SCS")
                .pattern("PSP")
                .define('P', Items.IRON_INGOT)
                .define('S', Tags.Items.RODS)
                .define('C', Tags.Items.CHESTS)
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                .save(output);

        shaped(RecipeCategory.MISC, SSBlocks.GOLD_CRATE.get())
                .pattern("PSP")
                .pattern("SCS")
                .pattern("PSP")
                .define('P', Items.GOLD_INGOT)
                .define('S', Tags.Items.RODS)
                .define('C', Tags.Items.CHESTS)
                .unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT))
                .save(output);

        shaped(RecipeCategory.MISC, SSBlocks.EMERALD_CRATE.get())
                .pattern("PSP")
                .pattern("SCS")
                .pattern("PSP")
                .define('P', Items.EMERALD)
                .define('S', Tags.Items.RODS)
                .define('C', Tags.Items.CHESTS)
                .unlockedBy("has_emerald", has(Items.EMERALD))
                .save(output);

        shaped(RecipeCategory.MISC, SSBlocks.DIAMOND_CRATE.get())
                .pattern("PSP")
                .pattern("SCS")
                .pattern("PSP")
                .define('P', Items.DIAMOND)
                .define('S', Tags.Items.RODS)
                .define('C', Tags.Items.CHESTS)
                .unlockedBy("has_diamond", has(Items.DIAMOND))
                .save(output);

        shaped(RecipeCategory.MISC, SSBlocks.NETHERITE_CRATE.get())
                .pattern("PSP")
                .pattern("SCS")
                .pattern("PSP")
                .define('P', Items.NETHERITE_INGOT)
                .define('S', Tags.Items.RODS)
                .define('C', Tags.Items.CHESTS)
                .unlockedBy("has_netherite_ingot", has(Items.NETHERITE_INGOT))
                .save(output);

        shaped(RecipeCategory.MISC, SSBlocks.WOODEN_DRUM.get())
                .pattern("PSP")
                .pattern("PCP")
                .pattern("PSP")
                .define('P', ItemTags.PLANKS)
                .define('S', ItemTags.WOODEN_SLABS)
                .define('C', Tags.Items.BUCKETS)
                .unlockedBy("has_bucket", has(Tags.Items.BUCKETS))
                .save(output);

        shaped(RecipeCategory.MISC, SSBlocks.COPPER_DRUM.get())
                .pattern("PSP")
                .pattern("SCS")
                .pattern("PSP")
                .define('P', Items.COPPER_INGOT)
                .define('S', Tags.Items.RODS)
                .define('C', Tags.Items.BUCKETS)
                .unlockedBy("has_copper_ingot", has(Items.COPPER_INGOT))
                .save(output);

        shaped(RecipeCategory.MISC, SSBlocks.IRON_DRUM.get())
                .pattern("PSP")
                .pattern("SCS")
                .pattern("PSP")
                .define('P', Items.IRON_INGOT)
                .define('S', Tags.Items.RODS)
                .define('C', Tags.Items.BUCKETS)
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                .save(output);

        shaped(RecipeCategory.MISC, SSBlocks.GOLD_DRUM.get())
                .pattern("PSP")
                .pattern("SCS")
                .pattern("PSP")
                .define('P', Items.GOLD_INGOT)
                .define('S', Tags.Items.RODS)
                .define('C', Tags.Items.BUCKETS)
                .unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT))
                .save(output);

        shaped(RecipeCategory.MISC, SSBlocks.EMERALD_DRUM.get())
                .pattern("PSP")
                .pattern("SCS")
                .pattern("PSP")
                .define('P', Items.EMERALD)
                .define('S', Tags.Items.RODS)
                .define('C', Tags.Items.BUCKETS)
                .unlockedBy("has_emerald", has(Items.EMERALD))
                .save(output);

        shaped(RecipeCategory.MISC, SSBlocks.DIAMOND_DRUM.get())
                .pattern("PSP")
                .pattern("SCS")
                .pattern("PSP")
                .define('P', Items.DIAMOND)
                .define('S', Tags.Items.RODS)
                .define('C', Tags.Items.BUCKETS)
                .unlockedBy("has_diamond", has(Items.DIAMOND))
                .save(output);

        shaped(RecipeCategory.MISC, SSBlocks.NETHERITE_DRUM.get())
                .pattern("PSP")
                .pattern("SCS")
                .pattern("PSP")
                .define('P', Items.NETHERITE_INGOT)
                .define('S', Tags.Items.RODS)
                .define('C', Tags.Items.BUCKETS)
                .unlockedBy("has_netherite_ingot", has(Items.NETHERITE_INGOT))
                .save(output);

        shaped(RecipeCategory.MISC, SSBlocks.SUPER_CHEST_I.get())
                .pattern("PSP")
                .pattern("SCS")
                .pattern("PSP")
                .define('P', Items.GOLD_BLOCK)
                .define('S', Items.GOLD_INGOT)
                .define('C', SSBlocks.GOLD_CRATE)
                .unlockedBy("has_gold_crate", has(SSBlocks.GOLD_CRATE))
                .save(output);

        shaped(RecipeCategory.MISC, SSBlocks.SUPER_CHEST_II.get())
                .pattern("PSP")
                .pattern("SCS")
                .pattern("PSP")
                .define('P', Items.EMERALD_BLOCK)
                .define('S', Items.EMERALD)
                .define('C', SSBlocks.EMERALD_CRATE)
                .unlockedBy("has_emerald_crate", has(SSBlocks.EMERALD_CRATE))
                .save(output);

        shaped(RecipeCategory.MISC, SSBlocks.SUPER_CHEST_III.get())
                .pattern("PSP")
                .pattern("SCS")
                .pattern("PSP")
                .define('P', Items.DIAMOND_BLOCK)
                .define('S', Items.DIAMOND)
                .define('C', SSBlocks.DIAMOND_CRATE)
                .unlockedBy("has_diamond_crate", has(SSBlocks.DIAMOND_CRATE))
                .save(output);

        shaped(RecipeCategory.MISC, SSBlocks.SUPER_CHEST_IV.get())
                .pattern("PSP")
                .pattern("SCS")
                .pattern("PSP")
                .define('P', Items.NETHERITE_BLOCK)
                .define('S', Items.NETHERITE_INGOT)
                .define('C', SSBlocks.NETHERITE_CRATE)
                .unlockedBy("has_netherite_crate", has(SSBlocks.NETHERITE_CRATE))
                .save(output);

        shaped(RecipeCategory.MISC, SSBlocks.SUPER_TANK_I.get())
                .pattern("PSP")
                .pattern("SCS")
                .pattern("PSP")
                .define('P', Items.GOLD_BLOCK)
                .define('S', Items.GOLD_INGOT)
                .define('C', SSBlocks.GOLD_DRUM)
                .unlockedBy("has_gold_drum", has(SSBlocks.GOLD_DRUM))
                .save(output);

        shaped(RecipeCategory.MISC, SSBlocks.SUPER_TANK_II.get())
                .pattern("PSP")
                .pattern("SCS")
                .pattern("PSP")
                .define('P', Items.EMERALD_BLOCK)
                .define('S', Items.EMERALD)
                .define('C', SSBlocks.EMERALD_DRUM)
                .unlockedBy("has_emerald_drum", has(SSBlocks.EMERALD_DRUM))
                .save(output);

        shaped(RecipeCategory.MISC, SSBlocks.SUPER_TANK_III.get())
                .pattern("PSP")
                .pattern("SCS")
                .pattern("PSP")
                .define('P', Items.DIAMOND_BLOCK)
                .define('S', Items.DIAMOND)
                .define('C', SSBlocks.DIAMOND_DRUM)
                .unlockedBy("has_diamond_drum", has(SSBlocks.DIAMOND_DRUM))
                .save(output);

        shaped(RecipeCategory.MISC, SSBlocks.SUPER_TANK_IV.get())
                .pattern("PSP")
                .pattern("SCS")
                .pattern("PSP")
                .define('P', Items.NETHERITE_BLOCK)
                .define('S', Items.NETHERITE_INGOT)
                .define('C', SSBlocks.NETHERITE_DRUM)
                .unlockedBy("has_netherite_drum", has(SSBlocks.NETHERITE_DRUM))
                .save(output);

        shaped(RecipeCategory.MISC, SSItems.UPGRADE_BASE.get(), 2)
                .pattern("PSP")
                .pattern("SIS")
                .pattern("PSP")
                .define('P', Items.IRON_INGOT)
                .define('S', Items.IRON_BARS)
                .define('I', Items.QUARTZ)
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                .save(output);

        shaped(RecipeCategory.MISC, SSItems.HOPPER_UPGRADE.get())
                .pattern(" H ")
                .pattern("IBI")
                .pattern(" H ")
                .define('H', Items.HOPPER)
                .define('I', Items.IRON_INGOT)
                .define('B', SSItems.UPGRADE_BASE.get())
                .unlockedBy("has_upgrade_base", has(SSItems.UPGRADE_BASE.get()))
                .save(output);

        shaped(RecipeCategory.MISC, SSItems.COLLECTOR_UPGRADE.get())
                .pattern(" E ")
                .pattern("EBE")
                .pattern(" H ")
                .define('E', Items.ENDER_PEARL)
                .define('H', Items.HOPPER)
                .define('B', SSItems.UPGRADE_BASE.get())
                .unlockedBy("has_upgrade_base", has(SSItems.UPGRADE_BASE.get()))
                .save(output);

        shaped(RecipeCategory.MISC, SSItems.COMPACTING_UPGRADE.get())
                .pattern(" P ")
                .pattern("CBC")
                .pattern(" P ")
                .define('P', Items.PISTON)
                .define('C', Items.CRAFTING_TABLE)
                .define('B', SSItems.UPGRADE_BASE.get())
                .unlockedBy("has_upgrade_base", has(SSItems.UPGRADE_BASE.get()))
                .save(output);

        shaped(RecipeCategory.MISC, SSItems.FILTER.get())
                .pattern("ISI")
                .pattern("SPS")
                .pattern("ISI")
                .define('I', Items.IRON_NUGGET)
                .define('S', Items.STRING)
                .define('P', Items.PAPER)
                .unlockedBy("has_paper", has(Items.PAPER))
                .save(output);

        shapeless(RecipeCategory.MISC, SSItems.FILTER.get())
                .requires(SSItems.FILTER.get())
                .unlockedBy("has_filter", has(SSItems.FILTER.get()))
                .save(output, "superstorage:filter_clear");

        shaped(RecipeCategory.MISC, SSItems.TAPE.get(), 2)
                .pattern(" P ")
                .pattern("PSP")
                .pattern(" P ")
                .define('P', Items.PAPER)
                .define('S', Items.SLIME_BALL)
                .unlockedBy("has_slime_ball", has(Items.SLIME_BALL))
                .save(output);

        kit(SSItems.WOOD_TO_COPPER_KIT.get(), Items.COPPER_INGOT, Tags.Items.RODS);
        kit(SSItems.COPPER_TO_IRON_KIT.get(), Items.IRON_INGOT, Tags.Items.RODS);
        kit(SSItems.IRON_TO_GOLD_KIT.get(), Items.GOLD_INGOT, Tags.Items.RODS);
        kit(SSItems.GOLD_TO_EMERALD_KIT.get(), Items.EMERALD, Tags.Items.RODS);
        kit(SSItems.EMERALD_TO_DIAMOND_KIT.get(), Items.DIAMOND, Tags.Items.RODS);
        kit(SSItems.DIAMOND_TO_NETHERITE_KIT.get(), Items.NETHERITE_INGOT, Tags.Items.RODS);
        kit(SSItems.I_TO_II_KIT.get(), Items.EMERALD_BLOCK, Items.EMERALD);
        kit(SSItems.II_TO_III_KIT.get(), Items.DIAMOND_BLOCK, Items.DIAMOND);
        kit(SSItems.III_TO_IV_KIT.get(), Items.NETHERITE_BLOCK, Items.NETHERITE_INGOT);
    }

    private void kit(ItemLike result, ItemLike corner, TagKey<Item> side) {
        shaped(RecipeCategory.MISC, result)
                .pattern("PSP")
                .pattern("SBS")
                .pattern("PSP")
                .define('P', corner)
                .define('S', side)
                .define('B', SSItems.UPGRADE_BASE.get())
                .unlockedBy("has_upgrade_base", has(SSItems.UPGRADE_BASE.get()))
                .save(output);
    }

    private void kit(ItemLike result, ItemLike corner, ItemLike side) {
        shaped(RecipeCategory.MISC, result)
                .pattern("PSP")
                .pattern("SBS")
                .pattern("PSP")
                .define('P', corner)
                .define('S', side)
                .define('B', SSItems.UPGRADE_BASE.get())
                .unlockedBy("has_upgrade_base", has(SSItems.UPGRADE_BASE.get()))
                .save(output);
    }

}
