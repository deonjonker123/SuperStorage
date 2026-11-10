package com.misterd.superstorage.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.transfer.item.ItemResource;

public record SuperChestContents(ItemResource item, long count) {
    public static final Codec<SuperChestContents> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemResource.CODEC.fieldOf("item").forGetter(SuperChestContents::item),
            Codec.LONG.fieldOf("count").forGetter(SuperChestContents::count)
    ).apply(instance, SuperChestContents::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SuperChestContents> STREAM_CODEC = StreamCodec.composite(
            ItemResource.STREAM_CODEC, SuperChestContents::item,
            ByteBufCodecs.VAR_LONG, SuperChestContents::count,
            SuperChestContents::new);
}