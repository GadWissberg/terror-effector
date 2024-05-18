package com.gadarts.te.systems.map.graph;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.gadarts.te.common.map.Coords;
import com.gadarts.te.common.map.MapNodesTypes;
import com.gadarts.te.components.ComponentsMapper;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("GDXJavaUnsafeIterator")
public class MapGraph implements IndexedGraph<MapGraphNode> {
    public static final float PASSABLE_MAX_HEIGHT_DIFF = 0.3f;
    private static final Array<Connection<MapGraphNode>> auxConnectionsList = new Array<>();
    private static final Vector2 auxVector = new Vector2();
    private final Array<MapGraphNode> nodes;
    @Getter
    private final int width;
    @Getter
    private final int depth;
    private final ImmutableArray<Entity> characters;

    @Setter
    private MapGraphNode currentCalculationDestination;

    public MapGraph(int width, int depth, ImmutableArray<Entity> characters) {
        this.width = width;
        this.depth = depth;
        this.nodes = new Array<>(width * depth);
        this.characters = characters;
        for (int z = 0; z < depth; z++) {
            for (int x = 0; x < width; x++) {
                nodes.add(new MapGraphNode(x, z, MapNodesTypes.values()[MapNodesTypes.PASSABLE_NODE.ordinal()], 8));
            }
        }
    }

    public MapGraphConnection findConnection(MapGraphNode node1, MapGraphNode node2) {
        if (node1 == null || node2 == null) return null;
        MapGraphConnection result = findConnectionBetweenTwoNodes(node1, node2);
        if (result == null) {
            result = findConnectionBetweenTwoNodes(node2, node1);
        }
        return result;
    }

    private MapGraphConnection findConnectionBetweenTwoNodes(MapGraphNode src, MapGraphNode dst) {
        Array<MapGraphConnection> connections = src.getConnections();
        for (MapGraphConnection connection : connections) {
            if (connection.getToNode() == dst) {
                return connection;
            }
        }
        return null;
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

    public MapGraphNode getNode(int x, int z) {
        if (x < 0 || x >= width || z < 0 || z >= depth) return null;

        int index = z * width + x;
        MapGraphNode result = null;
        if (0 <= index && index < width * depth) {
            result = nodes.get(index);
        }
        return result;
    }

    private void addConnection(final MapGraphNode source, final int xOffset, final int yOffset) {
        MapGraphNode target = getNode(source.getX() + xOffset, source.getZ() + yOffset);
        if (target.getType() == MapNodesTypes.PASSABLE_NODE
            && isDiagonalPossible(source, target)
            && Math.abs(target.getHeight() - source.getHeight()) < PASSABLE_MAX_HEIGHT_DIFF) {
            MapGraphConnection connection;
            connection = new MapGraphConnection(source, target);
            source.getConnections().add(connection);
        }
    }

    private boolean isDiagonalBlockedWithEastOrWest(final MapGraphNode source, final int col) {
        float east = getNode(col, source.getZ()).getHeight();
        return Math.abs(source.getHeight() - east) > PASSABLE_MAX_HEIGHT_DIFF;
    }

    private boolean isDiagonalBlockedWithNorthAndSouth(final MapGraphNode target,
                                                       final int srcX,
                                                       final int srcY,
                                                       final float srcHeight) {
        if (srcY < target.getZ()) {
            float bottom = getNode(srcX, srcY + 1).getHeight();
            return Math.abs(srcHeight - bottom) > PASSABLE_MAX_HEIGHT_DIFF;
        } else {
            float top = getNode(srcX, srcY - 1).getHeight();
            return Math.abs(srcHeight - top) > PASSABLE_MAX_HEIGHT_DIFF;
        }
    }

    private boolean isDiagonalPossible(final MapGraphNode source, final MapGraphNode target) {
        if (source.getX() == target.getX() || source.getZ() == target.getZ()) return true;
        if (source.getX() < target.getX()) {
            if (isDiagonalBlockedWithEastOrWest(source, source.getX() + 1)) {
                return false;
            }
        } else if (isDiagonalBlockedWithEastOrWest(source, source.getX() - 1)) {
            return false;
        }
        return !isDiagonalBlockedWithNorthAndSouth(target, source.getX(), source.getZ(), source.getHeight());
    }

    @Override
    public Array<Connection<MapGraphNode>> getConnections(MapGraphNode fromNode) {
        auxConnectionsList.clear();
        Array<MapGraphConnection> connections = fromNode.getConnections();
        for (Connection<MapGraphNode> connection : connections) {
            if (isConnectionAvailable(connection)) {
                auxConnectionsList.add(connection);
            }
        }
        return auxConnectionsList;
    }

    private boolean isConnectionAvailable(Connection<MapGraphNode> connection) {
        boolean result = true;
        for (Entity character : characters) {
            Vector2 characterNodePosition = ComponentsMapper.characterDecal.get(character).getNodePosition(auxVector);
            MapGraphNode toNode = connection.getToNode();
            boolean hasCharacter = toNode.equals(getNode((int) characterNodePosition.x, (int) characterNodePosition.y));
            if (hasCharacter && (!toNode.equals(currentCalculationDestination))) {
                result = false;
                break;
            }
        }
        return result;
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

    public void init( ) {
        applyConnections();
    }
}
