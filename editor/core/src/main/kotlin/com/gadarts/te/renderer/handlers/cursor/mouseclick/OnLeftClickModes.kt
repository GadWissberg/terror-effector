package com.gadarts.te.renderer.handlers.cursor.mouseclick

import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.gadarts.te.EditorEvents
import com.gadarts.te.Modes
import com.gadarts.te.common.map.Coords
import com.gadarts.te.common.map.Wall
import com.gadarts.te.common.map.element.Direction
import com.gadarts.te.renderer.handlers.cursor.CursorHandler
import com.gadarts.te.renderer.handlers.cursor.SelectedNode
import com.gadarts.te.renderer.handlers.cursor.extra.ClickedGridCellEventForCharacterExtraInfo
import com.gadarts.te.renderer.handlers.cursor.extra.ClickedGridCellEventForEnvObjectExtraInfo

val onLeftClickMapping = mapOf(
    Modes.FLOOR to OnLeftClickFloorMode,
    Modes.WALLS to OnLeftClickWalls,
    Modes.ENV_OBJECTS to OnLeftClickEnvObject,
    Modes.CHARACTERS to OnLeftClickCharacters
)
private val auxVector = Vector3()
private val auxMatrix = Matrix4()


private object OnLeftClickFloorMode : OnMouseClickLogic {

    override fun execute(
        selectedNodes: MutableList<SelectedNode>,
        dispatcher: MessageDispatcher,
        cursorHandler: CursorHandler,
        selectedWalls: MutableList<Wall>
    ): Boolean {
        if (selectedNodes.isEmpty()) {
            val position = cursorHandler.objectModelCursor!!.modelInstance.transform.getTranslation(auxVector)
            dispatcher.dispatchMessage(
                EditorEvents.CLICKED_LEFT_ON_GRID_CELL.ordinal,
                listOf(Coords(position.x.toInt(), position.z.toInt()))
            )
        } else {
            selectedNodes.clear()
        }
        return true
    }

}

private object OnLeftClickWalls : OnMouseClickLogic {

    override fun execute(
        selectedNodes: MutableList<SelectedNode>,
        dispatcher: MessageDispatcher,
        cursorHandler: CursorHandler,
        selectedWalls: MutableList<Wall>
    ): Boolean {
        if (cursorHandler.highlightWall != null) {
            if (selectedWalls.contains(cursorHandler.highlightWall)) {
                selectedWalls.remove(cursorHandler.highlightWall)
            } else {
                selectedWalls.add(cursorHandler.highlightWall!!)
            }
        }
        return true
    }

}

private object OnLeftClickEnvObject : OnMouseClickLogic {

    override fun execute(
        selectedNodes: MutableList<SelectedNode>,
        dispatcher: MessageDispatcher,
        cursorHandler: CursorHandler,
        selectedWalls: MutableList<Wall>
    ): Boolean {
        val objectModelCursor = cursorHandler.objectModelCursor
        auxMatrix.set(objectModelCursor!!.modelInstance.transform)
            .translate(
                -objectModelCursor.definition!!.modelDefinition.modelOffset.x,
                -objectModelCursor.definition.modelDefinition.modelOffset.y,
                -objectModelCursor.definition.modelDefinition.modelOffset.z
            )
        val position = auxMatrix.getTranslation(auxVector)
        dispatcher.dispatchMessage(
            EditorEvents.CLICKED_LEFT_ON_GRID_CELL.ordinal,
            ClickedGridCellEventForEnvObjectExtraInfo(
                Coords(position.x.toInt(), position.z.toInt()),
                objectModelCursor.definition,
                objectModelCursor.getDirection()
            )
        )
        return true
    }

}

private object OnLeftClickCharacters : OnMouseClickLogic {

    override fun execute(
        selectedNodes: MutableList<SelectedNode>,
        dispatcher: MessageDispatcher,
        cursorHandler: CursorHandler,
        selectedWalls: MutableList<Wall>
    ): Boolean {
        if (cursorHandler.decalCursor.decal == null) return false

        val position = cursorHandler.decalCursor.decal!!.position
        dispatcher.dispatchMessage(
            EditorEvents.CLICKED_LEFT_ON_GRID_CELL.ordinal,
            ClickedGridCellEventForCharacterExtraInfo(
                Coords(position.x.toInt(), position.z.toInt()),
                Direction.SOUTH
            )
        )
        return true
    }

}
