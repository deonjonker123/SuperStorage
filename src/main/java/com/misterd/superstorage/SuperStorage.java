package com.misterd.superstorage;

import com.misterd.superstorage.block.SSBlocks;
import com.misterd.superstorage.block.custom.DrumBlock;
import com.misterd.superstorage.blockentity.SSBlockEntities;
import com.misterd.superstorage.blockentity.custom.SuperChestBlockEntity;
import com.misterd.superstorage.blockentity.custom.SuperTankBlockEntity;
import com.misterd.superstorage.client.ber.SuperChestRenderer;
import com.misterd.superstorage.client.ber.SuperTankRenderer;
import com.misterd.superstorage.client.decorator.StoredContentsDecorator;
import com.misterd.superstorage.component.SSDataComponents;
import com.misterd.superstorage.config.SSConfig;
import com.misterd.superstorage.gui.SSMenuTypes;
import com.misterd.superstorage.gui.custom.CrateScreen;
import com.misterd.superstorage.gui.custom.SuperChestScreen;
import com.misterd.superstorage.gui.custom.SuperTankScreen;
import com.misterd.superstorage.item.SSCreativeTab;
import com.misterd.superstorage.item.SSItems;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.transfer.fluid.ItemAccessFluidHandler;

import java.util.List;

@Mod(SuperStorage.MODID)
public class SuperStorage {
    public static final String MODID = "superstorage";

    public SuperStorage(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerCapabilities);
        NeoForge.EVENT_BUS.register(this);
        modContainer.registerConfig(ModConfig.Type.SERVER, SSConfig.SPEC);
        SSDataComponents.register(modEventBus);
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
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, SSBlockEntities.DRUM_BE.get(), (drum, side) -> drum.getFluidHandler());
        event.registerBlockEntity(Capabilities.Item.BLOCK, SSBlockEntities.SUPER_CHEST_BE.get(), SuperChestBlockEntity::getItemHandler);
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, SSBlockEntities.SUPER_TANK_BE.get(), SuperTankBlockEntity::getFluidHandler);

        List<DeferredBlock<DrumBlock>> drums = List.of(
                SSBlocks.WOODEN_DRUM,
                SSBlocks.COPPER_DRUM,
                SSBlocks.IRON_DRUM,
                SSBlocks.GOLD_DRUM,
                SSBlocks.EMERALD_DRUM,
                SSBlocks.DIAMOND_DRUM,
                SSBlocks.NETHERITE_DRUM);
        for (DeferredBlock<DrumBlock> drum : drums) {
            int capacity = drum.get().getTier().capacity();
            event.registerItem(Capabilities.Fluid.ITEM, (stack, access) -> new ItemAccessFluidHandler(access, SSDataComponents.FLUID_CONTENT.get(), capacity), drum);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(SSMenuTypes.CRATE_MENU.get(), CrateScreen::new);
            event.register(SSMenuTypes.SUPER_CHEST_MENU.get(), SuperChestScreen::new);
            event.register(SSMenuTypes.SUPER_TANK_MENU.get(), SuperTankScreen::new);
        }

        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(SSBlockEntities.SUPER_CHEST_BE.get(), SuperChestRenderer::new);
            event.registerBlockEntityRenderer(SSBlockEntities.SUPER_TANK_BE.get(), SuperTankRenderer::new);
        }

        @SubscribeEvent
        public static void registerItemDecorations(RegisterItemDecorationsEvent event) {
            List<DeferredBlock<?>> blocks = List.of(
                    SSBlocks.WOODEN_DRUM, SSBlocks.COPPER_DRUM, SSBlocks.IRON_DRUM, SSBlocks.GOLD_DRUM,
                    SSBlocks.EMERALD_DRUM, SSBlocks.DIAMOND_DRUM, SSBlocks.NETHERITE_DRUM,
                    SSBlocks.SUPER_CHEST_I, SSBlocks.SUPER_CHEST_II, SSBlocks.SUPER_CHEST_III, SSBlocks.SUPER_CHEST_IV,
                    SSBlocks.SUPER_TANK_I, SSBlocks.SUPER_TANK_II, SSBlocks.SUPER_TANK_III, SSBlocks.SUPER_TANK_IV);
            for (DeferredBlock<?> block : blocks) {
                event.register(block, StoredContentsDecorator.INSTANCE);
            }
        }
    }
}