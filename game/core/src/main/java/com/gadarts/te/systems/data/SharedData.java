package com.gadarts.te.systems.data;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.gadarts.te.systems.map.graph.MapGraph;
import lombok.Getter;

@Getter
public class SharedData {
    private final OrthographicCamera camera;
    private final MapGraph mapGraph;

    SharedData(OrthographicCamera camera, MapGraph mapGraph) {
        this.camera = camera;
        this.mapGraph = mapGraph;
    }

}
