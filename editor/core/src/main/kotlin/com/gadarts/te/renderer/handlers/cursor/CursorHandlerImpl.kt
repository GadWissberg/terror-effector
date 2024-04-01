package com.gadarts.te.renderer.handlers.cursor

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.decals.Decal
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch
import com.badlogic.gdx.math.*
import com.badlogic.gdx.math.Matrix4.M13
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.math.collision.Ray
import com.badlogic.gdx.utils.Disposable
import com.gadarts.te.DebugSettings
import com.gadarts.te.EditorEvents
import com.gadarts.te.Modes
import com.gadarts.te.TerrorEffectorEditor
import com.gadarts.te.common.assets.GameAssetsManager
import com.gadarts.te.common.assets.atlas.Atlases
import com.gadarts.te.common.definitions.character.CharacterType.BILLBOARD_Y
import com.gadarts.te.common.definitions.character.SpriteType
import com.gadarts.te.common.definitions.env.EnvObjectDefinition
import com.gadarts.te.common.map.Coords
import com.gadarts.te.common.map.MapNodeData
import com.gadarts.te.common.map.MapUtils
import com.gadarts.te.common.map.Wall
import com.gadarts.te.common.map.element.Direction
import com.gadarts.te.common.utils.CameraUtils
import com.gadarts.te.common.utils.CharacterUtils
import com.gadarts.te.common.utils.GeneralUtils
import com.gadarts.te.common.utils.ModelInstanceFactory
import com.gadarts.te.renderer.handlers.BaseHandler
import com.gadarts.te.renderer.handlers.HandlerOnEvent
import com.gadarts.te.renderer.handlers.HandlersData
import com.gadarts.te.renderer.handlers.cursor.react.*
import com.gadarts.te.renderer.handlers.utils.DecalUtils
import java.util.*
import kotlin.math.abs
import kotlin.math.max


class CursorHandlerImpl : Disposable, InputProcessor, BaseHandler(), CursorHandler {
    override var decalCursor: Decal? = null
    override val selectedWalls = mutableListOf<Wall>()
    override val selectedNodes = mutableListOf<SelectedNode>()
    override var objectModelCursor: ObjectModelCursor? = null
    private val originalFloorModelInstanceCursorPosition = Vector3()
    private val prevFloorCursorPosition = Vector3()
    private val floorModel: Model = MapUtils.createFloorModel()
    override var highlightWall: Wall? = null
    private var selecting: Boolean = false
    private var viewportScreenY: Float = 0.0f
    private var viewportScreenX: Float = 0.0f
    private var viewportHeight: Float = 0.0f
    private var viewportWidth: Float = 0.0f
    private var cursorFading: Float = 0.0f
    private var cursorMaterialBlendingAttribute: BlendingAttribute = BlendingAttribute()

    init {
        cursorMaterialBlendingAttribute.opacity = 1f
    }

    private fun addAttributesToCursorModel() {
        val cursorMaterial = objectModelCursor!!.modelInstance.materials.get(0)
        cursorMaterial.set(cursorMaterialBlendingAttribute)
        cursorMaterial.set(ColorAttribute.createDiffuse(Color.GREEN))
    }

    override fun setCursorToFloorModel() {
        objectModelCursor = ObjectModelCursor(
            ModelInstance(floorModel),
            null,
            Direction.EAST,
            this,
            handlersData.mapData
        )
        objectModelCursor!!.modelInstance.nodes.get(0).isAnimated = true
        addAttributesToCursorModel()
    }

    override fun displayObjectOfTreeNode(envObjectDefinition: EnvObjectDefinition) {
        objectModelCursor =
            ObjectModelCursor(
                ModelInstanceFactory.create(gameAssetsManager, envObjectDefinition.modelDefinition),
                envObjectDefinition,
                Direction.EAST,
                this,
                handlersData.mapData
            )
        addAttributesToCursorModel()
    }

    override fun fetchGridCellAtMouse(): Vector3 {
        return fetchGridCellAtMouse(Gdx.input.getX(0), Gdx.input.getY(0))
    }

    override fun clearSelection() {
        selectedNodes.clear()
        selectedWalls.clear()
    }

    override fun displayPlayerCursor() {
        objectModelCursor = null
        val idle: String = SpriteType.IDLE.name + "_0_" + Direction.SOUTH.name.lowercase(Locale.getDefault())
        val atlas: TextureAtlas = gameAssetsManager.getAtlas(Atlases.PLAYER_MELEE)
        val region = atlas.findRegion(idle.lowercase(Locale.getDefault()))
        decalCursor = CharacterUtils.createCharacterDecal(region)
    }

    override fun dispose() {
        GeneralUtils.disposeObject(this, CursorHandlerImpl::class.java)
    }

    private fun turnOnSelectingCursor() {
        selecting = true
        objectModelCursor!!.modelInstance.nodes.get(0).localTransform.trn(0.5F, 0.01F, 0.5F)
        objectModelCursor!!.modelInstance.calculateTransforms()
        objectModelCursor!!.modelInstance.transform.trn(-0.5F, 0F, -0.5F)
        objectModelCursor!!.modelInstance.transform.getTranslation(originalFloorModelInstanceCursorPosition)
    }

    private fun turnOffSelectingCursor(screenX: Int, screenY: Int) {
        selecting = false
        objectModelCursor!!.modelInstance.transform.values[Matrix4.M00] = 1F
        objectModelCursor!!.modelInstance.transform.values[Matrix4.M22] = 1F
        objectModelCursor!!.modelInstance.nodes.get(0).localTransform.trn(-0.5F, -0.01F, -0.5F)
        updateCursorPosition(screenX, screenY)
        objectModelCursor!!.modelInstance.calculateTransforms()
    }

    private fun updateCursorPosition(screenX: Int, screenY: Int) {
        val position = fetchGridCellAtMouse(screenX, screenY)
        when (handlersData.selectedMode) {
            Modes.FLOOR -> {
                objectModelCursor?.updateObjectModelCursorPosition(position)
            }

            Modes.CHARACTERS -> {
                CursorUtils.stickPositionToGrid(position, handlersData.mapData.matrix)
                decalCursor?.setPosition(
                    position.x,
                    (handlersData.mapData.matrix[position.z.toInt()][position.x.toInt()]?.height ?: 0F) + BILLBOARD_Y,
                    position.z
                )
            }

            else -> {
                objectModelCursor?.updateObjectModelCursorPositionWithOffsets(position)
            }
        }
    }

    override fun onInitialize(
        dispatcher: MessageDispatcher,
        gameAssetsManager: GameAssetsManager,
        handlersData: HandlersData,
    ) {
        super.onInitialize(dispatcher, gameAssetsManager, handlersData)
        addToInputMultiplexer(this)
        setViewportSize(
            handlersData.screenX,
            handlersData.screenY,
            TerrorEffectorEditor.WINDOW_WIDTH - handlersData.screenX,
            handlersData.heightUnderBars
        )
        setCursorToFloorModel()
    }

    override fun keyDown(keycode: Int): Boolean {
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        return false
    }

    override fun keyTyped(character: Char): Boolean {
        return false
    }

    override fun getSubscribedEvents(): Map<EditorEvents, HandlerOnEvent> {
        return mapOf(
            EditorEvents.TEXTURE_SET to CursorHandlerOnTextureSet(this),
            EditorEvents.NODES_HEIGHT_SET to CursorHandlerOnNodesHeightSet(this),
            EditorEvents.MODE_CHANGED to CursorHandlerOnModeSelected(this),
            EditorEvents.CLICKED_TREE_NODE to CursorHandlerOnClickedTreeNode(this),
            EditorEvents.CLICKED_BUTTON_ROTATE_CLOCKWISE to CursorHandlerOnClickedButtonRotateClockwise(this),
            EditorEvents.CLICKED_BUTTON_ROTATE_COUNTER_CLOCKWISE to CursorHandlerOnClickedButtonRotateCounterClockwise(
                this
            )
        )
    }

    override fun onDecalsRender(decalsBatch: DecalBatch) {
        if (decalCursor != null) {
            DecalUtils.applyFrameSeenFromCameraForCharacterDecal(decalCursor!!, handlersData.camera, gameAssetsManager)
            decalsBatch.add(decalCursor)
        }
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (DebugSettings.FREELOOK) return false
        if (button == Input.Buttons.LEFT) {
            return onLeftClickMapping[handlersData.selectedMode]?.execute(
                selectedNodes,
                dispatcher,
                this,
                selectedWalls
            ) ?: false
        } else if (button == Input.Buttons.RIGHT) {
            if (!selecting && handlersData.selectedMode == Modes.FLOOR) {
                turnOnSelectingCursor()
                return true
            } else if (handlersData.selectedMode == Modes.ENV_OBJECTS && objectModelCursor != null) {
                val position = objectModelCursor!!.modelInstance.transform.getTranslation(auxVector3_2)
                dispatcher.dispatchMessage(
                    EditorEvents.CLICKED_RIGHT_ON_GRID_CELL.ordinal,
                    Coords(position.x.toInt(), position.z.toInt())
                )
                return true
            }
        }
        return false
    }



    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (button == Input.Buttons.RIGHT && selecting) {
            selectNodes(screenX, screenY)
            return true
        }
        return false
    }

    private fun selectNodes(screenX: Int, screenY: Int) {
        val northWestPosition = objectModelCursor!!.modelInstance.transform.getTranslation(auxVector3_2)
        val selectionBoxSize = objectModelCursor!!.modelInstance.transform.getScale(auxVector3_1)
        selectedNodes.clear()
        val northWestX = northWestPosition.x.toInt()
        val northWestZ = northWestPosition.z.toInt()
        for (x in northWestX until northWestX + selectionBoxSize.x.toInt()) {
            for (z in northWestZ until northWestZ + selectionBoxSize.z.toInt()) {
                if (x >= 0 && x < handlersData.mapData.mapSize && z >= 0 && z < handlersData.mapData.mapSize) {
                    selectedNodes.add(
                        SelectedNode(
                            Coords(x, z),
                            ModelInstance(floorModel),
                            handlersData.mapData.getNode(x, z)?.height ?: 0F
                        )
                    )
                }
            }
        }
        turnOffSelectingCursor(screenX, screenY)
    }

    override fun touchCancelled(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        if (!selecting) {
            updatePrevCursorPosition()
            updateCursorPosition(screenX, screenY)
            val current = objectModelCursor!!.modelInstance.transform.getTranslation(auxVector3_2).sub(0.5F, 0F, 0.5F)
            if (!prevFloorCursorPosition.epsilonEquals(current.x, 0F, current.z, 0.01F)) {
                dispatcher.dispatchMessage(
                    EditorEvents.DRAGGED_GRID_CELL.ordinal,
                    auxVector2_1.set(current.x, current.z)
                )
            }
        } else {
            updateSelectionBox(screenX, screenY)
        }
        return true
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        if (DebugSettings.FREELOOK) return false

        if (handlersData.selectedMode == Modes.FLOOR
            || handlersData.selectedMode == Modes.ENV_OBJECTS
            || handlersData.selectedMode == Modes.CHARACTERS
        ) {
            if (!selecting) {
                updatePrevCursorPosition()
                updateCursorPosition(screenX, screenY)
            } else {
                updateSelectionBox(screenX, screenY)
            }
        } else {
            highlightWallFromMouse(screenX, screenY)
        }
        return true
    }

    private fun highlightWallFromMouse(screenX: Int, screenY: Int) {
        val coords = CameraUtils.findAllCoordsOnRay(
            screenX,
            screenY,
            viewportScreenX,
            viewportScreenY,
            viewportWidth,
            viewportHeight,
            handlersData.camera
        )
        val unproject = handlersData.camera.unproject(
            auxVector3_2.set(screenX.toFloat(), screenY.toFloat(), 0F),
            viewportScreenX, viewportScreenY,
            viewportWidth, viewportHeight
        )
        resetWallHighlight()
        highlightWall = null
        while (!coords.isEmpty()) {
            val coord = coords.pop()
            val node = handlersData.mapData.getNode(coord.x, coord.z)
            if (node != null) {
                if (tryHighlightWallsOfNode(unproject, node)) break
                if (tryHighlightWallsOfNode(unproject, handlersData.mapData.getNode(coord.x - 1, coord.z))) break
                if (tryHighlightWallsOfNode(unproject, handlersData.mapData.getNode(coord.x + 1, coord.z))) break
                if (tryHighlightWallsOfNode(unproject, handlersData.mapData.getNode(coord.x, coord.z - 1))) break
                if (tryHighlightWallsOfNode(unproject, handlersData.mapData.getNode(coord.x, coord.z + 1))) break
            }
        }
    }

    private fun resetWallHighlight() {
        if (highlightWall != null) {
            (highlightWall!!.modelInstance.materials.get(0).get(ColorAttribute.Diffuse) as ColorAttribute).color.set(
                Color.WHITE
            )
        }
    }

    private fun tryHighlightWallsOfNode(unproject: Vector3, node: MapNodeData?): Boolean {
        if (node == null) return false

        if (tryHighlightWallOfNode(unproject, node.walls.southWall)) return true
        if (tryHighlightWallOfNode(unproject, node.walls.northWall)) return true
        if (tryHighlightWallOfNode(unproject, node.walls.westWall)) return true
        if (tryHighlightWallOfNode(unproject, node.walls.eastWall)) return true

        return false
    }

    private fun tryHighlightWallOfNode(unproject: Vector3, wall: Wall?): Boolean {
        if (wall != null) {
            val intersectRayBoundsFast = Intersector.intersectRayBoundsFast(
                auxRay.set(unproject, handlersData.camera.direction),
                wall.modelInstance.calculateBoundingBox(auxBoundingBox).mul(wall.modelInstance.transform)
            )
            if (intersectRayBoundsFast) {
                highlightWall = wall
                return true
            }
        }
        return false
    }

    private fun updateSelectionBox(screenX: Int, screenY: Int) {
        val mousePosition = fetchGridCellAtMouse(screenX, screenY)
        mousePosition.x = MathUtils.clamp(mousePosition.x, 0F, handlersData.mapData.mapSize - 1F)
        mousePosition.z = MathUtils.clamp(mousePosition.z, 0F, handlersData.mapData.mapSize - 1F)
        val mousePositionX = mousePosition.x.toInt()
        objectModelCursor!!.modelInstance.transform.values[Matrix4.M00] =
            abs(mousePositionX - (originalFloorModelInstanceCursorPosition.x)) + 1F
        val mousePositionZ = mousePosition.z.toInt()
        objectModelCursor!!.modelInstance.transform.values[Matrix4.M22] =
            abs(mousePositionZ - (originalFloorModelInstanceCursorPosition.z)) + 1F
        if (mousePositionX < (originalFloorModelInstanceCursorPosition.x)) {
            objectModelCursor!!.modelInstance.transform.values[Matrix4.M03] = mousePositionX.toFloat()
        }
        if (mousePositionZ < (originalFloorModelInstanceCursorPosition.z)) {
            objectModelCursor!!.modelInstance.transform.values[Matrix4.M23] = mousePositionZ.toFloat()
        }
        objectModelCursor!!.modelInstance.transform.values[M13] = mousePosition.y + 0.01F
    }

    private fun updatePrevCursorPosition() {
        if (objectModelCursor == null) return

        val prev = objectModelCursor!!.modelInstance.transform.getTranslation(auxVector3_2)
        prevFloorCursorPosition.set(prev.x.toInt().toFloat(), 0F, prev.z.toInt().toFloat())
    }


    private fun fetchGridCellAtMouse(screenX: Int, screenY: Int): Vector3 {
        val unproject = handlersData.camera.unproject(
            auxVector3_2.set(screenX.toFloat(), screenY.toFloat(), 0F),
            viewportScreenX, viewportScreenY,
            viewportWidth, viewportHeight
        )
        Intersector.intersectRayPlane(
            auxRay.set(unproject, handlersData.camera.direction),
            groundPlane,
            auxVector3_2
        )
        return auxVector3_2.set(
            MathUtils.clamp(auxVector3_2.x, 0F, handlersData.mapData.mapSize.toFloat()),
            auxVector3_2.y,
            MathUtils.clamp(auxVector3_2.z, 0F, handlersData.mapData.mapSize.toFloat())
        )
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        val position = objectModelCursor!!.modelInstance.transform.getTranslation(auxVector3_2)
        auxList.clear()
        if (selectedNodes.isEmpty()) {
            auxList.add(Coords(position.x.toInt(), position.z.toInt()))
        } else {
            selectedNodes.forEach { auxList.add(it.coords) }
        }
        dispatcher.dispatchMessage(
            (if (amountY > 0) EditorEvents.SCROLLED_DOWN else EditorEvents.SCROLLED_UP).ordinal,
            auxList
        )
        objectModelCursor!!.modelInstance.transform.values[M13] =
            MathUtils.clamp(
                ((handlersData.mapData.getNode(position.x.toInt(), position.z.toInt())?.height)
                    ?: 0F) + (objectModelCursor!!.definition?.modelDefinition?.modelOffset?.y ?: 0F),
                objectModelCursor!!.definition?.modelDefinition?.modelOffset?.y ?: 0F,
                MapNodeData.MAX_FLOOR_HEIGHT
            )
        return true
    }

    override fun onUpdate() {
        cursorMaterialBlendingAttribute.opacity = max(MathUtils.sin(cursorFading / 10F), 0.1F)
        cursorFading += 1
    }

    override fun onModelsRender(batch: ModelBatch) {
        renderModels(batch)
    }


    private fun renderModels(batch: ModelBatch) {
        if (objectModelCursor != null
            && (handlersData.selectedMode == Modes.FLOOR || handlersData.selectedMode == Modes.ENV_OBJECTS)
        ) {
            batch.render(objectModelCursor!!.modelInstance)
        } else if (handlersData.selectedMode == Modes.WALLS) {
            if (highlightWall != null) {
                (highlightWall!!.modelInstance.materials.get(0)
                    .get(ColorAttribute.Diffuse) as ColorAttribute).color.set(
                    Color.GREEN
                )
            }
            selectedWalls.forEach {
                (it.modelInstance.materials.get(0).get(ColorAttribute.Diffuse) as ColorAttribute).color.set(
                    Color.BLUE
                )
            }
        }
        selectedNodes.forEach {
            batch.render(it.modelInstance)
        }
    }


    private fun setViewportSize(screenX: Float, screenY: Float, width: Float, height: Float) {
        viewportScreenX = screenX
        viewportScreenY = screenY
        viewportWidth = width
        viewportHeight = height
    }

    companion object {
        private val auxVector3_1 = Vector3()
        private val auxVector3_2 = Vector3()
        private val auxRay = Ray()
        private val groundPlane = Plane(Vector3.Y, 0F)
        private val auxVector2_1 = Vector2()
        private val auxList = mutableListOf<Coords>()
        private val auxBoundingBox = BoundingBox()
    }

}
