package com.gadarts.te.renderer.handlers

import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.math.Vector2
import com.gadarts.te.EditorEvents
import com.gadarts.te.GeneralUtils
import com.gadarts.te.common.assets.texture.SurfaceTextures
import com.gadarts.te.renderer.handlers.actions.PlaceFloorTilesAction

class DrawingHandler : BaseHandler() {
    private var selectedTexture: SurfaceTextures? = null

    override fun getSubscribedEvents(): List<EditorEvents> {
        return listOf(EditorEvents.CLICKED_GRID_CELL, EditorEvents.TEXTURE_SELECTED)
    }

    override fun onUpdate() {
    }

    override fun onRender(batch: ModelBatch) {
    }

    override fun handleMessage(msg: Telegram): Boolean {
        var handled = false

        if (msg.message == EditorEvents.CLICKED_GRID_CELL.ordinal && selectedTexture != null) {
            val action = PlaceFloorTilesAction(
                msg.extraInfo as Vector2,
                handlersData.mapData,
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
