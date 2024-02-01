package com.gadarts.te.renderer.handlers.drawing.react

import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.math.Vector2
import com.gadarts.te.EditorEvents
import com.gadarts.te.common.assets.GameAssetsManager
import com.gadarts.te.common.map.WallCreator
import com.gadarts.te.renderer.handlers.HandlerOnEvent
import com.gadarts.te.renderer.handlers.HandlersData
import com.gadarts.te.renderer.handlers.actions.types.PlaceFloorTilesAction
import com.gadarts.te.renderer.handlers.drawing.DrawingHandler

class DrawingHandlerOnClickedGridCell(private val drawingHandler: DrawingHandler) : HandlerOnEvent {
    override fun react(
        msg: Telegram,
        handlersData: HandlersData,
        gameAssetsManager: GameAssetsManager,
        dispatcher: MessageDispatcher,
        wallCreator: WallCreator,
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
