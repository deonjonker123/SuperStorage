package com.misterd.superstorage.util;

import com.misterd.superstorage.item.custom.UpgradeItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

public class CrateUpgrades extends ItemStacksResourceHandler {
    private final Runnable onChanged;

    public CrateUpgrades(Runnable onChanged) {
        super(UpgradeType.values().length);
        this.onChanged = onChanged;
    }

    @Override
    public boolean isValid(int index, ItemResource resource) {
        return resource.getItem() instanceof UpgradeItem upgrade && upgrade.getType().ordinal() == index;
    }

    @Override
    protected int getCapacity(int index, ItemResource resource) {
        return 1;
    }

    @Override
    protected void onContentsChanged(int index, ItemStack previousContents) {
        onChanged.run();
    }

    public boolean has(UpgradeType type) {
        return !getResource(type.ordinal()).isEmpty();
    }

    public ItemStack get(UpgradeType type) {
        ItemResource resource = getResource(type.ordinal());
        return resource.isEmpty() ? ItemStack.EMPTY : resource.toStack(1);
    }

    public void put(UpgradeType type, ItemStack stack) {
        set(type.ordinal(), ItemResource.of(stack), stack.getCount());
    }
}