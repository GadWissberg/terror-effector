package com.gadarts.te.systems.data;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.gadarts.te.systems.map.graph.MapGraph;
import lombok.Setter;

@Setter
public class SharedDataBuilder {
    private MapGraph mapGraph;
    private OrthographicCamera camera;
    private Entity player;

    public SharedData build( ) {
        return new SharedData(camera, mapGraph, player);
    }
}
