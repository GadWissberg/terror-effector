package com.gadarts.te.systems.data;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.gadarts.te.systems.map.graph.MapGraph;
import lombok.Setter;

@Setter
public class SharedDataBuilder {
    private MapGraph mapGraph;
    private OrthographicCamera camera;
    private Entity player;
    private Stage uiStage;

    public GameSessionData build( ) {
        return new GameSessionData(camera, mapGraph, player, uiStage, new GameModeContainer(), new CharacterCommandContainer());
    }
}
