package com.gadarts.te.renderer.handlers.cursor

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.gadarts.te.common.map.MapNodeData

object CursorUtils {
    fun stickPositionToGrid(position: Vector3, mapMatrix: Array<Array<MapNodeData?>>) {
        position.x = position.x.toInt().toFloat()
        position.z = position.z.toInt().toFloat()
        val floatMapSize = mapMatrix.size.toFloat()
        position.x = MathUtils.clamp(position.x, 0F, floatMapSize - 1)
        position.z = MathUtils.clamp(position.z, 0F, floatMapSize - 1)
        position.x += 0.5F
        position.z += 0.5F
        position.y = mapMatrix[position.z.toInt()][position.x.toInt()]?.height ?: 0F
    }

}
