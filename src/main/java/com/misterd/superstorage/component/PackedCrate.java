package com.misterd.superstorage.component;

import com.misterd.superstorage.util.UpgradeType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

import java.util.List;

public record PackedCrate(List<Entry> items, ItemContainerContents upgrades) {
    public static final Codec<PackedCrate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Entry.CODEC.listOf().fieldOf("items").forGetter(PackedCrate::items),
            ItemContainerContents.CODEC.optionalFieldOf("upgrades", ItemContainerContents.EMPTY).forGetter(PackedCrate::upgrades)
    ).apply(instance, PackedCrate::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PackedCrate> STREAM_CODEC = StreamCodec.composite(
            Entry.STREAM_CODEC.apply(ByteBufCodecs.list()), PackedCrate::items,
            ItemContainerContents.STREAM_CODEC, PackedCrate::upgrades,
            PackedCrate::new);

    public NonNullList<ItemStack> upgradeList() {
        NonNullList<ItemStack> list = NonNullList.withSize(UpgradeType.values().length, ItemStack.EMPTY);
        upgrades.copyInto(list);
        return list;
    }

    public int upgradeCount() {
        return (int) upgradeList().stream().filter(stack -> !stack.isEmpty()).count();
    }

    public record Entry(int slot, ItemStack stack) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("slot").forGetter(Entry::slot),
                ItemStack.CODEC.fieldOf("item").forGetter(Entry::stack)
        ).apply(instance, Entry::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Entry> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, Entry::slot,
                ItemStack.STREAM_CODEC, Entry::stack,
                Entry::new);
    }
}