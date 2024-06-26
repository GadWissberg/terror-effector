package com.gadarts.te.renderer.handlers

import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.gadarts.te.EditorEvents
import com.gadarts.te.common.assets.GameAssetsManager
import com.gadarts.te.common.assets.definitions.Definitions
import com.gadarts.te.common.assets.definitions.DefinitionsUtils
import com.gadarts.te.common.assets.definitions.character.CharacterDefinition
import com.gadarts.te.common.assets.definitions.character.enemy.EnemiesDefinitions
import com.gadarts.te.common.assets.definitions.character.player.PlayerDefinition
import com.gadarts.te.common.assets.definitions.env.EnvObjectDefinition
import com.gadarts.te.common.assets.definitions.env.EnvObjectsDefinitions
import com.gadarts.te.common.assets.texture.SurfaceTextures
import com.gadarts.te.common.map.*
import com.gadarts.te.common.map.MapJsonKeys.*
import com.gadarts.te.common.map.element.Direction
import com.gadarts.te.renderer.model.PlacedElement
import com.google.gson.*
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.util.*
import java.util.stream.IntStream

class PersistenceHandler : BaseHandler() {
    private val gson = Gson()

    override fun getSubscribedEvents(): Map<EditorEvents, HandlerOnEvent> {
        return mapOf(EditorEvents.CLICKED_BUTTON_SAVE to object : HandlerOnEvent {
            override fun react(
                msg: Telegram,
                handlersData: HandlersData,
                gameAssetsManager: GameAssetsManager,
                dispatcher: MessageDispatcher,
                wallCreator: WallCreator
            ) {
                save()
            }
        }, EditorEvents.CLICKED_BUTTON_LOAD to object : HandlerOnEvent {
            override fun react(
                msg: Telegram,
                handlersData: HandlersData,
                gameAssetsManager: GameAssetsManager,
                dispatcher: MessageDispatcher,
                wallCreator: WallCreator
            ) {
                load()
            }
        })
    }

    private fun inflateNodes(
        nodesJsonObject: JsonObject,
        initializedNodes: MutableList<MapNodeData>,
    ): Array<Array<MapNodeData?>> {
        val width = handlersData.mapData.mapSize
        val depth = handlersData.mapData.mapSize
        val matrix = nodesJsonObject[MATRIX].asString
        val inputMap = Array(depth) {
            arrayOfNulls<MapNodeData>(
                width
            )
        }
        initializedNodes.clear()
        val matrixByte = Base64.getDecoder().decode(matrix.toByteArray())
        for (z in 0 until depth) {
            for (x in 0 until width) {
                inflateNode(
                    width,
                    matrixByte,
                    Coords(x, z),
                    inputMap
                )
            }
        }
        return inputMap
    }

    private fun inflateNode(
        mapWidth: Int,
        input: ByteArray,
        flatNode: Coords,
        newMatrix: Array<Array<MapNodeData?>>
    ) {
        val tileId = input[flatNode.z * mapWidth + flatNode.x % mapWidth]
        if (tileId.toInt() != 0) {
            val textureDefinition: SurfaceTextures = SurfaceTextures.entries[tileId - 1]
            val modelInstance = ModelInstance(handlersData.mapData.floorModel)
            (modelInstance.materials.get(0)
                .get(TextureAttribute.Diffuse) as TextureAttribute).textureDescription.texture =
                gameAssetsManager.getTexture(textureDefinition)
            val node = MapNodeData(
                flatNode.x,
                flatNode.z,
                MapNodesTypes.PASSABLE_NODE,
                modelInstance,
                textureDefinition
            )
            handlersData.mapData.definedNodes.add(node)
            newMatrix[flatNode.z][flatNode.x] = node
        }
    }

    private fun inflateNodeHeight(nodeJsonObject: JsonObject) {
        val mapNodeData: MapNodeData =
            handlersData.mapData.matrix[nodeJsonObject[COORD_Z].asInt][nodeJsonObject[COORD_X].asInt]!!
        if (nodeJsonObject.has(HEIGHT)) {
            mapNodeData.applyHeight(nodeJsonObject[HEIGHT].asFloat)
        }
    }

    private fun inflateMapStructure(input: JsonObject) {
        val nodesJsonObject = input.getAsJsonObject(NODES)
        val nodesDataJson = nodesJsonObject.getAsJsonArray(NODES_DATA)
        handlersData.mapData.matrix = inflateNodes(nodesJsonObject, handlersData.mapData.definedNodes)
        nodesDataJson?.forEach { inflateNodeHeight(it.asJsonObject) }
        nodesDataJson?.forEach { jsonElement ->
            jsonElement as JsonObject
            inflateWalls(
                handlersData.mapData.matrix[jsonElement[COORD_Z].asInt][jsonElement[COORD_X].asInt]!!,
                jsonElement,
                wallCreator
            )
        }
        fillMissingTextures()
    }

    private fun fillMissingTextures() {
        val nodes: Array<Array<MapNodeData?>> = handlersData.mapData.matrix
        for (mapNodeData in nodes) {
            for (node in mapNodeData) {
                node?.let {
                    fillSouthWallMissingTexture(nodes, node)
                    fillNorthWallMissingTexture(nodes, node)
                    fillEastWallMissingTexture(nodes, node)
                    fillWestWallMissingTexture(nodes, node)
                }
            }
        }
    }

    private fun fillSouthWallMissingTexture(
        nodes: Array<Array<MapNodeData?>>,
        node: MapNodeData
    ) {
        if (node.coords.z == nodes.size - 1 || node.walls.southWall != null) return
        nodes[node.coords.z + 1][node.coords.x]?.let {
            if (node.height < it.height) {
                inflateSouthWall(node, wallCreator.southWallModel, SurfaceTextures.MISSING, it, wallCreator)
            }
        }
    }

    private fun inflateSouthWall(
        mapNodeData: MapNodeData,
        wallModel: Model,
        texture: SurfaceTextures,
        southNode: MapNodeData,
        wallCreator: WallCreator
    ) {
        val southWall: Wall = wallCreator.createWall(mapNodeData, wallModel, gameAssetsManager, texture)
        mapNodeData.walls.southWall = southWall
        wallCreator.adjustSouthWall(southNode, mapNodeData)
    }

    private fun fillNorthWallMissingTexture(
        nodes: Array<Array<MapNodeData?>>,
        node: MapNodeData
    ) {
        if (node.coords.z == 0 || node.walls.northWall != null) return
        nodes[node.coords.z - 1][node.coords.x]?.let {
            if (node.height < it.height) {
                inflateNorthWall(node, wallCreator.northWallModel, SurfaceTextures.MISSING, it, wallCreator)
            }
        }
    }

    private fun inflateNorthWall(
        mapNodeData: MapNodeData,
        wallModel: Model,
        texture: SurfaceTextures,
        northNode: MapNodeData,
        wallCreator: WallCreator
    ) {
        val northWall: Wall = wallCreator.createWall(mapNodeData, wallModel, gameAssetsManager, texture)
        mapNodeData.walls.northWall = northWall
        wallCreator.adjustNorthWall(mapNodeData, northNode)
    }

    private fun fillWestWallMissingTexture(
        nodes: Array<Array<MapNodeData?>>,
        node: MapNodeData
    ) {
        if (node.coords.x == 0 || node.walls.westWall != null) return
        nodes[node.coords.z][node.coords.x - 1]?.let {
            if (node.height < it.height) {
                inflateWestWall(node, wallCreator.westWallModel, SurfaceTextures.MISSING, it, wallCreator)
            }
        }
    }

    private fun inflateWestWall(
        mapNodeData: MapNodeData,
        wallModel: Model,
        texture: SurfaceTextures,
        westNode: MapNodeData,
        wallCreator: WallCreator
    ) {
        val westWall: Wall = wallCreator.createWall(mapNodeData, wallModel, gameAssetsManager, texture)
        mapNodeData.walls.westWall = westWall
        wallCreator.adjustWestWall(westNode, mapNodeData)
    }

    private fun fillEastWallMissingTexture(
        nodes: Array<Array<MapNodeData?>>,
        node: MapNodeData
    ) {
        if (node.coords.x == nodes[0].size - 1 || node.walls.eastWall != null) return
        nodes[node.coords.z][node.coords.x + 1]?.let {
            if (node.height < it.height) {
                inflateEastWall(node, wallCreator.eastWallModel, SurfaceTextures.MISSING, it, wallCreator)
            }
        }
    }

    private fun inflateEastWall(
        mapNodeData: MapNodeData,
        wallModel: Model,
        texture: SurfaceTextures,
        eastNode: MapNodeData,
        wallCreator: WallCreator
    ) {
        val eastWall: Wall = wallCreator.createWall(mapNodeData, wallModel, gameAssetsManager, texture)
        mapNodeData.walls.eastWall = eastWall
        wallCreator.adjustEastWall(mapNodeData, eastNode)
    }

    private fun extractTextureName(wallJsonObj: JsonObject): SurfaceTextures {
        return SurfaceTextures.valueOf(wallJsonObj[TEXTURE].asString)
    }

    private fun inflateWalls(
        mapNodeData: MapNodeData,
        jsonObject: JsonObject,
        wallCreator: WallCreator
    ) {
        val wallsJsonObject = Optional.ofNullable(jsonObject[WALLS])
            .orElseGet { JsonObject() }
            .asJsonObject
        val eastWallModel = wallCreator.eastWallModel
        val westWallModel = wallCreator.westWallModel
        val northWallModel = wallCreator.northWallModel
        val southWallModel = wallCreator.southWallModel
        inflateNorthWall(mapNodeData, wallsJsonObject, wallCreator, northWallModel)
        inflateSouthWall(mapNodeData, wallsJsonObject, southWallModel, wallCreator)
        inflateWestWall(mapNodeData, wallsJsonObject, westWallModel, wallCreator)
        inflateEastWall(mapNodeData, wallsJsonObject, eastWallModel, wallCreator)
    }

    private fun inflateEastWall(
        mapNodeData: MapNodeData,
        wallsJsonObject: JsonObject,
        eastWallModel: Model,
        wallCreator: WallCreator
    ) {
        val matrix = handlersData.mapData.matrix
        val coords = mapNodeData.coords
        if (coords.x < matrix[0].size - 1 && mapNodeData.height < (matrix[coords.z][coords.x + 1]?.height ?: 0F)) {
            Optional.ofNullable<JsonElement>(wallsJsonObject[EAST]).ifPresent { east: JsonElement ->
                val texture: SurfaceTextures = extractTextureName(east.asJsonObject)
                inflateEastWall(
                    mapNodeData,
                    eastWallModel,
                    texture,
                    matrix[coords.z][coords.x + 1]!!,
                    wallCreator
                )
            }
        }
    }

    private fun inflateWestWall(
        mapNodeData: MapNodeData,
        wallsJsonObject: JsonObject,
        westWallModel: Model,
        wallCreator: WallCreator
    ) {
        val coords = mapNodeData.coords
        if (coords.x > 0 && mapNodeData.height < (handlersData.mapData.matrix[coords.z][coords.x - 1]?.height ?: 0F)) {
            Optional.ofNullable<JsonElement>(wallsJsonObject[WEST]).ifPresent { west: JsonElement ->
                val texture: SurfaceTextures = extractTextureName(west.asJsonObject)
                inflateWestWall(
                    mapNodeData,
                    westWallModel,
                    texture,
                    handlersData.mapData.matrix[coords.z][coords.x - 1]!!,
                    wallCreator
                )
            }
        }
    }

    private fun inflateSouthWall(
        mapNodeData: MapNodeData,
        wallsJsonObject: JsonObject,
        southWallModel: Model,
        wallCreator: WallCreator
    ) {
        val coords = mapNodeData.coords
        val matrix = handlersData.mapData.matrix
        if (coords.z < matrix.size - 1 && mapNodeData.height < (matrix[coords.z + 1][coords.x]?.height ?: 0F)) {
            Optional.ofNullable<JsonElement>(wallsJsonObject[SOUTH]).ifPresent { north: JsonElement ->
                val texture: SurfaceTextures = extractTextureName(north.asJsonObject)
                inflateSouthWall(
                    mapNodeData,
                    southWallModel,
                    texture,
                    matrix[coords.z + 1][coords.x]!!,
                    wallCreator
                )
            }
        }
    }

    private fun inflateNorthWall(
        mapNodeData: MapNodeData,
        wallsJsonObject: JsonObject,
        wallCreator: WallCreator,
        northWallModel: Model
    ) {
        val coords = mapNodeData.coords
        if (coords.z > 0 && mapNodeData.height < (handlersData.mapData.matrix[coords.z - 1][coords.x]?.height ?: 0F)) {
            wallsJsonObject[NORTH]?.let {
                val texture: SurfaceTextures = extractTextureName(it.asJsonObject)
                val northWall: Wall = wallCreator.createWall(mapNodeData, northWallModel, gameAssetsManager, texture)
                mapNodeData.walls.northWall = northWall
                wallCreator.adjustNorthWall(
                    mapNodeData,
                    handlersData.mapData.matrix[coords.z - 1][coords.x]
                )
                inflateNorthWall(
                    mapNodeData,
                    northWallModel,
                    texture,
                    handlersData.mapData.matrix[coords.z - 1][coords.x]!!,
                    wallCreator
                )
            }
        }
    }

    private fun load() {
        val envObjectDefs =
            (gameAssetsManager.getDefinition(Definitions.ENV_OBJECTS) as EnvObjectsDefinitions).definitions
        try {
            FileReader(TEMP_PATH).use { reader ->
                val input: JsonObject = gson.fromJson(reader, JsonObject::class.java)
                inflateMapStructure(input)
                inflateEnvObjects(input, envObjectDefs)
                inflateCharacters(input)
            }
        } catch (e: JsonSyntaxException) {
            throw IOException(e.message)
        }
    }

    private fun inflateEnvObjects(
        input: JsonObject,
        envObjectDefs: List<EnvObjectDefinition>?
    ) {
        inflateMapElements(input, ENV_OBJECTS)
        { x: Int, z: Int, height: Float, definition: String, direction: Direction ->
            handlersData.mapData.insertEnvObject(
                Coords(x, z),
                height,
                DefinitionsUtils.parse(definition, envObjectDefs),
                direction
            )
        }
    }

    private fun inflateCharacters(input: JsonObject) {
        inflateMapElements(input, CHARACTERS)
        { x: Int, z: Int, height: Float, definitionId: String, direction: Direction ->
            val selectedCharacter = parseCharacter(definitionId)
            if (selectedCharacter != null) {
                handlersData.mapData.insertCharacter(
                    Coords(x, z),
                    height,
                    direction,
                    selectedCharacter
                )
            }
        }
    }

    private fun parseCharacter(definitionId: String): CharacterDefinition? =
        if (PlayerDefinition.getInstance().id().equals(definitionId)) {
            PlayerDefinition.getInstance()
        } else {
            val enemyDefinitions =
                (gameAssetsManager.getDefinition(Definitions.ENEMIES) as EnemiesDefinitions).definitions
            DefinitionsUtils.parse(definitionId, enemyDefinitions)
        }

    private fun inflateMapElements(
        input: JsonObject,
        jsonKey: String,
        inflationMethod: (x: Int, z: Int, height: Float, definition: String, direction: Direction) -> Unit
    ) {
        if (!input.has(ELEMENTS) || !input.getAsJsonObject(ELEMENTS).has(jsonKey)) return

        input.getAsJsonObject(ELEMENTS).getAsJsonArray(jsonKey).forEach {
            val elementJsonObject = it.asJsonObject
            val x = elementJsonObject.get(COORD_X).asInt
            val z = elementJsonObject.get(COORD_Z).asInt
            val y = handlersData.mapData.matrix[z][x]?.height ?: 0F
            val definition = elementJsonObject.get(DEFINITION).asString
            val direction = Direction.valueOf(elementJsonObject.get(DIRECTION).asString)
            inflationMethod(x, z, y, definition, direction)
        }
    }

    private fun save() {
        val output = JsonObject()
        val nodesJson = JsonObject()
        nodesJson.addProperty(WIDTH, handlersData.mapData.mapSize)
        nodesJson.addProperty(DEPTH, handlersData.mapData.mapSize)
        val matrix = ByteArray(handlersData.mapData.mapSize * handlersData.mapData.mapSize)
        val nodesDataJson = JsonArray()
        val nodes: Array<Array<MapNodeData?>> = handlersData.mapData.matrix
        IntStream.range(0, handlersData.mapData.mapSize).forEach { row: Int ->
            IntStream.range(0, handlersData.mapData.mapSize).forEach { col: Int ->
                insertIntoMatrix(
                    row,
                    col,
                    nodes,
                    handlersData.mapData.mapSize,
                    nodesDataJson,
                    matrix
                )
            }
        }
        nodesJson.addProperty(MATRIX, String(Base64.getEncoder().encode(matrix)))
        if (nodesDataJson.size() > 0) {
            nodesJson.add(NODES_DATA, nodesDataJson)
        }
        output.add(NODES, nodesJson)
        output.add(ELEMENTS, deflateMapElements())
        try {
            FileWriter(TEMP_PATH).use { writer ->
                gson.toJson(output, writer)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun deflateMapElements(): JsonObject {
        val mapElements = JsonObject()
        deflateMapElementsByType(mapElements, handlersData.mapData.placedEnvObjects, ENV_OBJECTS)
        deflateMapElementsByType(mapElements, handlersData.mapData.placedCharacters, CHARACTERS)
        return mapElements
    }

    private fun deflateMapElementsByType(
        mapElements: JsonObject, placedElements: MutableList<out PlacedElement>, jsonKey: String
    ) {
        val elementsJsonArray = JsonArray()
        placedElements.forEach {
            val elementJsonObject = JsonObject()
            elementJsonObject.addProperty(DEFINITION, it.elementDefinition.id())
            elementJsonObject.addProperty(DIRECTION, it.direction.name)
            elementJsonObject.addProperty(COORD_X, it.coords.x)
            elementJsonObject.addProperty(COORD_Z, it.coords.z)
            elementsJsonArray.add(elementJsonObject)
        }
        mapElements.add(jsonKey, elementsJsonArray)
    }

    private fun insertIntoMatrix(
        row: Int,
        col: Int,
        nodes: Array<Array<MapNodeData?>>,
        numberOfCols: Int,
        nodesDataJson: JsonArray,
        matrix: ByteArray
    ) {
        val mapNodeData: MapNodeData? = nodes[row][col]
        val index = row * numberOfCols + col
        if (mapNodeData?.textureDefinition != null) {
            addNodeData(mapNodeData, nodesDataJson)
            matrix[index] = ((mapNodeData.textureDefinition.ordinal + 1).toByte())
        } else {
            matrix[index] = 0.toByte()
        }
    }

    private fun addNodeData(node: MapNodeData, nodesDataJson: JsonArray) {
        if (node.height > 0 || !node.walls.isEmpty) {
            val nodeDataJson = JsonObject()
            nodeDataJson.addProperty(COORD_Z, node.coords.z)
            nodeDataJson.addProperty(COORD_X, node.coords.x)
            if (node.height > 0) {
                nodeDataJson.addProperty(HEIGHT, node.height)
            }
            deflateWalls(node, nodeDataJson)
            nodesDataJson.add(nodeDataJson)
        }
    }


    private fun addWallDefinition(json: JsonObject, w: Wall, side: String) {
        if (w.definition == null) return
        val textureName: String = w.definition.getName()
        val jsonObject = JsonObject()
        jsonObject.addProperty(TEXTURE, textureName)
        jsonObject.addProperty(V_SCALE, w.vScale)
        jsonObject.addProperty(H_OFFSET, w.hOffset)
        jsonObject.addProperty(V_OFFSET, w.vOffset)
        json.add(side, jsonObject)
    }

    private fun deflateWalls(node: MapNodeData, output: JsonObject) {
        val walls: NodeWalls = node.walls
        if (!walls.isEmpty) {
            val wallsJson = JsonObject()
            walls.eastWall?.let { addWallDefinition(wallsJson, it, EAST) }
            walls.westWall?.let { addWallDefinition(wallsJson, it, WEST) }
            walls.northWall?.let { addWallDefinition(wallsJson, it, NORTH) }
            walls.southWall?.let { addWallDefinition(wallsJson, it, SOUTH) }
            output.add(WALLS, wallsJson)
        }
    }

    override fun onUpdate() {

    }


    override fun dispose() {
    }

    companion object {
        const val TEMP_PATH = "..\\game\\assets\\maps\\test_map.json"
    }

}
