package com.gadarts.te.components;

import com.gadarts.te.common.assets.texture.SurfaceTextures;
import com.gadarts.te.systems.map.graph.MapGraphNode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FloorComponent implements GameComponent {

    @Getter
    @Setter(AccessLevel.NONE)
    private MapGraphNode node;
    @Setter(AccessLevel.NONE)
    private SurfaceTextures definition;

    @Override
    public void reset( ) {

    }

    public void init(MapGraphNode node, SurfaceTextures definition) {
        this.node = node;
        this.definition = definition;
    }

}
