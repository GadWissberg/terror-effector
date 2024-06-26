package com.gadarts.te.renderer.handlers.cursor.react

import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.ai.msg.Telegram
import com.gadarts.te.common.assets.GameAssetsManager
import com.gadarts.te.common.assets.definitions.character.CharacterDefinition
import com.gadarts.te.common.assets.definitions.env.EnvObjectDefinition
import com.gadarts.te.common.map.WallCreator
import com.gadarts.te.renderer.handlers.HandlerOnEvent
import com.gadarts.te.renderer.handlers.HandlersData
import com.gadarts.te.renderer.handlers.cursor.CursorHandler

class CursorHandlerOnClickedTreeNode(private val cursorHandler: CursorHandler) : HandlerOnEvent {
    override fun react(
        msg: Telegram,
        handlersData: HandlersData,
        gameAssetsManager: GameAssetsManager,
        dispatcher: MessageDispatcher,
        wallCreator: WallCreator
    ) {
        if (msg.extraInfo is CharacterDefinition) {
            cursorHandler.displayCharacterCursor(msg.extraInfo as CharacterDefinition)
        } else {
            val envObjectDefinition = msg.extraInfo as EnvObjectDefinition
            cursorHandler.displayObjectOfTreeNode(envObjectDefinition)
        }

    }

}
