package com.gadarts.te.renderer.handlers.cursor

import com.gadarts.te.common.map.Wall

interface CursorHandler {
    fun setCursorToFloorModel()

    val selectedNodes: List<SelectedNode>
    val selectedWalls: List<Wall>
    var objectModelCursor: ObjectModelCursor?

}
