package com.gadarts.te.systems.turns;

import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.Telegram;
import com.gadarts.te.SoundPlayer;
import com.gadarts.te.common.assets.GameAssetsManager;
import com.gadarts.te.systems.GameSystem;
import com.gadarts.te.systems.SystemEvent;
import com.gadarts.te.systems.data.SharedDataBuilder;

public class TurnsSystem extends GameSystem {
    private GameMode mode = GameMode.EXPLORE;

    @Override
    public void initialize(SharedDataBuilder sharedDataBuilder,
                           GameAssetsManager assetsManager,
                           MessageDispatcher eventDispatcher,
                           SoundPlayer soundPlayer) {
        super.initialize(sharedDataBuilder, assetsManager, eventDispatcher, soundPlayer);
        subscribeToEvents(SystemEvent.ENEMY_SPOTTED_PLAYER);
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        if (msg.message == SystemEvent.ENEMY_SPOTTED_PLAYER.ordinal()) {
            if (mode != GameMode.COMBAT) {
                mode = GameMode.COMBAT;
                eventDispatcher.dispatchMessage(SystemEvent.GAME_MODE_CHANGED.ordinal());
            }
        }
        return super.handleMessage(msg);
    }

    @Override
    public void dispose( ) {

    }
}
