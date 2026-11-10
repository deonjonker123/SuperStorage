package com.misterd.superstorage.component;

import com.misterd.superstorage.util.SideMode;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Collections;
import java.util.List;

public record SuperSettings(boolean locked, boolean voidOverflow, boolean capabilitiesEnabled, List<SideMode> sides) {
    public static final SuperSettings DEFAULT = new SuperSettings(false, false, true, Collections.nCopies(6, SideMode.NONE));

    public static final Codec<SuperSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("locked").forGetter(SuperSettings::locked),
            Codec.BOOL.fieldOf("void_overflow").forGetter(SuperSettings::voidOverflow),
            Codec.BOOL.fieldOf("capabilities_enabled").forGetter(SuperSettings::capabilitiesEnabled),
            SideMode.CODEC.listOf(6, 6).fieldOf("sides").forGetter(SuperSettings::sides)
    ).apply(instance, SuperSettings::new));

    public static final StreamCodec<ByteBuf, SuperSettings> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, SuperSettings::locked,
            ByteBufCodecs.BOOL, SuperSettings::voidOverflow,
            ByteBufCodecs.BOOL, SuperSettings::capabilitiesEnabled,
            ByteBufCodecs.idMapper(i -> SideMode.values()[i], SideMode::ordinal).apply(ByteBufCodecs.list(6)), SuperSettings::sides,
            SuperSettings::new);
}