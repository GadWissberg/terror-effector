package com.gadarts.te.common.map;

public record Coords(int row, int col) {
    public boolean equals(int row, int col) {
        return this.row == row && this.col == col;
    }
}

