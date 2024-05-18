package com.gadarts.te.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.utils.Disposable;
import com.gadarts.te.SoundPlayer;
import com.gadarts.te.common.assets.GameAssetsManager;
import com.gadarts.te.systems.data.GameSessionData;
import com.gadarts.te.systems.data.SharedDataBuilder;

import java.util.Arrays;

public abstract class GameSystem extends EntitySystem implements Disposable, Telegraph {
    protected GameSessionData sessionData;
    protected GameAssetsManager assetsManager;
    protected MessageDispatcher eventDispatcher;
    protected SoundPlayer soundPlayer;

    public void initialize(SharedDataBuilder sharedDataBuilder,
                           GameAssetsManager assetsManager,
                           MessageDispatcher eventDispatcher,
                           SoundPlayer soundPlayer) {
        this.assetsManager = assetsManager;
        this.eventDispatcher = eventDispatcher;
        this.soundPlayer = soundPlayer;
    }

    protected void subscribeToEvents(SystemEvent... systemEvents) {
        Arrays.stream(systemEvents).forEach(event -> eventDispatcher.addListeners(GameSystem.this, event.ordinal()));
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        return false;
    }

    public void onSystemReady(GameSessionData gameSessionData) {
        this.sessionData = gameSessionData;
    }
}
