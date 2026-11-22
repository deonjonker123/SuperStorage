package com.misterd.superstorage.item;

import com.misterd.superstorage.SuperStorage;
import com.misterd.superstorage.block.SSBlocks;
import com.misterd.superstorage.item.custom.*;
import com.misterd.superstorage.util.UpgradeType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SSItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SuperStorage.MODID);

    public static final DeferredItem<Item> UPGRADE_BASE = ITEMS.registerSimpleItem("upgrade_base");

    public static final DeferredItem<Item> WOOD_TO_COPPER_KIT = ITEMS.registerItem("wood_to_copper_kit",
            props -> new TierKitItem(props,
                    new TierKitItem.Upgrade(SSBlocks.WOODEN_CRATE, SSBlocks.COPPER_CRATE),
                    new TierKitItem.Upgrade(SSBlocks.WOODEN_DRUM, SSBlocks.COPPER_DRUM)));

    public static final DeferredItem<Item> COPPER_TO_IRON_KIT = ITEMS.registerItem("copper_to_iron_kit",
            props -> new TierKitItem(props,
                    new TierKitItem.Upgrade(SSBlocks.COPPER_CRATE, SSBlocks.IRON_CRATE),
                    new TierKitItem.Upgrade(SSBlocks.COPPER_DRUM, SSBlocks.IRON_DRUM)));

    public static final DeferredItem<Item> IRON_TO_GOLD_KIT = ITEMS.registerItem("iron_to_gold_kit",
            props -> new TierKitItem(props,
                    new TierKitItem.Upgrade(SSBlocks.IRON_CRATE, SSBlocks.GOLD_CRATE),
                    new TierKitItem.Upgrade(SSBlocks.IRON_DRUM, SSBlocks.GOLD_DRUM)));

    public static final DeferredItem<Item> GOLD_TO_EMERALD_KIT = ITEMS.registerItem("gold_to_emerald_kit",
            props -> new TierKitItem(props,
                    new TierKitItem.Upgrade(SSBlocks.GOLD_CRATE, SSBlocks.EMERALD_CRATE),
                    new TierKitItem.Upgrade(SSBlocks.GOLD_DRUM, SSBlocks.EMERALD_DRUM)));

    public static final DeferredItem<Item> EMERALD_TO_DIAMOND_KIT = ITEMS.registerItem("emerald_to_diamond_kit",
            props -> new TierKitItem(props,
                    new TierKitItem.Upgrade(SSBlocks.EMERALD_CRATE, SSBlocks.DIAMOND_CRATE),
                    new TierKitItem.Upgrade(SSBlocks.EMERALD_DRUM, SSBlocks.DIAMOND_DRUM)));

    public static final DeferredItem<Item> DIAMOND_TO_NETHERITE_KIT = ITEMS.registerItem("diamond_to_netherite_kit",
            props -> new TierKitItem(props,
                    new TierKitItem.Upgrade(SSBlocks.DIAMOND_CRATE, SSBlocks.NETHERITE_CRATE),
                    new TierKitItem.Upgrade(SSBlocks.DIAMOND_DRUM, SSBlocks.NETHERITE_DRUM)));

    public static final DeferredItem<Item> I_TO_II_KIT = ITEMS.registerItem("i_to_ii_kit",
            props -> new TierKitItem(props,
                    new TierKitItem.Upgrade(SSBlocks.SUPER_CHEST_I, SSBlocks.SUPER_CHEST_II),
                    new TierKitItem.Upgrade(SSBlocks.SUPER_TANK_I, SSBlocks.SUPER_TANK_II)));

    public static final DeferredItem<Item> II_TO_III_KIT = ITEMS.registerItem("ii_to_iii_kit",
            props -> new TierKitItem(props,
                    new TierKitItem.Upgrade(SSBlocks.SUPER_CHEST_II, SSBlocks.SUPER_CHEST_III),
                    new TierKitItem.Upgrade(SSBlocks.SUPER_TANK_II, SSBlocks.SUPER_TANK_III)));

    public static final DeferredItem<Item> III_TO_IV_KIT = ITEMS.registerItem("iii_to_iv_kit",
            props -> new TierKitItem(props,
                    new TierKitItem.Upgrade(SSBlocks.SUPER_CHEST_III, SSBlocks.SUPER_CHEST_IV),
                    new TierKitItem.Upgrade(SSBlocks.SUPER_TANK_III, SSBlocks.SUPER_TANK_IV)));

    public static final DeferredItem<Item> FILTER = ITEMS.registerItem("filter", FilterItem::new);

    public static final DeferredItem<Item> HOPPER_UPGRADE = ITEMS.registerItem("hopper_upgrade",
            props -> new UpgradeItem(UpgradeType.HOPPER, props));

    public static final DeferredItem<Item> COLLECTOR_UPGRADE = ITEMS.registerItem("collector_upgrade",
            props -> new UpgradeItem(UpgradeType.COLLECTOR, props));

    public static final DeferredItem<Item> COMPACTING_UPGRADE = ITEMS.registerItem("compacting_upgrade",
            props -> new UpgradeItem(UpgradeType.COMPACTING, props));

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