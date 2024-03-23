package com.gadarts.te.renderer.handlers.drawing

import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.gadarts.te.EditorEvents
import com.gadarts.te.GeneralUtils
import com.gadarts.te.common.assets.texture.SurfaceTextures
import com.gadarts.te.renderer.handlers.BaseHandler
import com.gadarts.te.renderer.handlers.HandlerOnEvent
import com.gadarts.te.renderer.handlers.drawing.react.*

class DrawingHandlerImpl : BaseHandler(), DrawingHandler {
    override var selectedTexture: SurfaceTextures? = null

    override fun getSubscribedEvents(): Map<EditorEvents, HandlerOnEvent> {
        return mapOf(
            EditorEvents.CLICKED_LEFT_ON_GRID_CELL to DrawingHandlerOnClickedGridCell(this),
            EditorEvents.CLICKED_RIGHT_ON_GRID_CELL to DrawingHandlerOnClickedRightOnGridCell(),
            EditorEvents.TEXTURE_SELECTED_VIA_GALLERY to DrawingHandlerOnTextureSelected(this),
            EditorEvents.TEXTURE_SELECTED_FOR_NODES to DrawingHandlerOnTextureSelectedForNodes(this),
            EditorEvents.TEXTURE_SELECTED_FOR_WALLS to DrawingHandlerOnTextureSelectedForWalls(this),
            EditorEvents.DRAGGED_GRID_CELL to DrawingHandlerOnDraggedGridCell(this),
            EditorEvents.SCROLLED_UP to DrawingHandlerOnScrolledUp(),
            EditorEvents.SCROLLED_DOWN to DrawingHandlerOnScrolledDown(),
            EditorEvents.NODES_HEIGHT_SET to DrawingHandlerOnNodesHeightSet(),
        )
    }

    override fun onUpdate() {
    }

    override fun onRender(batch: ModelBatch) {
    }

    override fun dispose() {
        GeneralUtils.disposeObject(this, DrawingHandlerImpl::class)
    }

}
