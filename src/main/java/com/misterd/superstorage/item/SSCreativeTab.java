package com.misterd.superstorage.item;

import com.misterd.superstorage.SuperStorage;
import com.misterd.superstorage.block.SSBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SSCreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SuperStorage.MODID);

    public static final Supplier<CreativeModeTab> SUPER_STORAGE = CREATIVE_MODE_TAB.register("super_storage_creativetab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(SSBlocks.SUPER_CHEST_I.get()))
                    .title(Component.translatable("creativetab.super_storage"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(SSBlocks.WOODEN_CRATE);
                        output.accept(SSBlocks.COPPER_CRATE);
                        output.accept(SSBlocks.IRON_CRATE);
                        output.accept(SSBlocks.GOLD_CRATE);
                        output.accept(SSBlocks.EMERALD_CRATE);
                        output.accept(SSBlocks.DIAMOND_CRATE);
                        output.accept(SSBlocks.NETHERITE_CRATE);

                        output.accept(SSBlocks.WOODEN_DRUM);
                        output.accept(SSBlocks.COPPER_DRUM);
                        output.accept(SSBlocks.IRON_DRUM);
                        output.accept(SSBlocks.GOLD_DRUM);
                        output.accept(SSBlocks.EMERALD_DRUM);
                        output.accept(SSBlocks.DIAMOND_DRUM);
                        output.accept(SSBlocks.NETHERITE_DRUM);

                        output.accept(SSBlocks.SUPER_CHEST_I);
                        output.accept(SSBlocks.SUPER_CHEST_II);
                        output.accept(SSBlocks.SUPER_CHEST_III);
                        output.accept(SSBlocks.SUPER_CHEST_IV);

                        output.accept(SSBlocks.SUPER_TANK_I);
                        output.accept(SSBlocks.SUPER_TANK_II);
                        output.accept(SSBlocks.SUPER_TANK_III);
                        output.accept(SSBlocks.SUPER_TANK_IV);
                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
