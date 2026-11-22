package com.misterd.superstorage.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record CompactingSettings(boolean reversibleOnly, boolean threeByThree, ItemStack filter) {
    public static final int REVERSIBLE_ONLY = 1;
    public static final int THREE_BY_THREE = 2;
    public static final CompactingSettings DEFAULT = new CompactingSettings(true, true, ItemStack.EMPTY);

    public static final Codec<CompactingSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("reversible_only", true).forGetter(CompactingSettings::reversibleOnly),
            Codec.BOOL.optionalFieldOf("three_by_three", true).forGetter(CompactingSettings::threeByThree),
            ItemStack.OPTIONAL_CODEC.optionalFieldOf("filter", ItemStack.EMPTY).forGetter(CompactingSettings::filter)
    ).apply(instance, CompactingSettings::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CompactingSettings> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, CompactingSettings::reversibleOnly,
            ByteBufCodecs.BOOL, CompactingSettings::threeByThree,
            ItemStack.OPTIONAL_STREAM_CODEC, CompactingSettings::filter,
            CompactingSettings::new);

    public static CompactingSettings of(ItemStack upgrade) {
        return upgrade.getOrDefault(SSDataComponents.COMPACTING_SETTINGS.get(), DEFAULT);
    }

    public int size() {
        return threeByThree ? 3 : 2;
    }

    public int flags() {
        return (reversibleOnly ? REVERSIBLE_ONLY : 0) | (threeByThree ? THREE_BY_THREE : 0);
    }

    public CompactingSettings withFlags(int flags) {
        return new CompactingSettings((flags & REVERSIBLE_ONLY) != 0, (flags & THREE_BY_THREE) != 0, filter);
    }

    public CompactingSettings withFilter(ItemStack stack) {
        return new CompactingSettings(reversibleOnly, threeByThree, stack);
    }
}