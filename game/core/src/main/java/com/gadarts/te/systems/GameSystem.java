package com.gadarts.te.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.utils.Disposable;
import com.gadarts.te.common.assets.GameAssetsManager;
import com.gadarts.te.systems.data.SharedData;
import com.gadarts.te.systems.data.SharedDataBuilder;

public abstract class GameSystem extends EntitySystem implements Disposable {
    protected SharedData sharedData;
    protected GameAssetsManager assetsManager;
    protected MessageDispatcher eventDispatcher;

    public void initialize(SharedDataBuilder sharedDataBuilder, GameAssetsManager assetsManager, MessageDispatcher eventDispatcher) {
        this.assetsManager = assetsManager;
        this.eventDispatcher = eventDispatcher;
    }

    public void onSystemReady(SharedData sharedData) {
        this.sharedData = sharedData;
    }
}
