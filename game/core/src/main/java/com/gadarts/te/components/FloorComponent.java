package com.gadarts.te.components;

import com.badlogic.ashley.core.Entity;
import com.gadarts.te.common.assets.texture.SurfaceTextures;
import com.gadarts.te.systems.map.graph.MapGraphNode;
import lombok.Getter;
import lombok.Setter;

@Getter
public class FloorComponent implements GameComponent {

    @Getter
    private MapGraphNode node;
    private SurfaceTextures definition;
    @Setter
    private Entity containedCharacter;

    @Override
    public void reset( ) {

    }

    public void init(MapGraphNode node, SurfaceTextures definition) {
        this.node = node;
        this.definition = definition;
    }

}
