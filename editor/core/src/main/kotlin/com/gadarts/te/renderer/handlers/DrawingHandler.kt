package com.gadarts.te.renderer.handlers

import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.math.Vector2
import com.gadarts.te.EditorEvents
import com.gadarts.te.GeneralUtils
import com.gadarts.te.common.assets.GameAssetsManager
import com.gadarts.te.common.assets.texture.SurfaceTextures
import com.gadarts.te.renderer.handlers.actions.PlaceFloorTilesAction
import com.gadarts.te.renderer.model.MapData

class DrawingHandler(
    dispatcher: MessageDispatcher,
    private val gameAssetsManager: GameAssetsManager,
    private val mapData: MapData
) :
    BaseHandler(dispatcher) {
    private var selectedTexture: SurfaceTextures? = null

    init {
        dispatcher.addListener(this, EditorEvents.CLICKED_GRID_CELL.ordinal)
        dispatcher.addListener(this, EditorEvents.TEXTURE_SELECTED.ordinal)
    }

    override fun handleMessage(msg: Telegram): Boolean {
        var handled = false

        if (msg.message == EditorEvents.CLICKED_GRID_CELL.ordinal && selectedTexture != null) {
            val action = PlaceFloorTilesAction(
                msg.extraInfo as Vector2,
                mapData,
                gameAssetsManager.getTexture(selectedTexture!!),
                selectedTexture!!
            )
            dispatcher.dispatchMessage(EditorEvents.ACTION_BEGIN.ordinal, action)
            handled = true
        } else if (msg.message == EditorEvents.TEXTURE_SELECTED.ordinal) {
            selectedTexture = msg.extraInfo as SurfaceTextures
            handled = true
        }

        return handled
    }

    override fun dispose() {
        GeneralUtils.disposeObject(this, DrawingHandler::class)
    }


}
