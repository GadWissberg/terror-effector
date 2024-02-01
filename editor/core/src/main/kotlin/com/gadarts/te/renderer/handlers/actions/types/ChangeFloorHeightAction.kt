package com.gadarts.te.renderer.handlers.actions.types

import com.badlogic.gdx.math.Vector2
import com.gadarts.te.common.map.WallCreator
import com.gadarts.te.renderer.model.MapData

class ChangeFloorHeightAction(
    private val position: Vector2,
    private val mapData: MapData,
    private val valueToAdd: Int,
    private val wallCreator: WallCreator
) : SingleStepAction {
    override fun begin() {
        val node = mapData.getTile(position.x.toInt(), position.y.toInt())
        node?.applyHeight(node.height + valueToAdd)
        val eastNode = mapData.getTile(position.x.toInt() + 1, position.y.toInt())
        if (eastNode != null) {
            wallCreator.adjustWestWall(node, eastNode)
        }
        val northNode = mapData.getTile(position.x.toInt(), position.y.toInt() - 1)
        if (northNode != null) {
            wallCreator.adjustSouthWall(node, northNode)
        }
        val westNode = mapData.getTile(position.x.toInt() - 1, position.y.toInt())
        if (westNode != null) {
            wallCreator.adjustEastWall(westNode, node)
        }
        val southNode = mapData.getTile(position.x.toInt(), position.y.toInt() + 1)
        if (southNode != null) {
            wallCreator.adjustNorthWall(southNode, node)
        }
    }

}
