package com.gadarts.te.renderer.handlers.drawing.react

import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.ai.msg.Telegram
import com.gadarts.te.EditorEvents
import com.gadarts.te.common.assets.GameAssetsManager
import com.gadarts.te.common.map.WallCreator
import com.gadarts.te.renderer.handlers.HandlerOnEvent
import com.gadarts.te.renderer.handlers.HandlersData
import com.gadarts.te.renderer.handlers.drawing.DrawingHandler

class DrawingHandlerOnDraggedGridCell(drawingHandler: DrawingHandler) :
    HandlerOnEvent {
    override fun react(
        msg: Telegram,
        handlersData: HandlersData,
        gameAssetsManager: GameAssetsManager,
        dispatcher: MessageDispatcher,
        wallCreator: WallCreator
    ) {
        dispatcher.dispatchMessage(EditorEvents.ACTION_TAKE_STEP.ordinal, msg.extraInfo)
    }

}
