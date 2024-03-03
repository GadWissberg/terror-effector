package com.gadarts.te.common.map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Coords {
    private final int x;
    private final int z;

    public Coords(Coords coords) {
        this(coords.getX(), coords.getZ());
    }

    @Override
    public String toString( ) {
        return "Coords{" +
            "x=" + x +
            ", z=" + z +
            '}';
    }

    public boolean equals(int z, int x) {
        return this.z == z && this.x == x;
    }

    public boolean equals(Coords coords) {
        if (coords == null) return false;
        if (this == coords) return true;
        return this.equals(coords.getZ(), coords.getX());
    }
}

