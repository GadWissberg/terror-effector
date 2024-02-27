package com.gadarts.te.common.map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor

@Getter
public class Coords {
    private final int x;
    private final int z;

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
}

