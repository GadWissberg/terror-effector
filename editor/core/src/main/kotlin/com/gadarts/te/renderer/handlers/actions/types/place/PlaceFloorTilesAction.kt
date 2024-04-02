package com.gadarts.te.renderer.handlers.actions.types.place

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.gadarts.te.common.assets.texture.SurfaceTextures
import com.gadarts.te.common.map.Coords
import com.gadarts.te.common.map.MapNodeData
import com.gadarts.te.common.map.WallCreator
import com.gadarts.te.renderer.handlers.actions.types.Action
import com.gadarts.te.renderer.model.MapData

open class PlaceFloorTilesAction(
    private val nodes: List<Coords>,
    private val mapData: MapData,
    private val selectedTexture: Texture,
    private val textureDefinition: SurfaceTextures,
    private val wallCreator: WallCreator
) : Action {

    override fun begin(mapData: MapData) {
        nodes.forEach {
            applyAction(it.x, it.z)
        }
    }

    override fun takeStep(extraInfo: Any) {
        val position = extraInfo as Vector2
        val x = position.x.toInt()
        val z = position.y.toInt()
        applyAction(x, z)
    }

    protected fun applyAction(x: Int, z: Int) {
        val node = mapData.getNode(x, z)
        mapData.setNode(x, z, selectedTexture, textureDefinition)
        createWallsIfNeeded(node, x, z)
    }

    private fun createWallsIfNeeded(node: MapNodeData?, x: Int, z: Int) {
        if (node == null) {
            val newNode = mapData.getNode(x, z)
            if (mapData.getNode(x - 1, z) != null) {
                mapData.getNode(x - 1, z).let { wallCreator.adjustWestWall(it, newNode) }
            }
            if (mapData.getNode(x + 1, z) != null) {
                mapData.getNode(x + 1, z).let { wallCreator.adjustEastWall(newNode, it) }
            }
            if (mapData.getNode(x, z - 1) != null) {
                mapData.getNode(x, z - 1).let { wallCreator.adjustNorthWall(newNode, it) }
            }
            if (mapData.getNode(x, z + 1) != null) {
                mapData.getNode(x, z + 1).let { wallCreator.adjustSouthWall(it, newNode) }
            }
        }
    }

}
