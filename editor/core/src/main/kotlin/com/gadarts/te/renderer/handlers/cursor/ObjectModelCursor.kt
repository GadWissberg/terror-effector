package com.gadarts.te.renderer.handlers.cursor

import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.gadarts.te.common.WallObjects
import com.gadarts.te.common.map.element.Direction

class ObjectModelCursor(
    val modelInstance: ModelInstance,
    val definition: WallObjects?,
    private var direction: Direction
) {
    fun rotateClockwise() {
        rotate(90F, -1)
    }

    private fun rotate(degrees: Float, dir: Int) {
        if (definition == null) return

        modelInstance.transform.rotate(Vector3.Y, degrees)
        direction = Direction.findDirection(direction.getDirection(Vector2()).rotate90(dir))
    }

    fun getDirection(): Direction {
        return direction
    }

    fun rotateCounterClockwise() {
        rotate(-90F, 1)
    }
}
