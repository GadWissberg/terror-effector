package com.gadarts.te.renderer.handlers.cursor

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.math.*
import com.badlogic.gdx.math.Matrix4.M13
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.math.collision.Ray
import com.badlogic.gdx.utils.Disposable
import com.gadarts.te.*
import com.gadarts.te.common.CameraUtils
import com.gadarts.te.common.assets.GameAssetsManager
import com.gadarts.te.common.map.Coords
import com.gadarts.te.common.map.MapNodeData
import com.gadarts.te.common.map.MapUtils
import com.gadarts.te.common.map.Wall
import com.gadarts.te.renderer.handlers.BaseHandler
import com.gadarts.te.renderer.handlers.HandlerOnEvent
import com.gadarts.te.renderer.handlers.HandlersData
import com.gadarts.te.renderer.handlers.cursor.react.CursorHandlerOnModeSelected
import com.gadarts.te.renderer.handlers.cursor.react.CursorHandlerOnNodesHeightSet
import com.gadarts.te.renderer.handlers.cursor.react.CursorHandlerOnTextureSet
import kotlin.math.abs
import kotlin.math.max

class CursorHandlerImpl : Disposable, InputProcessor, BaseHandler(), CursorHandler {
    private var highlightWall: Wall? = null
    override var selectedMode: Modes = Modes.FLOOR
    override val selectedNodes = mutableListOf<SelectedNode>()

    private val originalFloorModelInstanceCursorPosition = Vector3()
    private var selecting: Boolean = false
    private val prevFloorCursorPosition = Vector3()
    private var viewportScreenY: Float = 0.0f
    private var viewportScreenX: Float = 0.0f
    private var viewportHeight: Float = 0.0f
    private var viewportWidth: Float = 0.0f
    private var cursorFading: Float = 0.0f
    private val cursorMaterialBlendingAttribute: BlendingAttribute
    private val floorModelInstanceCursor: ModelInstance
    private val floorModel: Model = MapUtils.createFloorModel()

    init {
        floorModelInstanceCursor = ModelInstance(floorModel)
        floorModelInstanceCursor.nodes.get(0).isAnimated = true
        cursorMaterialBlendingAttribute = BlendingAttribute()
        cursorMaterialBlendingAttribute.opacity = 1f
        val cursorMaterial = floorModelInstanceCursor.materials.get(0)
        cursorMaterial.set(cursorMaterialBlendingAttribute)
        cursorMaterial.set(ColorAttribute.createDiffuse(Color.GREEN))
    }

    override fun dispose() {
        GeneralUtils.disposeObject(this, CursorHandlerImpl::class)
    }

    private fun turnOnSelectingCursor() {
        selecting = true
        floorModelInstanceCursor.nodes.get(0).localTransform.trn(0.5F, 0.01F, 0.5F)
        floorModelInstanceCursor.calculateTransforms()
        floorModelInstanceCursor.transform.trn(-0.5F, 0F, -0.5F)
        floorModelInstanceCursor.transform.getTranslation(originalFloorModelInstanceCursorPosition)
    }

    private fun turnOffSelectingCursor(screenX: Int, screenY: Int) {
        selecting = false
        floorModelInstanceCursor.transform.values[Matrix4.M00] = 1F
        floorModelInstanceCursor.transform.values[Matrix4.M22] = 1F
        floorModelInstanceCursor.nodes.get(0).localTransform.trn(-0.5F, -0.01F, -0.5F)
        updateFloorCursorPosition(screenX, screenY)
        floorModelInstanceCursor.calculateTransforms()
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
            EditorEvents.MODE_SELECTED to CursorHandlerOnModeSelected(this)
        )
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (DebugSettings.FREELOOK) return false

        var handled = false
        if (button == Input.Buttons.LEFT) {
            if (selectedNodes.isEmpty()) {
                val position = floorModelInstanceCursor.transform.getTranslation(auxVector3_2)
                dispatcher.dispatchMessage(
                    EditorEvents.CLICKED_GRID_CELL.ordinal,
                    listOf(Coords(position.x.toInt(), position.z.toInt()))
                )
            } else {
                selectedNodes.clear()
            }
            handled = true
        } else if (button == Input.Buttons.RIGHT && !selecting) {
            turnOnSelectingCursor()
            handled = true
        }
        return handled
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (button == Input.Buttons.RIGHT && selecting) {
            selectNodes(screenX, screenY)
            return true
        }
        return false
    }

    private fun selectNodes(screenX: Int, screenY: Int) {
        val northWestPosition = floorModelInstanceCursor.transform.getTranslation(auxVector3_2)
        val selectionBoxSize = floorModelInstanceCursor.transform.getScale(auxVector3_1)
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
            updateFloorCursorPosition(screenX, screenY)
            val current = floorModelInstanceCursor.transform.getTranslation(auxVector3_2).sub(0.5F, 0F, 0.5F)
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

        if (selectedMode == Modes.FLOOR) {
            if (!selecting) {
                updatePrevCursorPosition()
                updateFloorCursorPosition(screenX, screenY)
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
        floorModelInstanceCursor.transform.values[Matrix4.M00] =
            abs(mousePositionX - (originalFloorModelInstanceCursorPosition.x)) + 1F
        val mousePositionZ = mousePosition.z.toInt()
        floorModelInstanceCursor.transform.values[Matrix4.M22] =
            abs(mousePositionZ - (originalFloorModelInstanceCursorPosition.z)) + 1F
        if (mousePositionX < (originalFloorModelInstanceCursorPosition.x)) {
            floorModelInstanceCursor.transform.values[Matrix4.M03] = mousePositionX.toFloat()
        }
        if (mousePositionZ < (originalFloorModelInstanceCursorPosition.z)) {
            floorModelInstanceCursor.transform.values[Matrix4.M23] = mousePositionZ.toFloat()
        }
        floorModelInstanceCursor.transform.values[M13] = mousePosition.y + 0.01F
    }

    private fun updatePrevCursorPosition() {
        val prev = floorModelInstanceCursor.transform.getTranslation(auxVector3_2)
        prevFloorCursorPosition.set(prev.x.toInt().toFloat(), 0F, prev.z.toInt().toFloat())
    }

    private fun updateFloorCursorPosition(screenX: Int, screenY: Int) {
        val position = fetchGridCellAtMouse(screenX, screenY)
        val x = position.x.toInt()
        val z = position.z.toInt()
        floorModelInstanceCursor.transform.setTranslation(
            MathUtils.clamp(x.toFloat(), 0F, handlersData.mapData.mapSize.toFloat()) + 0.5F,
            MathUtils.clamp(handlersData.mapData.getNode(x, z)?.height ?: 0F, 0F, MapNodeData.MAX_FLOOR_HEIGHT),
            MathUtils.clamp(z.toFloat(), 0F, handlersData.mapData.mapSize.toFloat()) + 0.5F
        )
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
        return auxVector3_2
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        val position = floorModelInstanceCursor.transform.getTranslation(auxVector3_2)
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
        floorModelInstanceCursor.transform.values[M13] =
            MathUtils.clamp(
                handlersData.mapData.getNode(position.x.toInt(), position.z.toInt())?.height ?: 0F,
                0F,
                MapNodeData.MAX_FLOOR_HEIGHT
            )
        return true
    }

    override fun onUpdate() {
        cursorMaterialBlendingAttribute.opacity = max(MathUtils.sin(cursorFading / 10F), 0.1F)
        cursorFading += 1
    }

    override fun onRender(batch: ModelBatch) {
        if (selectedMode == Modes.FLOOR) {
            batch.render(floorModelInstanceCursor)
        } else if (highlightWall != null) {
            (highlightWall!!.modelInstance.materials.get(0).get(BlendingAttribute.Type) as BlendingAttribute).opacity =
                0F
        }
        selectedNodes.forEach { batch.render(it.modelInstance) }
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
