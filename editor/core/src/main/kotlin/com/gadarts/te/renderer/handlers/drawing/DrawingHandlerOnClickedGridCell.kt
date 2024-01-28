package com.gadarts.te.renderer.handlers.drawing

import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.math.Vector2
import com.gadarts.te.EditorEvents
import com.gadarts.te.common.assets.GameAssetsManager
import com.gadarts.te.renderer.handlers.HandlerOnEvent
import com.gadarts.te.renderer.handlers.HandlersData
import com.gadarts.te.renderer.handlers.actions.PlaceFloorTilesAction

class DrawingHandlerOnClickedGridCell(private val drawingHandler: DrawingHandler) : HandlerOnEvent {
    override fun react(
        msg: Telegram,
        handlersData: HandlersData,
        gameAssetsManager: GameAssetsManager,
        dispatcher: MessageDispatcher,
    ) {
        val selectedTexture = drawingHandler.selectedTexture ?: return

        val action = PlaceFloorTilesAction(
            msg.extraInfo as Vector2,
            handlersData.mapData,
            gameAssetsManager.getTexture(selectedTexture),
            selectedTexture
        )

        dispatcher.dispatchMessage(EditorEvents.ACTION_BEGIN.ordinal, action)
    }


}
