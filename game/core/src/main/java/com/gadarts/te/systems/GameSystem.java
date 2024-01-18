package com.gadarts.te.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.utils.Disposable;
import com.gadarts.te.common.assets.GameAssetsManager;
import com.gadarts.te.systems.data.SharedData;
import com.gadarts.te.systems.data.SharedDataBuilder;

public abstract class GameSystem extends EntitySystem implements Disposable {
    protected SharedData sharedData;
    protected GameAssetsManager assetsManager;

    public void initialize(SharedDataBuilder sharedDataBuilder, GameAssetsManager assetsManager) {
        this.assetsManager = assetsManager;
    }

    public void onSystemReady(SharedData sharedData) {
        this.sharedData = sharedData;
    }
}
