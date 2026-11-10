package com.misterd.superstorage;

import com.misterd.superstorage.block.SSBlocks;
import com.misterd.superstorage.blockentity.SSBlockEntities;
import com.misterd.superstorage.gui.SSMenuTypes;
import com.misterd.superstorage.gui.custom.CrateScreen;
import com.misterd.superstorage.item.SSCreativeTab;
import com.misterd.superstorage.item.SSItems;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(SuperStorage.MODID)
public class SuperStorage {
    public static final String MODID = "superstorage";

    public SuperStorage(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerCapabilities);
        NeoForge.EVENT_BUS.register(this);

        SSItems.register(modEventBus);
        SSBlocks.register(modEventBus);
        SSCreativeTab.register(modEventBus);
        SSBlockEntities.register(modEventBus);
        SSMenuTypes.register(modEventBus);
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.Item.BLOCK, SSBlockEntities.CRATE_BE.get(), (crate, side) -> crate.getItemHandler());
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(SSMenuTypes.CRATE_MENU.get(), CrateScreen::new);
        }
    }
}