package com.gadarts.te.renderer.model

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.utils.Disposable
import com.gadarts.te.GeneralUtils
import com.gadarts.te.common.assets.texture.SurfaceTextures
import com.gadarts.te.common.map.MapNodeData
import com.gadarts.te.common.map.MapNodesTypes
import com.gadarts.te.common.map.MapUtils
import com.gadarts.te.common.map.Wall

class MapData(val mapSize: Int, blankTexture: Texture) : Disposable {

    private val floorModel = MapUtils.createFloorModel()

    init {
        floorModel.materials.get(0).set(TextureAttribute.createDiffuse(blankTexture))
    }

    private val matrix = Array(mapSize) {
        arrayOfNulls<MapNodeData?>(mapSize)
    }
    private val definedTiles = mutableListOf<MapNodeData>()
    override fun dispose() {
        GeneralUtils.disposeObject(this, MapData::class)
    }

    fun setTile(x: Int, z: Int, selectedTexture: Texture, textureDefinition: SurfaceTextures) {
        val mapNodeData: MapNodeData
        if (matrix[z][x] == null) {
            val modelInstance = ModelInstance(floorModel)
            mapNodeData = MapNodeData(x, z, MapNodesTypes.PASSABLE_NODE, modelInstance, textureDefinition)
            definedTiles.add(mapNodeData)
            matrix[z][x] = mapNodeData
        } else {
            mapNodeData = matrix[z][x]!!
        }
        (mapNodeData.modelInstance.materials.get(0)
            .get(TextureAttribute.Diffuse) as TextureAttribute).textureDescription.texture = selectedTexture
    }

    fun render(batch: ModelBatch) {
        definedTiles.forEach {
            batch.render(it.modelInstance)
            renderWall(batch, it.walls.eastWall)
            renderWall(batch, it.walls.northWall)
            renderWall(batch, it.walls.westWall)
            renderWall(batch, it.walls.southWall)
        }
    }

    private fun renderWall(batch: ModelBatch, wall: Wall?) {
        if (wall != null) {
            batch.render(wall.modelInstance)
        }
    }

    fun getNode(x: Int, z: Int): MapNodeData? {
        if (x < 0 || x >= mapSize || z < 0 || z >= mapSize) return null

        return matrix[z][x]
    }
}
