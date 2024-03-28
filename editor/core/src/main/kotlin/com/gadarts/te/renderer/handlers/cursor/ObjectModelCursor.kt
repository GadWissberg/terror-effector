package com.gadarts.te.renderer.handlers.cursor

import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.gadarts.te.common.definitions.env.EnvObjectDefinition
import com.gadarts.te.common.map.element.Direction
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
        val modelDefinition = definition?.modelDefinition
        val xOffset = (modelDefinition?.modelOffset?.x ?: 0F)
        val yOffset = (modelDefinition?.modelOffset?.y ?: 0F)
        val zOffset = (modelDefinition?.modelOffset?.z ?: 0F)
        modelInstance.transform.translate(xOffset, yOffset, zOffset)
    }

    fun updateObjectModelCursorPosition(position: Vector3) {
        position.x = position.x.toInt().toFloat()
        position.z = position.z.toInt().toFloat()
        val mapSize = this.mapData.mapSize.toFloat()
        position.x = MathUtils.clamp(position.x, 0F, mapSize - 1)
        position.z = MathUtils.clamp(position.z, 0F, mapSize - 1)
        position.x += 0.5F
        position.z += 0.5F
        position.y = this.mapData.matrix[position.z.toInt()][position.x.toInt()]?.height ?: 0F
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
