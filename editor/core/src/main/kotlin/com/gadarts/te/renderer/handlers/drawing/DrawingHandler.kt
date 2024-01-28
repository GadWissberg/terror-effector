package com.gadarts.te.renderer.handlers.drawing

import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.gadarts.te.EditorEvents
import com.gadarts.te.GeneralUtils
import com.gadarts.te.common.assets.texture.SurfaceTextures
import com.gadarts.te.renderer.handlers.BaseHandler
import com.gadarts.te.renderer.handlers.HandlerOnEvent

class DrawingHandler : BaseHandler() {
    var selectedTexture: SurfaceTextures? = null

    override fun getSubscribedEvents(): Map<EditorEvents, HandlerOnEvent> {
        return mapOf(
            EditorEvents.CLICKED_GRID_CELL to DrawingHandlerOnClickedGridCell(this),
            EditorEvents.TEXTURE_SELECTED to DrawingHandlerOnTextureSelected(this),
            EditorEvents.DRAGGED_GRID_CELL to DrawingHandlerOnDraggedGridCell(this)
        )
    }

    override fun onUpdate() {
    }

    override fun onRender(batch: ModelBatch) {
    }

    override fun dispose() {
        GeneralUtils.disposeObject(this, DrawingHandler::class)
    }

}
