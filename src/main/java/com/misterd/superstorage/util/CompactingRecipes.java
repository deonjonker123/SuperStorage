package com.misterd.superstorage.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CompactingRecipes {
    private static final Map<Key, ItemStack> CACHE = new HashMap<>();
    private static RecipeManager cachedFor;

    private CompactingRecipes() {
    }

    public static ItemStack result(ServerLevel level, Item item, int size, boolean reversibleOnly) {
        RecipeManager manager = level.recipeAccess();
        if (manager != cachedFor) {
            CACHE.clear();
            cachedFor = manager;
        }
        return CACHE.computeIfAbsent(new Key(item, size, reversibleOnly), key -> lookup(level, key)).copy();
    }

    private static ItemStack lookup(ServerLevel level, Key key) {
        int needed = key.size() * key.size();
        ItemStack input = new ItemStack(key.item());
        ItemStack result = craft(level, CraftingInput.of(key.size(), key.size(), Collections.nCopies(needed, input)));
        if (result.isEmpty() || result.is(key.item()) || !key.reversibleOnly()) {
            return result.is(key.item()) ? ItemStack.EMPTY : result;
        }
        ItemStack back = craft(level, CraftingInput.of(1, 1, List.of(result.copyWithCount(1))));
        return back.is(key.item()) && back.getCount() * result.getCount() == needed ? result : ItemStack.EMPTY;
    }

    private static ItemStack craft(ServerLevel level, CraftingInput input) {
        return level.recipeAccess().getRecipeFor(RecipeType.CRAFTING, input, level)
                .map(holder -> holder.value().assemble(input))
                .orElse(ItemStack.EMPTY);
    }

    private record Key(Item item, int size, boolean reversibleOnly) {
    }
}