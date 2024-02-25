package com.gadarts.te.renderer.handlers.cursor.react

import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.ai.msg.Telegram
import com.gadarts.te.EditorEvents
import com.gadarts.te.Modes
import com.gadarts.te.common.assets.GameAssetsManager
import com.gadarts.te.common.map.WallCreator
import com.gadarts.te.renderer.handlers.HandlerOnEvent
import com.gadarts.te.renderer.handlers.HandlersData
import com.gadarts.te.renderer.handlers.cursor.CursorHandler

class CursorHandlerOnTextureSet(private val cursorHandler: CursorHandler) : HandlerOnEvent {
    override fun react(
        msg: Telegram,
        handlersData: HandlersData,
        gameAssetsManager: GameAssetsManager,
        dispatcher: MessageDispatcher,
        wallCreator: WallCreator
    ) {
        if (cursorHandler.selectedMode == Modes.FLOOR && cursorHandler.selectedNodes.isNotEmpty()) {
            dispatcher.dispatchMessage(
                EditorEvents.TEXTURE_SELECTED_FOR_NODES.ordinal,
                cursorHandler.selectedNodes.map { it.coords }
            )
        } else if (cursorHandler.selectedMode == Modes.WALLS && cursorHandler.selectedWalls.isNotEmpty()) {
            dispatcher.dispatchMessage(
                EditorEvents.TEXTURE_SELECTED_FOR_WALLS.ordinal,
                cursorHandler.selectedWalls
            )
        }
    }

}
