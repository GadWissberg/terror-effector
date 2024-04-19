package com.gadarts.te.renderer.handlers.cursor.mouseclick

import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.math.Vector3
import com.gadarts.te.EditorEvents
import com.gadarts.te.Modes
import com.gadarts.te.common.map.Coords
import com.gadarts.te.common.map.Wall
import com.gadarts.te.renderer.handlers.cursor.CursorHandler
import com.gadarts.te.renderer.handlers.cursor.SelectedNode

val onRightClickMapping = mapOf(
    Modes.FLOOR to OnRightClickFloorMode,
    Modes.WALLS to OnRightClickWalls,
    Modes.ENV_OBJECTS to OnRightClickEnvObject,
    Modes.CHARACTERS to OnRightClickCharacters
)
private val auxVector = Vector3()


private object OnRightClickFloorMode : OnMouseClickLogic {

    override fun execute(
        selectedNodes: MutableList<SelectedNode>,
        dispatcher: MessageDispatcher,
        cursorHandler: CursorHandler,
        selectedWalls: MutableList<Wall>
    ): Boolean {
        cursorHandler.turnOnSelectingCursor()
        return true
    }

}

private object OnRightClickWalls : OnMouseClickLogic {

    override fun execute(
        selectedNodes: MutableList<SelectedNode>,
        dispatcher: MessageDispatcher,
        cursorHandler: CursorHandler,
        selectedWalls: MutableList<Wall>
    ): Boolean {
        cursorHandler.clearSelection()
        return true
    }

}

private object OnRightClickEnvObject : OnMouseClickLogic {

    override fun execute(
        selectedNodes: MutableList<SelectedNode>,
        dispatcher: MessageDispatcher,
        cursorHandler: CursorHandler,
        selectedWalls: MutableList<Wall>
    ): Boolean {
        var handled = false
        if (cursorHandler.objectModelCursor != null) {
            val position = cursorHandler.objectModelCursor!!.modelInstance.transform.getTranslation(
                auxVector
            )
            dispatcher.dispatchMessage(
                EditorEvents.CLICKED_RIGHT_ON_GRID_CELL.ordinal,
                Coords(position.x.toInt(), position.z.toInt())
            )
            handled = true
        }
        return handled
    }

}

private object OnRightClickCharacters : OnMouseClickLogic {

    override fun execute(
        selectedNodes: MutableList<SelectedNode>,
        dispatcher: MessageDispatcher,
        cursorHandler: CursorHandler,
        selectedWalls: MutableList<Wall>
    ): Boolean {
        var handled = false
        if (cursorHandler.decalCursor != null) {
            dispatcher.dispatchMessage(
                EditorEvents.CLICKED_RIGHT_ON_GRID_CELL.ordinal,
                Coords(cursorHandler.decalCursor!!.position.x.toInt(), cursorHandler.decalCursor!!.position.z.toInt())
            )
            handled = true
        }
        return handled
    }

}
