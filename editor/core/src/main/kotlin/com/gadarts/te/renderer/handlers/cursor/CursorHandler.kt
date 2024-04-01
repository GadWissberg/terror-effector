package com.gadarts.te.renderer.handlers.cursor

import com.badlogic.gdx.graphics.g3d.decals.Decal
import com.badlogic.gdx.math.Vector3
import com.gadarts.te.common.definitions.env.EnvObjectDefinition
import com.gadarts.te.common.map.Wall

interface CursorHandler {
    fun setCursorToFloorModel()
    fun displayObjectOfTreeNode(envObjectDefinition: EnvObjectDefinition)
    fun fetchGridCellAtMouse(): Vector3
    fun clearSelection()
    fun displayPlayerCursor()

    val selectedNodes: List<SelectedNode>
    val selectedWalls: List<Wall>
    var objectModelCursor: ObjectModelCursor?
    var highlightWall: Wall?
    var decalCursor: Decal?
}
