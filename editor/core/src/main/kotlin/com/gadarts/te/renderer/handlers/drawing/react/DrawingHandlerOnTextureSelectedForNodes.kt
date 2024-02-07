package com.gadarts.te.renderer.handlers.drawing.react

import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.ai.msg.Telegram
import com.gadarts.te.EditorEvents
import com.gadarts.te.common.assets.GameAssetsManager
import com.gadarts.te.common.map.Coords
import com.gadarts.te.common.map.WallCreator
import com.gadarts.te.renderer.handlers.HandlerOnEvent
import com.gadarts.te.renderer.handlers.HandlersData
import com.gadarts.te.renderer.handlers.actions.types.SinglePlaceFloorTilesAction
import com.gadarts.te.renderer.handlers.drawing.DrawingHandler

class DrawingHandlerOnTextureSelectedForNodes(private val drawingHandler: DrawingHandler) :
    HandlerOnEvent {
    override fun react(
        msg: Telegram,
        handlersData: HandlersData,
        gameAssetsManager: GameAssetsManager,
        dispatcher: MessageDispatcher,
        wallCreator: WallCreator
    ) {
        @Suppress("UNCHECKED_CAST") val selected = msg.extraInfo as List<Coords>
        val action = SinglePlaceFloorTilesAction(
            selected,
            handlersData.mapData,
            gameAssetsManager.getTexture(drawingHandler.selectedTexture),
            drawingHandler.selectedTexture!!,
            wallCreator
        )
        dispatcher.dispatchMessage(EditorEvents.ACTION_BEGIN.ordinal, action)
    }

}
