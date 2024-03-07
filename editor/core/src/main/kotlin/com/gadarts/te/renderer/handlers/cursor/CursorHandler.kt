package com.gadarts.te.renderer.handlers.cursor

import com.gadarts.te.common.WallObjects
import com.gadarts.te.common.map.Wall

interface CursorHandler {
    fun setCursorToFloorModel()
    fun displayObjectOfTreeNode(wallObject: WallObjects)

    val selectedNodes: List<SelectedNode>
    val selectedWalls: List<Wall>
    var objectModelCursor: ObjectModelCursor?

}
