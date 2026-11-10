package com.misterd.superstorage.compat.jei;

import com.misterd.superstorage.SuperStorage;
import com.misterd.superstorage.block.SSBlocks;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.List;

@JeiPlugin
public class SuperStorageJeiPlugin implements IModPlugin {
    private static final Identifier UID = Identifier.fromNamespaceAndPath(SuperStorage.MODID, "jei_plugin");

    @Override
    public Identifier getPluginUid() {
        return UID;
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        runtime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, List.of(
                new ItemStack(SSBlocks.CREATIVE_SUPER_CHEST.get()),
                new ItemStack(SSBlocks.CREATIVE_SUPER_TANK.get())));
    }
}