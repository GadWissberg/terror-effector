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
import com.gadarts.te.renderer.handlers.actions.types.Action
import com.gadarts.te.renderer.handlers.actions.types.PlaceEnvObjectAction
import com.gadarts.te.renderer.handlers.actions.types.PlaceFloorTilesAction
import com.gadarts.te.renderer.handlers.cursor.ClickedGridCellEventForEnvObject
import com.gadarts.te.renderer.handlers.drawing.DrawingHandler

@Suppress("UNCHECKED_CAST")
class DrawingHandlerOnClickedGridCell(private val drawingHandler: DrawingHandler) : HandlerOnEvent {
    override fun react(
        msg: Telegram,
        handlersData: HandlersData,
        gameAssetsManager: GameAssetsManager,
        dispatcher: MessageDispatcher,
        wallCreator: WallCreator,
    ) {
        var action: Action? = null
        if (handlersData.selectedMode == Modes.FLOOR) {
            val selectedTexture = drawingHandler.selectedTexture ?: return

            action = PlaceFloorTilesAction(
                msg.extraInfo as List<Coords>,
                handlersData.mapData,
                gameAssetsManager.getTexture(selectedTexture),
                selectedTexture,
                wallCreator
            )

        } else {
            val clickedGridCellEventForEnvObject = msg.extraInfo as ClickedGridCellEventForEnvObject
            val mapNodeData = handlersData.mapData.getNode(
                clickedGridCellEventForEnvObject.coords.x,
                clickedGridCellEventForEnvObject.coords.z
            )
            if (mapNodeData != null) {
                action = PlaceEnvObjectAction(
                    mapNodeData,
                    handlersData.mapData,
                    clickedGridCellEventForEnvObject.definition,
                )
            }
        }
        action?.let { dispatcher.dispatchMessage(EditorEvents.ACTION_BEGIN.ordinal, action) }
    }


}
