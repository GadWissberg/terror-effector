package com.gadarts.te.renderer.handlers.cursor

import com.gadarts.te.Modes

interface CursorHandler {

    var selectedMode: Modes
    val selectedNodes: List<SelectedNode>
}
