package com.gadarts.te.common;

import lombok.Getter;

@Getter
public enum WallObjects {
    WALL_WITH_POLE("Wall with railing");

    private final String displayName;

    WallObjects(String displayName) {
        this.displayName = displayName;
    }
}
