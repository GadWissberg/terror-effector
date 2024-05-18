package com.gadarts.te.systems.turns;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.utils.Queue;
import com.gadarts.te.SoundPlayer;
import com.gadarts.te.common.assets.GameAssetsManager;
import com.gadarts.te.components.ComponentsMapper;
import com.gadarts.te.systems.GameSystem;
import com.gadarts.te.systems.SystemEvent;
import com.gadarts.te.systems.data.GameModeContainer;
import com.gadarts.te.systems.data.SharedDataBuilder;

public class TurnsSystem extends GameSystem {

    private final Queue<Entity> turns = new Queue<>();

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
        boolean handled = false;
        if (msg.message == SystemEvent.ENEMY_SPOTTED_PLAYER.ordinal()) {
            GameModeContainer gameModeContainer = sessionData.modeManager();
            if (gameModeContainer.getMode() != GameMode.COMBAT) {
                gameModeContainer.setMode(GameMode.COMBAT);
                turns.clear();
                turns.addFirst((Entity) msg.extraInfo);
                turns.addLast(sessionData.player());
                eventDispatcher.dispatchMessage(SystemEvent.GAME_MODE_CHANGED.ordinal());
                handled = true;
            }
        }
        return handled;
    }

    @Override
    public void update(float deltaTime) {
        if (sessionData.modeManager().getMode() == GameMode.COMBAT && sessionData.commandInProgress().getCommand() == null) {
            Entity current = turns.first();
            if (ComponentsMapper.enemy.has(current)) {
                eventDispatcher.dispatchMessage(SystemEvent.ENEMY_NEW_TURN.ordinal(), current);
            }
        }
    }

    @Override
    public void dispose( ) {

    }
}
