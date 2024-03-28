package com.gadarts.te.systems.render;

import com.badlogic.gdx.graphics.Camera;
import com.gadarts.te.common.assets.GameAssetsManager;
import lombok.Getter;

@Getter
public class DecalsGroupStrategies {
    private GameCameraGroupStrategy regularDecalGroupStrategy;

    void createDecalGroupStrategies(Camera camera, GameAssetsManager assetsManager) {
        regularDecalGroupStrategy = new GameCameraGroupStrategy(camera, assetsManager);
    }
}
