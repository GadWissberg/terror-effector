package com.gadarts.te.renderer.handlers.drawing

import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.ai.msg.Telegram
import com.gadarts.te.EditorEvents
import com.gadarts.te.common.assets.GameAssetsManager
import com.gadarts.te.renderer.handlers.HandlerOnEvent
import com.gadarts.te.renderer.handlers.HandlersData

class DrawingHandlerOnDraggedGridCell(drawingHandler: DrawingHandler) :
    HandlerOnEvent {
    override fun react(
        msg: Telegram,
        handlersData: HandlersData,
        gameAssetsManager: GameAssetsManager,
        dispatcher: MessageDispatcher
    ) {
        dispatcher.dispatchMessage(EditorEvents.ACTION_TAKE_STEP.ordinal, msg.extraInfo)
    }

}
