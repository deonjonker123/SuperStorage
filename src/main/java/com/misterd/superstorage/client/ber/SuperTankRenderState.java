package com.misterd.superstorage.client.ber;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

public class SuperTankRenderState extends BlockEntityRenderState {
    public Direction facing = Direction.NORTH;
    public boolean visible;
    public String count = "";
    public @Nullable TextureAtlasSprite sprite;
    public int color = -1;
    public float fill;
    public boolean locked;
    public boolean voidOverflow;
}