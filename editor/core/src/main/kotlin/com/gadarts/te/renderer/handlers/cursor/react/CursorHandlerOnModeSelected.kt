package com.gadarts.te.renderer.handlers.cursor.react

import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.gadarts.te.Modes
import com.gadarts.te.common.assets.GameAssetsManager
import com.gadarts.te.common.map.WallCreator
import com.gadarts.te.renderer.handlers.HandlerOnEvent
import com.gadarts.te.renderer.handlers.HandlersData
import com.gadarts.te.renderer.handlers.cursor.CursorHandler

class CursorHandlerOnModeSelected(private val cursorHandler: CursorHandler) : HandlerOnEvent {
    override fun react(
        msg: Telegram,
        handlersData: HandlersData,
        gameAssetsManager: GameAssetsManager,
        dispatcher: MessageDispatcher,
        wallCreator: WallCreator
    ) {
        val mode = (msg.extraInfo as Modes)
        cursorHandler.selectedMode = mode
        if (cursorHandler.selectedMode == Modes.FLOOR) {
            cursorHandler.setCursorToFloorModel()
        }
        else if (cursorHandler.selectedMode == Modes.ENV_OBJECTS) {
            cursorHandler.objectModelCursor = null
        }
    }

}
