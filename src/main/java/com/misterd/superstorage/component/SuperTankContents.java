package com.misterd.superstorage.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

public record SuperTankContents(FluidResource fluid, long amount) {
    public static final Codec<SuperTankContents> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            FluidResource.CODEC.fieldOf("fluid").forGetter(SuperTankContents::fluid),
            Codec.LONG.fieldOf("amount").forGetter(SuperTankContents::amount)
    ).apply(instance, SuperTankContents::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SuperTankContents> STREAM_CODEC = StreamCodec.composite(
            FluidResource.STREAM_CODEC, SuperTankContents::fluid,
            ByteBufCodecs.VAR_LONG, SuperTankContents::amount,
            SuperTankContents::new);
}