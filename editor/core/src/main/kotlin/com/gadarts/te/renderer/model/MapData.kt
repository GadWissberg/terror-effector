package com.gadarts.te.renderer.model

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Disposable
import com.gadarts.te.GeneralUtils
import com.gadarts.te.common.WallObjects
import com.gadarts.te.common.assets.GameAssetsManager
import com.gadarts.te.common.assets.texture.SurfaceTextures
import com.gadarts.te.common.map.*

class MapData(val mapSize: Int, private val gameAssetsManager: GameAssetsManager) : Disposable {
    private val placedEnvObjects = mutableListOf<EnvObject>()

    var matrix = Array(mapSize) {
        arrayOfNulls<MapNodeData?>(mapSize)
    }

    val floorModel: Model = MapUtils.createFloorModel()

    val definedNodes = mutableListOf<MapNodeData>()

    init {
        floorModel.materials.get(0)
            .set(TextureAttribute.createDiffuse(gameAssetsManager.getTexture(SurfaceTextures.BLANK)))
    }

    override fun dispose() {
        GeneralUtils.disposeObject(this, MapData::class)
    }

    fun insertEnvObject(coords: Coords, definition: WallObjects) {
        val mapNodeData = matrix[coords.z][coords.x] ?: return

        if (mapNodeData.envObjects.find { it.coords.equals(coords) && it.definition == definition } == null) {
            val modelInstance = ModelInstance(gameAssetsManager.getModel(definition.modelDefinition))
            modelInstance.transform.setTranslation(
                Vector3(
                    mapNodeData.coords.x.toFloat(), mapNodeData.height,
                    mapNodeData.coords.z.toFloat()
                )
            )
            val element =
                EnvObject(coords, definition, modelInstance)
            mapNodeData.envObjects.add(element)
            placedEnvObjects.add(element)
        }
    }

    fun setNode(x: Int, z: Int, selectedTexture: Texture, textureDefinition: SurfaceTextures) {
        val mapNodeData: MapNodeData
        if (matrix[z][x] == null) {
            val modelInstance = ModelInstance(floorModel)
            mapNodeData = MapNodeData(x, z, MapNodesTypes.PASSABLE_NODE, modelInstance, textureDefinition)
            definedNodes.add(mapNodeData)
            matrix[z][x] = mapNodeData
        } else {
            mapNodeData = matrix[z][x]!!
        }
        (mapNodeData.modelInstance.materials.get(0)
            .get(TextureAttribute.Diffuse) as TextureAttribute).textureDescription.texture = selectedTexture
    }

    fun render(batch: ModelBatch) {
        definedNodes.forEach {
            batch.render(it.modelInstance)
            renderWall(batch, it.walls.eastWall)
            renderWall(batch, it.walls.northWall)
            renderWall(batch, it.walls.westWall)
            renderWall(batch, it.walls.southWall)
        }
        placedEnvObjects.forEach { batch.render(it.modelInstance) }
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
