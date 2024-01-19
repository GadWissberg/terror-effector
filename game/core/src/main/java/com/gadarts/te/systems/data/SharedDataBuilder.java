package com.gadarts.te.systems.data;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.gadarts.te.systems.map.graph.MapGraph;
import lombok.Setter;

@Setter
public class SharedDataBuilder {
    private MapGraph mapGraph;
    private OrthographicCamera camera;

    public SharedData build( ) {
        return new SharedData(camera, mapGraph);
    }
}
