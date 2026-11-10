package com.misterd.superstorage.datagen;

import com.misterd.superstorage.SuperStorage;
import com.misterd.superstorage.datagen.custom.SSBlockTagProvider;
import com.misterd.superstorage.datagen.custom.SSLootTableProvider;
import com.misterd.superstorage.datagen.custom.SSModelProvider;
import com.misterd.superstorage.datagen.custom.SSRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = SuperStorage.MODID)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(true, new LootTableProvider(packOutput, Collections.emptySet(), List.of(new LootTableProvider.SubProviderEntry(SSLootTableProvider::new, LootContextParamSets.BLOCK)), lookupProvider));
        generator.addProvider(true, new SSRecipeProvider.Runner(packOutput, lookupProvider));
        generator.addProvider(true, new SSBlockTagProvider(packOutput, lookupProvider));
        generator.addProvider(true, new SSModelProvider(packOutput));
    }
}