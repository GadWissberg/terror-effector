package com.gadarts.te.renderer.handlers.actions.types

import com.gadarts.te.common.map.MapNodeData
import com.gadarts.te.common.map.WallCreator
import com.gadarts.te.renderer.model.MapData

class ChangeFloorHeightAction(
    private val nodes: List<MapNodeData>,
    private val mapData: MapData,
    private val valueToAdd: Float,
    private val wallCreator: WallCreator
) : SingleStepAction {
    override fun begin() {
        nodes.forEach {
            it.applyHeight(it.height + valueToAdd)
            val eastNode = mapData.getNode(it.coords.x + 1, it.coords.z)
            if (eastNode != null) {
                wallCreator.adjustWestWall(it, eastNode)
            }
            val northNode = mapData.getNode(it.coords.x, it.coords.z - 1)
            if (northNode != null) {
                wallCreator.adjustSouthWall(it, northNode)
            }
            val westNode = mapData.getNode(it.coords.x - 1, it.coords.z)
            if (westNode != null) {
                wallCreator.adjustEastWall(westNode, it)
            }
            val southNode = mapData.getNode(it.coords.x, it.coords.z + 1)
            if (southNode != null) {
                wallCreator.adjustNorthWall(southNode, it)
            }
        }
    }

}
