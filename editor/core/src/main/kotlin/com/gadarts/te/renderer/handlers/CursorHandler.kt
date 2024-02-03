package com.gadarts.te.renderer.handlers

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
import com.badlogic.gdx.math.collision.Ray
import com.badlogic.gdx.utils.Disposable
import com.gadarts.te.DebugSettings
import com.gadarts.te.EditorEvents
import com.gadarts.te.GeneralUtils
import com.gadarts.te.TerrorEffectorEditor
import com.gadarts.te.common.assets.GameAssetsManager
import com.gadarts.te.common.map.MapNodeData
import com.gadarts.te.common.map.MapUtils
import kotlin.math.abs
import kotlin.math.max

class CursorHandler :
    Disposable, InputProcessor, BaseHandler() {
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
        addToInputMultiplexer(this)
    }

    override fun dispose() {
        GeneralUtils.disposeObject(this, CursorHandler::class)
    }

    override fun keyDown(keycode: Int): Boolean {
        var handled = false
        if (keycode == Input.Keys.SHIFT_LEFT) {
            selecting = true
            handled = true
            floorModelInstanceCursor.nodes.get(0).localTransform.trn(0.5F, 0F, 0.5F)
            floorModelInstanceCursor.calculateTransforms()
            floorModelInstanceCursor.transform.trn(-0.5F, 0F, -0.5F)
            floorModelInstanceCursor.transform.getTranslation(originalFloorModelInstanceCursorPosition)
        }
        return handled
    }

    override fun keyUp(keycode: Int): Boolean {
        var handled = false
        if (keycode == Input.Keys.SHIFT_LEFT) {
            selecting = false
            handled = true
            floorModelInstanceCursor.transform.values[Matrix4.M00] = 1F
            floorModelInstanceCursor.transform.values[Matrix4.M22] = 1F
            floorModelInstanceCursor.nodes.get(0).localTransform.trn(-0.5F, 0F, -0.5F)
            floorModelInstanceCursor.calculateTransforms()
        }
        return handled
    }

    override fun onInitialize(
        dispatcher: MessageDispatcher,
        gameAssetsManager: GameAssetsManager,
        handlersData: HandlersData,
    ) {
        super.onInitialize(dispatcher, gameAssetsManager, handlersData)
        setViewportSize(
            handlersData.screenX,
            handlersData.screenY,
            TerrorEffectorEditor.WINDOW_WIDTH - handlersData.screenX,
            handlersData.heightUnderBars
        )
    }

    override fun keyTyped(character: Char): Boolean {
        return false
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (DebugSettings.FREELOOK) return false

        var result = false
        if (button == Input.Buttons.LEFT) {
            val position = floorModelInstanceCursor.transform.getTranslation(auxVector3_2)
            dispatcher.dispatchMessage(EditorEvents.CLICKED_GRID_CELL.ordinal, auxVector2_1.set(position.x, position.z))
            result = true
        }
        return result
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchCancelled(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        updatePrevCursorPosition()
        updateFloorCursorPosition(screenX, screenY)
        val current = floorModelInstanceCursor.transform.getTranslation(auxVector3_2).sub(0.5F, 0F, 0.5F)
        if (!prevFloorCursorPosition.epsilonEquals(current.x, 0F, current.z, 0.01F)) {
            dispatcher.dispatchMessage(EditorEvents.DRAGGED_GRID_CELL.ordinal, auxVector2_1.set(current.x, current.z))
        }
        return true
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        if (DebugSettings.FREELOOK) return false

        if (!selecting) {
            updatePrevCursorPosition()
            updateFloorCursorPosition(screenX, screenY)
        } else {
            val position = floorModelInstanceCursor.transform.getTranslation(auxVector3_1)
            val mousePosition = fetchGridCellAtMouse(screenX, screenY)
            floorModelInstanceCursor.transform.values[Matrix4.M00] =
                abs(mousePosition.x.toInt() - (position.x)) + 1F
            floorModelInstanceCursor.transform.values[Matrix4.M22] =
                abs(mousePosition.z.toInt() - (position.z)) + 1F
            if (mousePosition.x.toInt() < (position.x)) {
                floorModelInstanceCursor.transform.trn(
                    originalFloorModelInstanceCursorPosition.x - (position.x + mousePosition.x.toInt() + 1F),
                    0F,
                    0F
                )
            }
            if (mousePosition.z.toInt() < (position.z)) {
                floorModelInstanceCursor.transform.trn(
                    0F,
                    0F,
                    originalFloorModelInstanceCursorPosition.z - position.z + mousePosition.z.toInt()
                )
            }
        }
        return true
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
            MathUtils.clamp(handlersData.mapData.getTile(x, z)?.height ?: 0F, 0F, MapNodeData.MAX_FLOOR_HEIGHT),
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
        dispatcher.dispatchMessage(
            (if (amountY > 0) EditorEvents.SCROLLED_DOWN else EditorEvents.SCROLLED_UP).ordinal,
            auxVector2_1.set(position.x, position.z)
        )
        floorModelInstanceCursor.transform.values[M13] =
            MathUtils.clamp(
                handlersData.mapData.getTile(position.x.toInt(), position.z.toInt())?.height ?: 0F,
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
        batch.render(floorModelInstanceCursor)
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

    }

}
