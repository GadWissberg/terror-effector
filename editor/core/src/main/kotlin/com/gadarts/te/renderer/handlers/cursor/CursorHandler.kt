package com.gadarts.te.renderer.handlers.cursor

import com.badlogic.gdx.math.Vector3
import com.gadarts.te.common.definitions.EnvObjectDefinition
import com.gadarts.te.common.map.Wall

interface CursorHandler {
    fun setCursorToFloorModel()
    fun displayObjectOfTreeNode(envObjectDefinition: EnvObjectDefinition)
    fun fetchGridCellAtMouse(): Vector3

    val selectedNodes: List<SelectedNode>
    val selectedWalls: List<Wall>
    var objectModelCursor: ObjectModelCursor?

}
