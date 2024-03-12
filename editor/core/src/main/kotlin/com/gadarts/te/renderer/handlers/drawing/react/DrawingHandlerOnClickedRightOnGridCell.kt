package com.gadarts.te.renderer.handlers.drawing.react

import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.ai.msg.Telegram
import com.gadarts.te.EditorEvents
import com.gadarts.te.Modes
import com.gadarts.te.common.assets.GameAssetsManager
import com.gadarts.te.common.map.Coords
import com.gadarts.te.common.map.WallCreator
import com.gadarts.te.renderer.handlers.HandlerOnEvent
import com.gadarts.te.renderer.handlers.HandlersData
import com.gadarts.te.renderer.handlers.actions.types.DeleteEnvObjectAction
import com.gadarts.te.renderer.handlers.drawing.DrawingHandler

class DrawingHandlerOnClickedRightOnGridCell(private val drawingHandler: DrawingHandler) :
    HandlerOnEvent {
    override fun react(
        msg: Telegram,
        handlersData: HandlersData,
        gameAssetsManager: GameAssetsManager,
        dispatcher: MessageDispatcher,
        wallCreator: WallCreator
    ) {
        if (handlersData.selectedMode == Modes.ENV_OBJECTS) {
            val position = msg.extraInfo as Coords
            handlersData.mapData.getNode(position)?.let {
                handlersData.mapData.placedEnvObjects.find {
                    it.coords.equals(position)
                }?.let { placedEnvObject ->
                    val action = DeleteEnvObjectAction(placedEnvObject)
                    dispatcher.dispatchMessage(EditorEvents.ACTION_BEGIN.ordinal, action)
                }
            }
        }
    }

}
