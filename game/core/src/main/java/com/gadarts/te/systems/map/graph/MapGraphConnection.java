package com.gadarts.te.systems.map.graph;

import com.badlogic.gdx.ai.pfa.Connection;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MapGraphConnection implements Connection<MapGraphNode> {
    private final MapGraphNode source;
    private final MapGraphNode dest;

    @Override
    public float getCost( ) {
        return 0;
    }

    @Override
    public MapGraphNode getFromNode( ) {
        return source;
    }

    @Override
    public MapGraphNode getToNode( ) {
        return dest;
    }

}
