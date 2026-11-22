package com.misterd.superstorage.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public record CollectorSettings(List<Integer> values, ItemStack filter) {
    public static final int SIZE_X = 0;
    public static final int SIZE_Y = 1;
    public static final int SIZE_Z = 2;
    public static final int OFFSET_X = 3;
    public static final int OFFSET_Y = 4;
    public static final int OFFSET_Z = 5;
    public static final int VALUE_COUNT = 6;
    public static final List<Integer> DEFAULT_VALUES = List.of(5, 3, 5, 0, 0, 0);
    public static final CollectorSettings DEFAULT = new CollectorSettings(DEFAULT_VALUES, ItemStack.EMPTY);

    public static final Codec<CollectorSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.listOf(VALUE_COUNT, VALUE_COUNT).optionalFieldOf("area", DEFAULT_VALUES).forGetter(CollectorSettings::values),
            ItemStack.OPTIONAL_CODEC.optionalFieldOf("filter", ItemStack.EMPTY).forGetter(CollectorSettings::filter)
    ).apply(instance, CollectorSettings::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CollectorSettings> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list(VALUE_COUNT)), CollectorSettings::values,
            ItemStack.OPTIONAL_STREAM_CODEC, CollectorSettings::filter,
            CollectorSettings::new);

    public static CollectorSettings of(ItemStack upgrade) {
        return upgrade.getOrDefault(SSDataComponents.COLLECTOR_SETTINGS.get(), DEFAULT);
    }

    public static boolean isSize(int index) {
        return index < OFFSET_X;
    }

    public int value(int index) {
        return values.get(index);
    }

    public CollectorSettings withValue(int index, int value) {
        List<Integer> copy = new ArrayList<>(values);
        copy.set(index, value);
        return new CollectorSettings(List.copyOf(copy), filter);
    }

    public CollectorSettings withFilter(ItemStack stack) {
        return new CollectorSettings(values, stack);
    }

    public CollectorSettings reset() {
        return new CollectorSettings(DEFAULT_VALUES, filter);
    }

    public AABB area(BlockPos pos) {
        return area(pos, value(SIZE_X), value(SIZE_Y), value(SIZE_Z), value(OFFSET_X), value(OFFSET_Y), value(OFFSET_Z));
    }

    public static AABB area(BlockPos pos, int sizeX, int sizeY, int sizeZ, int offsetX, int offsetY, int offsetZ) {
        int minX = pos.getX() + offsetX - (sizeX - 1) / 2;
        int minY = pos.getY() + offsetY - (sizeY - 1) / 2;
        int minZ = pos.getZ() + offsetZ - (sizeZ - 1) / 2;
        return new AABB(minX, minY, minZ, minX + sizeX, minY + sizeY, minZ + sizeZ);
    }
}