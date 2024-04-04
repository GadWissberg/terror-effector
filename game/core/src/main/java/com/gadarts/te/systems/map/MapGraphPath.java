package com.gadarts.te.systems.map;

import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.gadarts.te.systems.map.graph.MapGraphNode;

public class MapGraphPath extends DefaultGraphPath<MapGraphNode> {

    @Override
    public String toString( ) {
        return nodes.toString();
    }
}
