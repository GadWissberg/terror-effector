package com.gadarts.te.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.utils.Disposable;
import com.gadarts.te.common.assets.GameAssetsManager;
import com.gadarts.te.systems.data.SharedData;
import com.gadarts.te.systems.data.SharedDataBuilder;

import java.util.Arrays;

public abstract class GameSystem extends EntitySystem implements Disposable, Telegraph {
    protected SharedData sharedData;
    protected GameAssetsManager assetsManager;
    protected MessageDispatcher eventDispatcher;

    public void initialize(SharedDataBuilder sharedDataBuilder, GameAssetsManager assetsManager, MessageDispatcher eventDispatcher) {
        this.assetsManager = assetsManager;
        this.eventDispatcher = eventDispatcher;
    }

    protected void subscribeToEvents(SystemEvent... systemEvents) {
        Arrays.stream(systemEvents).forEach(event -> eventDispatcher.addListeners(GameSystem.this, event.ordinal()));
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        return false;
    }

    public void onSystemReady(SharedData sharedData) {
        this.sharedData = sharedData;
    }
}
