package com.gadarts.te.renderer.handlers.cursor.types

import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.gadarts.te.common.assets.definitions.env.EnvObjectDefinition
import com.gadarts.te.common.map.element.Direction
import com.gadarts.te.renderer.handlers.cursor.CursorHandler
import com.gadarts.te.renderer.handlers.cursor.CursorUtils
import com.gadarts.te.renderer.model.MapData

class ObjectModelCursor(
    val modelInstance: ModelInstance,
    val definition: EnvObjectDefinition?,
    private var direction: Direction,
    private val cursorHandler: CursorHandler,
    private val mapData: MapData
) {
    fun rotateClockwise() {
        rotate(-1)
    }

    fun updateObjectModelCursorPositionWithOffsets(position: Vector3) {
        updateObjectModelCursorPosition(position)
        applyOffsets()
    }

    private fun applyOffsets() {
        val modelDefinition = definition?.model
        val xOffset = (modelDefinition?.modelOffset?.x ?: 0F)
        val yOffset = (modelDefinition?.modelOffset?.y ?: 0F)
        val zOffset = (modelDefinition?.modelOffset?.z ?: 0F)
        modelInstance.transform.translate(xOffset, yOffset, zOffset)
    }

    fun updateObjectModelCursorPosition(position: Vector3) {
        CursorUtils.stickPositionToGrid(position, mapData.matrix)
        modelInstance.transform.setTranslation(position)
    }


    private fun rotate(dir: Int) {
        if (definition == null) return

        updateObjectModelCursorPosition(cursorHandler.fetchGridCellAtMouse())
        modelInstance.transform.rotate(Vector3.Y, dir * 90F)
        applyOffsets()
        direction = Direction.findDirection(direction.getDirection(Vector2()).rotate90(dir))
    }

    fun getDirection(): Direction {
        return direction
    }

    fun rotateCounterClockwise() {
        rotate(1)
    }
}
