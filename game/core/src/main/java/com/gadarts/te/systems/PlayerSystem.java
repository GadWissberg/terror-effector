package com.gadarts.te.systems;

import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pools;
import com.gadarts.te.SoundPlayer;
import com.gadarts.te.common.assets.GameAssetsManager;
import com.gadarts.te.systems.character.CharacterCommand;
import com.gadarts.te.systems.character.CharacterCommandDefinition;
import com.gadarts.te.systems.data.SharedDataBuilder;
import com.gadarts.te.systems.map.graph.MapGraphNode;

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
            CharacterCommand characterCommand = Pools.get(CharacterCommand.class).obtain();
            Vector2 extraInfo = (Vector2) msg.extraInfo;
            MapGraphNode destination = sessionData.mapGraph().getNode((int) extraInfo.x, (int) extraInfo.y);
            characterCommand.init(CharacterCommandDefinition.GO_TO, destination, sessionData.player());
            sessionData.commandsToExecute().addLast(characterCommand);
        }
        return false;
    }
}
