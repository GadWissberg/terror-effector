package com.gadarts.te.systems.map.graph;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;
import com.gadarts.te.common.map.Coords;
import com.gadarts.te.common.map.MapNodesTypes;
import lombok.Getter;

@SuppressWarnings("GDXJavaUnsafeIterator")
public class MapGraph implements IndexedGraph<MapGraphNode> {
    private static final Array<Connection<MapGraphNode>> auxConnectionsList = new Array<>();
    private final Array<MapGraphNode> nodes;
    @Getter
    private final int width;
    @Getter
    private final int depth;

    public MapGraph(int width, int depth) {
        this.width = width;
        this.depth = depth;
        this.nodes = new Array<>(width * depth);
        for (int z = 0; z < depth; z++) {
            for (int x = 0; x < width; x++) {
                nodes.add(new MapGraphNode(x, z, MapNodesTypes.values()[MapNodesTypes.PASSABLE_NODE.ordinal()], 8));
            }
        }
        applyConnections();
    }

    void applyConnections( ) {
        for (int row = 0; row < depth; row++) {
            int rows = row * width;
            for (int col = 0; col < width; col++) {
                MapGraphNode n = nodes.get(rows + col);
                if (col > 0) addConnection(n, -1, 0);
                if (col > 0 && row < depth - 1) addConnection(n, -1, 1);
                if (col > 0 && row > 0) addConnection(n, -1, -1);
                if (row > 0) addConnection(n, 0, -1);
                if (row > 0 && col < width - 1) addConnection(n, 1, -1);
                if (col < width - 1) addConnection(n, 1, 0);
                if (col < width - 1 && row < depth - 1) addConnection(n, 1, 1);
                if (row < depth - 1) addConnection(n, 0, 1);
            }
        }
    }

    public MapGraphNode getNode(final int col, final int row) {
        if (col < 0 || col >= width || row < 0 || row >= depth) return null;

        int index = row * width + col;
        MapGraphNode result = null;
        if (0 <= index && index < width * depth) {
            result = nodes.get(index);
        }
        return result;
    }

    private void addConnection(final MapGraphNode source, final int xOffset, final int yOffset) {
        MapGraphNode target = getNode(source.getX() + xOffset, source.getZ() + yOffset);
        if (target.getType() == MapNodesTypes.PASSABLE_NODE) {
            MapGraphConnection connection;
            connection = new MapGraphConnection(source, target);
            source.getConnections().add(connection);
        }
    }

    @Override
    public Array<Connection<MapGraphNode>> getConnections(MapGraphNode fromNode) {
        auxConnectionsList.clear();
        Array<MapGraphConnection> connections = fromNode.getConnections();
        for (Connection<MapGraphNode> connection : connections) {
            auxConnectionsList.add(connection);
        }
        return auxConnectionsList;
    }

    @Override
    public int getIndex(MapGraphNode node) {
        return node.getIndex(width);
    }

    @Override
    public int getNodeCount( ) {
        return nodes.size;
    }

    public MapGraphNode getNode(final Coords coord) {
        return getNode(coord.getX(), coord.getZ());
    }

}
