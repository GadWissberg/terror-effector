package com.gadarts.te.systems.data;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.gadarts.te.systems.map.graph.MapGraph;

public record GameSessionData(OrthographicCamera camera, MapGraph mapGraph, Entity player, Stage uiStage) {
}
