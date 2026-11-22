package com.misterd.superstorage.component;

import com.misterd.superstorage.util.SideMode;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record HopperSettings(List<SideMode> sides, ItemContainerContents filters) {
    public static final int INPUT_FILTER = 0;
    public static final int OUTPUT_FILTER = 1;
    public static final int FILTER_COUNT = 2;
    public static final HopperSettings DEFAULT = new HopperSettings(Collections.nCopies(6, SideMode.NONE), ItemContainerContents.EMPTY);

    public static final Codec<HopperSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SideMode.CODEC.listOf(6, 6).optionalFieldOf("sides", DEFAULT.sides()).forGetter(HopperSettings::sides),
            ItemContainerContents.CODEC.optionalFieldOf("filters", ItemContainerContents.EMPTY).forGetter(HopperSettings::filters)
    ).apply(instance, HopperSettings::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, HopperSettings> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.idMapper(i -> SideMode.values()[i], SideMode::ordinal).apply(ByteBufCodecs.list(6)), HopperSettings::sides,
            ItemContainerContents.STREAM_CODEC, HopperSettings::filters,
            HopperSettings::new);

    public static HopperSettings of(ItemStack upgrade) {
        return upgrade.getOrDefault(SSDataComponents.HOPPER_SETTINGS.get(), DEFAULT);
    }

    public SideMode side(Direction direction) {
        return sides.get(direction.ordinal());
    }

    public HopperSettings withSide(Direction direction, SideMode mode) {
        List<SideMode> copy = new ArrayList<>(sides);
        copy.set(direction.ordinal(), mode);
        return new HopperSettings(List.copyOf(copy), filters);
    }

    public ItemStack filter(int index) {
        NonNullList<ItemStack> list = NonNullList.withSize(FILTER_COUNT, ItemStack.EMPTY);
        filters.copyInto(list);
        return list.get(index);
    }

    public HopperSettings withFilters(NonNullList<ItemStack> list) {
        return new HopperSettings(sides, ItemContainerContents.fromItems(list));
    }

    public int encodeSides() {
        int bits = 0;
        for (int i = 0; i < sides.size(); i++) {
            bits |= sides.get(i).ordinal() << (i * 2);
        }
        return bits;
    }

    public NonNullList<ItemStack> filterList() {
        NonNullList<ItemStack> list = NonNullList.withSize(FILTER_COUNT, ItemStack.EMPTY);
        filters.copyInto(list);
        return list;
    }
}