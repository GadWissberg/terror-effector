package com.gadarts.te.renderer.handlers.cursor.mouseclick

import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.gadarts.te.common.map.Wall
import com.gadarts.te.renderer.handlers.cursor.CursorHandler
import com.gadarts.te.renderer.handlers.cursor.SelectedNode

interface OnMouseClickLogic {
    fun execute(
        selectedNodes: MutableList<SelectedNode>,
        dispatcher: MessageDispatcher,
        cursorHandler: CursorHandler,
        selectedWalls: MutableList<Wall>
    ): Boolean

}
