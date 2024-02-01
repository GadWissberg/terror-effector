package com.gadarts.te.renderer.handlers.drawing.react

import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.math.Vector2
import com.gadarts.te.EditorEvents
import com.gadarts.te.common.map.WallCreator
import com.gadarts.te.renderer.handlers.HandlerOnEvent
import com.gadarts.te.renderer.handlers.HandlersData
import com.gadarts.te.renderer.handlers.actions.types.ChangeFloorHeightAction

abstract class DrawingHandlerOnScrolledEvent : HandlerOnEvent {
    protected fun invokeHeightChange(
        msg: Telegram,
        handlersData: HandlersData,
        wallCreator: WallCreator,
        dispatcher: MessageDispatcher,
        value: Int
    ) {
        val action = ChangeFloorHeightAction(
            msg.extraInfo as Vector2,
            handlersData.mapData,
            value,
            wallCreator
        )
        dispatcher.dispatchMessage(EditorEvents.ACTION_BEGIN.ordinal, action)
    }


}
