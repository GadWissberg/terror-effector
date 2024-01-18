package com.gadarts.te.components;

import com.gadarts.te.systems.map.graph.MapGraphNode;
import lombok.Getter;
import lombok.Setter;

@Getter
public class WallComponent implements GameComponent {
    private MapGraphNode parentNode;
    @Setter
    private boolean applyGrayScale;

    public void init(final MapGraphNode parentNode) {
        this.parentNode = parentNode;
    }

    @Override
    public void reset( ) {

    }
}
