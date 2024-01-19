package com.gadarts.te.systems.data;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.gadarts.te.systems.map.graph.MapGraph;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SharedData {
    private final OrthographicCamera camera;
    private final MapGraph mapGraph;


}
