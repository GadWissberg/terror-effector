package com.gadarts.te.renderer.model

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch
import com.badlogic.gdx.utils.Disposable
import com.gadarts.te.common.assets.GameAssetsManager
import com.gadarts.te.common.assets.atlas.Atlases
import com.gadarts.te.common.assets.definitions.character.player.PlayerDefinition
import com.gadarts.te.common.assets.definitions.env.EnvObjectDefinition
import com.gadarts.te.common.assets.texture.SurfaceTextures
import com.gadarts.te.common.definitions.character.CharacterType.BILLBOARD_Y
import com.gadarts.te.common.definitions.character.SpriteType
import com.gadarts.te.common.map.*
import com.gadarts.te.common.map.element.Direction
import com.gadarts.te.common.utils.CharacterUtils
import com.gadarts.te.common.utils.EnvObjectUtils
import com.gadarts.te.common.utils.GeneralUtils
import com.gadarts.te.renderer.handlers.utils.DecalUtils
import java.util.*

class MapData(val mapSize: Int, private val gameAssetsManager: GameAssetsManager) : Disposable {
    val placedEnvObjects = mutableListOf<PlacedEnvObject>()
    val placedCharacters = mutableListOf<PlacedCharacter>()

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
        GeneralUtils.disposeObject(this, MapData::class.java)
    }

    fun insertEnvObject(coords: Coords, height: Float, declaration: EnvObjectDefinition, direction: Direction) {
        if (placedEnvObjects.find { it.coords.equals(coords) && it.declaration == declaration } == null) {
            val modelInstance =
                EnvObjectUtils.createModelInstanceForEnvObject(
                    gameAssetsManager,
                    coords,
                    height,
                    declaration,
                    direction
                )
            val element = PlacedEnvObject(coords, declaration, direction, modelInstance)
            placedEnvObjects.add(element)
        }
    }

    fun insertCharacter(coords: Coords, height: Float, direction: Direction) {
        if (placedCharacters.find { it.coords.equals(coords) } == null) {
            val idle: String = SpriteType.IDLE.name + "_0_" + Direction.SOUTH.name.lowercase(Locale.getDefault())
            val atlas: TextureAtlas = gameAssetsManager.getAtlas(Atlases.PLAYER_MELEE)
            val region = atlas.findRegion(idle.lowercase(Locale.getDefault()))
            val decal = CharacterUtils.createCharacterDecal(region)
            decal.setPosition(coords.x.toFloat() + 0.5F, height + BILLBOARD_Y, coords.z.toFloat() + 0.5F)
            val element = PlacedCharacter(coords, PlayerDefinition.getInstance(), direction, decal)
            placedCharacters.add(element)
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

    fun onModelsRender(batch: ModelBatch, environment: Environment) {
        definedNodes.forEach {
            batch.render(it.modelInstance, environment)
            renderWall(batch, it.walls.eastWall, environment)
            renderWall(batch, it.walls.northWall, environment)
            renderWall(batch, it.walls.westWall, environment)
            renderWall(batch, it.walls.southWall, environment)
        }
        placedEnvObjects.forEach { batch.render(it.modelInstance, environment) }
    }

    private fun renderWall(batch: ModelBatch, wall: Wall?, environment: Environment) {
        if (wall != null) {
            batch.render(wall.modelInstance, environment)
        }
    }

    fun getNode(x: Int, z: Int): MapNodeData? {
        if (x < 0 || x >= mapSize || z < 0 || z >= mapSize) return null

        return matrix[z][x]
    }

    fun getNode(coords: Coords): MapNodeData? {
        return getNode(coords.x, coords.z)
    }

    fun deleteEnvObject(toDelete: PlacedEnvObject) {
        placedEnvObjects.remove(toDelete)
    }

    fun deleteCharacter(toDelete: PlacedCharacter) {
        placedCharacters.remove(toDelete)
    }

    fun onDecalsRender(decalsBatch: DecalBatch, camera: OrthographicCamera) {
        placedCharacters.forEach {
            DecalUtils.applyFrameSeenFromCameraForCharacterDecal(
                it.decal,
                camera,
                gameAssetsManager,
            )
            decalsBatch.add(it.decal)
        }
    }
}
