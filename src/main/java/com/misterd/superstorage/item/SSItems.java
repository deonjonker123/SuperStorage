package com.misterd.superstorage.item;

import com.misterd.superstorage.SuperStorage;
import com.misterd.superstorage.block.SSBlocks;
import com.misterd.superstorage.item.custom.CrateBlockItem;
import com.misterd.superstorage.item.custom.DrumBlockItem;
import com.misterd.superstorage.item.custom.SuperBlockItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SSItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SuperStorage.MODID);

    public static final DeferredItem<Item> WOODEN_CRATE = ITEMS.registerItem("wooden_crate",
            props -> new CrateBlockItem(SSBlocks.WOODEN_CRATE.get(), props));

    public static final DeferredItem<Item> COPPER_CRATE = ITEMS.registerItem("copper_crate",
            props -> new CrateBlockItem(SSBlocks.COPPER_CRATE.get(), props));

    public static final DeferredItem<Item> IRON_CRATE = ITEMS.registerItem("iron_crate",
            props -> new CrateBlockItem(SSBlocks.IRON_CRATE.get(), props));

    public static final DeferredItem<Item> GOLD_CRATE = ITEMS.registerItem("gold_crate",
            props -> new CrateBlockItem(SSBlocks.GOLD_CRATE.get(), props));

    public static final DeferredItem<Item> DIAMOND_CRATE = ITEMS.registerItem("diamond_crate",
            props -> new CrateBlockItem(SSBlocks.DIAMOND_CRATE.get(), props));

    public static final DeferredItem<Item> EMERALD_CRATE = ITEMS.registerItem("emerald_crate",
            props -> new CrateBlockItem(SSBlocks.EMERALD_CRATE.get(), props));

    public static final DeferredItem<Item> NETHERITE_CRATE = ITEMS.registerItem("netherite_crate",
            props -> new CrateBlockItem(SSBlocks.NETHERITE_CRATE.get(), props));

    public static final DeferredItem<Item> WOODEN_DRUM = ITEMS.registerItem("wooden_drum",
            props -> new DrumBlockItem(SSBlocks.WOODEN_DRUM.get(), props));

    public static final DeferredItem<Item> COPPER_DRUM = ITEMS.registerItem("copper_drum",
            props -> new DrumBlockItem(SSBlocks.COPPER_DRUM.get(), props));

    public static final DeferredItem<Item> IRON_DRUM = ITEMS.registerItem("iron_drum",
            props -> new DrumBlockItem(SSBlocks.IRON_DRUM.get(), props));

    public static final DeferredItem<Item> GOLD_DRUM = ITEMS.registerItem("gold_drum",
            props -> new DrumBlockItem(SSBlocks.GOLD_DRUM.get(), props));

    public static final DeferredItem<Item> EMERALD_DRUM = ITEMS.registerItem("emerald_drum",
            props -> new DrumBlockItem(SSBlocks.EMERALD_DRUM.get(), props));

    public static final DeferredItem<Item> DIAMOND_DRUM = ITEMS.registerItem("diamond_drum",
            props -> new DrumBlockItem(SSBlocks.DIAMOND_DRUM.get(), props));

    public static final DeferredItem<Item> NETHERITE_DRUM = ITEMS.registerItem("netherite_drum",
            props -> new DrumBlockItem(SSBlocks.NETHERITE_DRUM.get(), props));

    public static final DeferredItem<Item> SUPER_CHEST_I = ITEMS.registerItem("super_chest_i",
            props -> new SuperBlockItem(SSBlocks.SUPER_CHEST_I.get(), props));

    public static final DeferredItem<Item> SUPER_CHEST_II = ITEMS.registerItem("super_chest_ii",
            props -> new SuperBlockItem(SSBlocks.SUPER_CHEST_II.get(), props));

    public static final DeferredItem<Item> SUPER_CHEST_III = ITEMS.registerItem("super_chest_iii",
            props -> new SuperBlockItem(SSBlocks.SUPER_CHEST_III.get(), props));

    public static final DeferredItem<Item> SUPER_CHEST_IV = ITEMS.registerItem("super_chest_iv",
            props -> new SuperBlockItem(SSBlocks.SUPER_CHEST_IV.get(), props));

    public static final DeferredItem<Item> SUPER_TANK_I = ITEMS.registerItem("super_tank_i",
            props -> new SuperBlockItem(SSBlocks.SUPER_TANK_I.get(), props));

    public static final DeferredItem<Item> SUPER_TANK_II = ITEMS.registerItem("super_tank_ii",
            props -> new SuperBlockItem(SSBlocks.SUPER_TANK_II.get(), props));

    public static final DeferredItem<Item> SUPER_TANK_III = ITEMS.registerItem("super_tank_iii",
            props -> new SuperBlockItem(SSBlocks.SUPER_TANK_III.get(), props));

    public static final DeferredItem<Item> SUPER_TANK_IV = ITEMS.registerItem("super_tank_iv",
            props -> new SuperBlockItem(SSBlocks.SUPER_TANK_IV.get(), props));

    public static final DeferredItem<Item> CREATIVE_SUPER_CHEST = ITEMS.registerItem("creative_super_chest",
            props -> new SuperBlockItem(SSBlocks.CREATIVE_SUPER_CHEST.get(), props.rarity(Rarity.EPIC)));

    public static final DeferredItem<Item> CREATIVE_SUPER_TANK = ITEMS.registerItem("creative_super_tank",
            props -> new SuperBlockItem(SSBlocks.CREATIVE_SUPER_TANK.get(), props.rarity(Rarity.EPIC)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}