package com.gadarts.te.systems.map.graph;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;

@SuppressWarnings("GDXJavaUnsafeIterator")
public class MapGraph implements IndexedGraph<MapGraphNode> {
    private static final Array<Connection<MapGraphNode>> auxConnectionsList = new Array<>();
    private final Array<MapGraphNode> nodes;
    private final int mapSize;

    public MapGraph(int mapSize) {
        this.mapSize = mapSize;
        this.nodes = new Array<>(mapSize * mapSize);
        for (int row = 0; row < mapSize; row++) {
            for (int col = 0; col < mapSize; col++) {
                nodes.add(new MapGraphNode(col, row, MapNodesTypes.values()[MapNodesTypes.PASSABLE_NODE.ordinal()], 8));
            }
        }
        applyConnections();
    }

    void applyConnections( ) {
        for (int row = 0; row < mapSize; row++) {
            int rows = row * mapSize;
            for (int col = 0; col < mapSize; col++) {
                MapGraphNode n = nodes.get(rows + col);
                if (col > 0) addConnection(n, -1, 0);
                if (col > 0 && row < mapSize - 1) addConnection(n, -1, 1);
                if (col > 0 && row > 0) addConnection(n, -1, -1);
                if (row > 0) addConnection(n, 0, -1);
                if (row > 0 && col < mapSize - 1) addConnection(n, 1, -1);
                if (col < mapSize - 1) addConnection(n, 1, 0);
                if (col < mapSize - 1 && row < mapSize - 1) addConnection(n, 1, 1);
                if (row < mapSize - 1) addConnection(n, 0, 1);
            }
        }
    }

    public MapGraphNode getNode(final int col, final int row) {
        if (col < 0 || col >= mapSize || row < 0 || row >= mapSize) return null;

        int index = row * mapSize + col;
        MapGraphNode result = null;
        if (0 <= index && index < mapSize * mapSize) {
            result = nodes.get(index);
        }
        return result;
    }

    private void addConnection(final MapGraphNode source, final int xOffset, final int yOffset) {
        MapGraphNode target = getNode(source.getCol() + xOffset, source.getRow() + yOffset);
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
        return node.getIndex(mapSize);
    }

    @Override
    public int getNodeCount( ) {
        return nodes.size;
    }
}
