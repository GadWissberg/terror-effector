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
        val modelInstance = ModelInstance(floorModel)
        (modelInstance.materials.get(0).get(TextureAttribute.Diffuse) as TextureAttribute).textureDescription.texture =
            selectedTexture
        val mapNodeData = MapNodeData(x, z, MapNodesTypes.PASSABLE_NODE, modelInstance, textureDefinition)
        matrix[z][x] = mapNodeData
        definedTiles.add(mapNodeData)
    }

    fun render(batch: ModelBatch) {
        definedTiles.forEach { batch.render(it.modelInstance) }
    }
}
