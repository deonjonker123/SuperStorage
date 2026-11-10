package com.misterd.superstorage.util;

import com.misterd.superstorage.SuperStorage;
import com.mojang.serialization.Codec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;

public enum CrateTier implements StringRepresentable {
    WOODEN("wooden", 9, 3, "wooden_crate_gui", 176, 160, 8, 77),
    COPPER("copper", 9, 6, "copper_crate_gui", 176, 214, 8, 131),
    IRON("iron", 9, 8, "iron_crate_gui", 176, 250, 8, 167),
    GOLD("gold", 12, 8, "gold_crate_gui", 230, 250, 38, 167),
    EMERALD("emerald", 12, 10, "big_crates_gui", 244, 250, 38, 167),
    DIAMOND("diamond", 12, 12, "big_crates_gui", 244, 250, 38, 167),
    NETHERITE("netherite", 12, 14, "big_crates_gui", 244, 250, 38, 167);

    public static final Codec<CrateTier> CODEC = StringRepresentable.fromEnum(CrateTier::values);

    public static final int MAX_VISIBLE_ROWS = 8;
    public static final int SLOT_X = 8;
    public static final int SLOT_Y = 8;
    public static final int SLOT_SIZE = 18;
    public static final int HOTBAR_OFFSET = 59;

    public static final int SCROLL_TRACK_X = 227;
    public static final int SCROLL_TRACK_Y = 7;
    public static final int SCROLL_TRACK_HEIGHT = 144;
    public static final int SCROLL_KNOB_WIDTH = 8;
    public static final int SCROLL_KNOB_HEIGHT = 10;

    private final String name;
    private final int columns;
    private final int rows;
    private final Identifier texture;
    private final int guiWidth;
    private final int guiHeight;
    private final int playerInvX;
    private final int playerInvY;

    CrateTier(String name, int columns, int rows, String texture, int guiWidth, int guiHeight, int playerInvX, int playerInvY) {
        this.name = name;
        this.columns = columns;
        this.rows = rows;
        this.texture = Identifier.fromNamespaceAndPath(SuperStorage.MODID, "textures/gui/" + texture + ".png");
        this.guiWidth = guiWidth;
        this.guiHeight = guiHeight;
        this.playerInvX = playerInvX;
        this.playerInvY = playerInvY;
    }

    public int columns() {
        return columns;
    }

    public int rows() {
        return rows;
    }

    public int slotCount() {
        return columns * rows;
    }

    public int visibleRows() {
        return Math.min(rows, MAX_VISIBLE_ROWS);
    }

    public int hiddenRows() {
        return rows - visibleRows();
    }

    public boolean scrolls() {
        return rows > MAX_VISIBLE_ROWS;
    }

    public Identifier texture() {
        return texture;
    }

    public int guiWidth() {
        return guiWidth;
    }

    public int guiHeight() {
        return guiHeight;
    }

    public int playerInvX() {
        return playerInvX;
    }

    public int playerInvY() {
        return playerInvY;
    }

    public int hotbarY() {
        return playerInvY + HOTBAR_OFFSET;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public static CrateTier byId(int id) {
        CrateTier[] values = values();
        return values[Math.floorMod(id, values.length)];
    }
}