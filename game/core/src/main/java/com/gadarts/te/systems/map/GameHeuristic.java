package com.gadarts.te.systems.map;

import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.math.Vector2;
import com.gadarts.te.systems.map.graph.MapGraphNode;

public class GameHeuristic implements Heuristic<MapGraphNode> {
    private static final Vector2 auxVector = new Vector2();

    @Override
    public float estimate(MapGraphNode node, MapGraphNode endNode) {
        return auxVector.set(node.getX(), node.getZ()).dst2(endNode.getX(), endNode.getZ());
    }
}
