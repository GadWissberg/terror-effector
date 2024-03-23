package com.gadarts.te.systems.map.graph;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.gadarts.te.common.map.MapNodesTypes;
import lombok.Getter;
import lombok.Setter;

@Getter
public class MapGraphNode {
    private final Array<MapGraphConnection> connections;
    private final int x;
    private final int z;

    @Setter
    private MapNodesTypes type;
    @Setter
    private float height;
    @Setter
    private Entity entity;

    public MapGraphNode(final int col, final int z, final MapNodesTypes type, final int connections) {
        this.x = col;
        this.z = z;
        this.type = type;
        this.connections = new Array<>(connections);
    }

    @Override
    public String toString( ) {
        return "MapGraphNode{" +
            "x=" + x +
            ", y=" + z +
            '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MapGraphNode that = (MapGraphNode) o;

        if (x != that.x) return false;
        if (z != that.z) return false;
        if (type != that.type) return false;
        return connections.equals(that.connections);
    }

    public int getIndex(int mapWidth) {
        return z * mapWidth + x;
    }
}
