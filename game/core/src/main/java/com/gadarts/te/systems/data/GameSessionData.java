package com.gadarts.te.systems.data;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Queue;
import com.gadarts.te.systems.character.CharacterCommand;
import com.gadarts.te.systems.map.graph.MapGraph;

public record GameSessionData(OrthographicCamera camera,
                              MapGraph mapGraph,
                              Entity player,
                              Stage uiStage,
                              GameModeContainer modeManager,
                              CharacterCommandContainer commandInProgress,
                              Queue<CharacterCommand> commandsToExecute) {
}
