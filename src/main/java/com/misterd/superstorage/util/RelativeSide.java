package com.misterd.superstorage.util;

import net.minecraft.core.Direction;

public enum RelativeSide {
    TOP,
    BOTTOM,
    FRONT,
    BACK,
    LEFT,
    RIGHT;

    public static RelativeSide of(Direction facing, Direction side) {
        if (side == Direction.UP) {
            return TOP;
        }
        if (side == Direction.DOWN) {
            return BOTTOM;
        }
        if (side == facing) {
            return FRONT;
        }
        if (side == facing.getOpposite()) {
            return BACK;
        }
        return side == facing.getClockWise() ? LEFT : RIGHT;
    }

    public Direction toDirection(Direction facing) {
        return switch (this) {
            case TOP -> Direction.UP;
            case BOTTOM -> Direction.DOWN;
            case FRONT -> facing;
            case BACK -> facing.getOpposite();
            case LEFT -> facing.getClockWise();
            case RIGHT -> facing.getCounterClockWise();
        };
    }
}