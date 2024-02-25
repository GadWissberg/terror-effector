package com.gadarts.te.renderer.handlers.cursor

import com.gadarts.te.Modes
import com.gadarts.te.common.map.Wall

interface CursorHandler {

    var selectedMode: Modes
    val selectedNodes: List<SelectedNode>
    val selectedWalls: List<Wall>
}
