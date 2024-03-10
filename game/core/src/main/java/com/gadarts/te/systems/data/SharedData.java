package com.gadarts.te.systems.data;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.gadarts.te.systems.map.graph.MapGraph;

public record SharedData(OrthographicCamera camera, MapGraph mapGraph) {
}
