package com.misterd.superstorage;

import com.misterd.superstorage.block.SSBlocks;
import com.misterd.superstorage.blockentity.SSBlockEntities;
import com.misterd.superstorage.item.SSCreativeTab;
import com.misterd.superstorage.item.SSItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(SuperStorage.MODID)
public class SuperStorage {
    public static final String MODID = "superstorage";

    public SuperStorage(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.register(this);

        SSItems.register(modEventBus);
        SSBlocks.register(modEventBus);
        SSCreativeTab.register(modEventBus);
        SSBlockEntities.register(modEventBus);
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }
}
