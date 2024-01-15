package com.gadarts.te.systems.map.graph;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import lombok.Getter;
import lombok.Setter;

@Getter
public class MapGraphNode {
    private final Array<MapGraphConnection> connections;
    private final int col;
    private final int row;

    @Setter
    private Entity door;
    @Setter
    private int nodeAmbientOcclusionValue;
    @Setter
    private MapNodesTypes type;
    @Setter
    private float height;
    @Setter
    private Entity entity;
    @Setter
    private boolean reachable;

    public MapGraphNode(final int col, final int row, final MapNodesTypes type, final int connections) {
        this.col = col;
        this.row = row;
        this.type = type;
        this.connections = new Array<>(connections);
    }

    @Override
    public String toString( ) {
        return "MapGraphNode{" +
            "x=" + col +
            ", y=" + row +
            '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MapGraphNode that = (MapGraphNode) o;

        if (col != that.col) return false;
        if (row != that.row) return false;
        if (type != that.type) return false;
        return connections.equals(that.connections);
    }

    public int getIndex(int mapWidth) {
        return row * mapWidth + col;
    }
}
