package com.gadarts.te.systems.data;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.gadarts.te.systems.map.graph.MapGraph;
import lombok.Getter;

@Getter
public record SharedData(OrthographicCamera camera, MapGraph mapGraph) {
}
