package com.gadarts.te.renderer.handlers.cursor

import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.gadarts.te.Modes
import com.gadarts.te.common.map.Wall

interface CursorHandler {
    fun setCursorToFloorModel()

    var selectedMode: Modes
    val selectedNodes: List<SelectedNode>
    val selectedWalls: List<Wall>
    var objectModelCursor: ModelInstance?

}
