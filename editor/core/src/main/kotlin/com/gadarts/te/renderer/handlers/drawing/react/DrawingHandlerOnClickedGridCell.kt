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
import com.gadarts.te.renderer.handlers.actions.types.PlaceCharacterAction
import com.gadarts.te.renderer.handlers.actions.types.PlaceEnvObjectAction
import com.gadarts.te.renderer.handlers.actions.types.PlaceFloorTilesAction
import com.gadarts.te.renderer.handlers.cursor.extra.ClickedGridCellEventForCharacterExtraInfo
import com.gadarts.te.renderer.handlers.cursor.extra.ClickedGridCellEventForEnvObjectExtraInfo
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
        val action: Action?
        when (handlersData.selectedMode) {
            Modes.FLOOR -> {
                val selectedTexture = drawingHandler.selectedTexture ?: return
                action = PlaceFloorTilesAction(
                    msg.extraInfo as List<Coords>,
                    handlersData.mapData,
                    gameAssetsManager.getTexture(selectedTexture),
                    selectedTexture,
                    wallCreator
                )

            }

            Modes.ENV_OBJECTS -> {
                val clickedGridCellEventForEnvObjectExtraInfo =
                    msg.extraInfo as ClickedGridCellEventForEnvObjectExtraInfo
                val mapNodeData = handlersData.mapData.getNode(
                    clickedGridCellEventForEnvObjectExtraInfo.coords.x,
                    clickedGridCellEventForEnvObjectExtraInfo.coords.z
                )
                action = PlaceEnvObjectAction(
                    clickedGridCellEventForEnvObjectExtraInfo.coords,
                    (mapNodeData?.height ?: 0F),
                    clickedGridCellEventForEnvObjectExtraInfo.definition,
                    clickedGridCellEventForEnvObjectExtraInfo.direction
                )
            }

            else -> {
                val clickedGridCellEventForCharacterExtraInfo =
                    msg.extraInfo as ClickedGridCellEventForCharacterExtraInfo
                action = PlaceCharacterAction(
                    clickedGridCellEventForCharacterExtraInfo.coords,
                    clickedGridCellEventForCharacterExtraInfo.direction
                )
            }
        }
        action.let { dispatcher.dispatchMessage(EditorEvents.ACTION_BEGIN.ordinal, action) }
    }


}
