package com.gadarts.te.renderer.handlers.actions.types

import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.gadarts.te.EditorEvents
import com.gadarts.te.common.map.MapNodeData
import com.gadarts.te.common.map.WallCreator
import com.gadarts.te.renderer.model.MapData

class ChangeFloorHeightAction(
    private val nodes: List<MapNodeData>,
    private val mapData: MapData,
    private val valueToAdd: Float,
    private val wallCreator: WallCreator,
    private val dispatcher: MessageDispatcher
) : SingleStepAction {

    override fun begin(mapData: MapData) {
        nodes.forEach {
            it.applyHeight(it.height + valueToAdd)
            adjustEastSideWall(it)
            adjustNorthSideWall(it)
            adjustWestSideWall(it)
            adjustSouthSideWall(it)
        }
        dispatcher.dispatchMessage(EditorEvents.NODES_HEIGHT_SET.ordinal, nodes)
    }

    private fun adjustSouthSideWall(it: MapNodeData) {
        val southNode = mapData.getNode(it.coords.x, it.coords.z + 1)
        if (southNode != null) {
            if (it.height > southNode.height) {
                wallCreator.adjustNorthWall(southNode, it)
            } else if (it.height < southNode.height) {
                wallCreator.adjustSouthWall(southNode, it)
            }
        }
    }

    private fun adjustWestSideWall(it: MapNodeData) {
        val westNode = mapData.getNode(it.coords.x - 1, it.coords.z)
        if (westNode != null) {
            if (it.height > westNode.height) {
                wallCreator.adjustEastWall(westNode, it)
            } else if (it.height < westNode.height) {
                wallCreator.adjustWestWall(westNode, it)
            }
        }
    }

    private fun adjustNorthSideWall(it: MapNodeData) {
        val northNode = mapData.getNode(it.coords.x, it.coords.z - 1)
        if (northNode != null) {
            if (it.height > northNode.height) {
                wallCreator.adjustSouthWall(it, northNode)
            } else if (it.height < northNode.height) {
                wallCreator.adjustNorthWall(it, northNode)
            }
        }
    }

    private fun adjustEastSideWall(it: MapNodeData) {
        val eastNode = mapData.getNode(it.coords.x + 1, it.coords.z)
        if (eastNode != null) {
            if (it.height > eastNode.height) {
                wallCreator.adjustWestWall(it, eastNode)
            } else if (it.height < eastNode.height) {
                wallCreator.adjustEastWall(it, eastNode)
            }
        }
    }

}
