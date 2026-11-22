package com.misterd.superstorage.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.transfer.item.ItemResource;

import java.util.List;
import java.util.function.Predicate;

public record FilterSettings(ItemContainerContents items, boolean blacklist, boolean matchMod, boolean matchNbt, boolean matchDurability) {
    public static final int SIZE = 27;
    public static final int BLACKLIST = 1;
    public static final int MATCH_MOD = 2;
    public static final int MATCH_NBT = 4;
    public static final int MATCH_DURABILITY = 8;
    public static final FilterSettings DEFAULT = new FilterSettings(ItemContainerContents.EMPTY, false, false, false, false);

    public static final Codec<FilterSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemContainerContents.CODEC.optionalFieldOf("items", ItemContainerContents.EMPTY).forGetter(FilterSettings::items),
            Codec.BOOL.optionalFieldOf("blacklist", false).forGetter(FilterSettings::blacklist),
            Codec.BOOL.optionalFieldOf("match_mod", false).forGetter(FilterSettings::matchMod),
            Codec.BOOL.optionalFieldOf("match_nbt", false).forGetter(FilterSettings::matchNbt),
            Codec.BOOL.optionalFieldOf("match_durability", false).forGetter(FilterSettings::matchDurability)
    ).apply(instance, FilterSettings::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FilterSettings> STREAM_CODEC = StreamCodec.composite(
            ItemContainerContents.STREAM_CODEC, FilterSettings::items,
            ByteBufCodecs.BOOL, FilterSettings::blacklist,
            ByteBufCodecs.BOOL, FilterSettings::matchMod,
            ByteBufCodecs.BOOL, FilterSettings::matchNbt,
            ByteBufCodecs.BOOL, FilterSettings::matchDurability,
            FilterSettings::new);

    public static FilterSettings of(ItemStack stack) {
        return stack.getOrDefault(SSDataComponents.FILTER.get(), DEFAULT);
    }

    public NonNullList<ItemStack> list() {
        NonNullList<ItemStack> list = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        items.copyInto(list);
        return list;
    }

    public int count() {
        int count = 0;
        for (ItemStack entry : list()) {
            if (!entry.isEmpty()) {
                count++;
            }
        }
        return count;
    }

    public FilterSettings withItems(NonNullList<ItemStack> list) {
        return new FilterSettings(ItemContainerContents.fromItems(list), blacklist, matchMod, matchNbt, matchDurability);
    }

    public int flags() {
        return (blacklist ? BLACKLIST : 0) | (matchMod ? MATCH_MOD : 0) | (matchNbt ? MATCH_NBT : 0) | (matchDurability ? MATCH_DURABILITY : 0);
    }

    public FilterSettings withFlags(int flags) {
        return new FilterSettings(items, (flags & BLACKLIST) != 0, (flags & MATCH_MOD) != 0, (flags & MATCH_NBT) != 0, (flags & MATCH_DURABILITY) != 0);
    }

    public static Predicate<ItemResource> predicate(ItemStack filter) {
        if (filter.isEmpty()) {
            return resource -> true;
        }
        FilterSettings settings = of(filter);
        List<ItemStack> entries = settings.list().stream().filter(entry -> !entry.isEmpty()).toList();
        return resource -> settings.test(resource.toStack(), entries);
    }

    public boolean test(ItemStack stack) {
        return test(stack, list());
    }

    private boolean test(ItemStack stack, List<ItemStack> entries) {
        boolean matched = false;
        for (ItemStack entry : entries) {
            if (!entry.isEmpty() && matches(entry, stack)) {
                matched = true;
                break;
            }
        }
        return matched != blacklist;
    }

    private boolean matches(ItemStack entry, ItemStack stack) {
        if (matchMod) {
            return namespace(entry).equals(namespace(stack));
        }
        if (!entry.is(stack.getItem())) {
            return false;
        }
        if (matchDurability && entry.getDamageValue() != stack.getDamageValue()) {
            return false;
        }
        return !matchNbt || withoutDamage(entry).equals(withoutDamage(stack));
    }

    private static String namespace(ItemStack stack) {
        return BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace();
    }

    private static DataComponentPatch withoutDamage(ItemStack stack) {
        return stack.getComponentsPatch().forget(type -> type == DataComponents.DAMAGE);
    }
}