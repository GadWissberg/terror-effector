package com.gadarts.te.renderer.handlers

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.math.*
import com.badlogic.gdx.math.collision.Ray
import com.badlogic.gdx.utils.Disposable
import com.gadarts.te.EditorEvents
import com.gadarts.te.GeneralUtils
import com.gadarts.te.TerrorEffectorEditor
import com.gadarts.te.common.assets.GameAssetsManager
import com.gadarts.te.common.map.MapUtils
import kotlin.math.max

class CursorHandler :
    Disposable, InputProcessor, BaseHandler() {
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
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        return false
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
            dispatcher.dispatchMessage(EditorEvents.DRAGGED_GRID_CELL.ordinal)
        }
        return true
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        updatePrevCursorPosition()
        updateFloorCursorPosition(screenX, screenY)
        return true
    }

    private fun updatePrevCursorPosition() {
        val prev = floorModelInstanceCursor.transform.getTranslation(auxVector3_2)
        prevFloorCursorPosition.set(prev.x.toInt().toFloat(), 0F, prev.z.toInt().toFloat())
    }

    private fun updateFloorCursorPosition(screenX: Int, screenY: Int) {
        val position = fetchGridCellAtMouse(screenX, screenY)
        floorModelInstanceCursor.transform.setTranslation(
            MathUtils.clamp(position.x.toInt().toFloat(), 0F, handlersData.mapData.mapSize.toFloat()) + 0.5F,
            0F,
            MathUtils.clamp(position.z.toInt().toFloat(), 0F, handlersData.mapData.mapSize.toFloat()) + 0.5F
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
        return false
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
        private val auxVector3_2 = Vector3()
        private val auxRay = Ray()
        private val groundPlane = Plane(Vector3.Y, 0F)
        private val auxVector2_1 = Vector2()

    }

    override fun handleMessage(msg: Telegram?): Boolean {
        return false
    }
}
