package com.gadarts.te.systems;

import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.Telegram;
import com.gadarts.te.SoundPlayer;
import com.gadarts.te.common.assets.GameAssetsManager;
import com.gadarts.te.systems.data.SharedDataBuilder;

public class PlayerSystem extends GameSystem {
    @Override
    public void initialize(SharedDataBuilder sharedDataBuilder,
                           GameAssetsManager assetsManager,
                           MessageDispatcher eventDispatcher,
                           SoundPlayer soundPlayer) {
        super.initialize(sharedDataBuilder, assetsManager, eventDispatcher, soundPlayer);
        subscribeToEvents(SystemEvent.USER_CLICKED_NODE);
    }


    @Override
    public void dispose( ) {

    }

    @Override
    public boolean handleMessage(Telegram msg) {
        if (msg.message == SystemEvent.USER_CLICKED_NODE.ordinal()) {
            eventDispatcher.dispatchMessage(SystemEvent.PLAYER_REQUESTS_MOVE.ordinal(), msg.extraInfo);
        }
        return false;
    }
}
