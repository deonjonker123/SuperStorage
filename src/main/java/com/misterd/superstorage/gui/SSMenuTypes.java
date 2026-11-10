package com.misterd.superstorage.gui;

import com.misterd.superstorage.SuperStorage;
import com.misterd.superstorage.gui.custom.CrateMenu;
import com.misterd.superstorage.gui.custom.SuperChestMenu;
import com.misterd.superstorage.gui.custom.SuperTankMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SSMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, SuperStorage.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<CrateMenu>> CRATE_MENU = MENUS.register("crate_menu",
            () -> IMenuTypeExtension.create(CrateMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<SuperChestMenu>> SUPER_CHEST_MENU = MENUS.register("super_chest_menu",
            () -> IMenuTypeExtension.create(SuperChestMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<SuperTankMenu>> SUPER_TANK_MENU = MENUS.register("super_tank_menu",
            () -> IMenuTypeExtension.create(SuperTankMenu::new));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}