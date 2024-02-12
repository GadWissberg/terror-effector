package com.gadarts.te.renderer.handlers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController
import com.badlogic.gdx.math.*
import com.badlogic.gdx.math.collision.Ray
import com.gadarts.te.DebugSettings
import com.gadarts.te.GeneralUtils
import com.gadarts.te.common.assets.GameAssetsManager

class CameraHandler : InputProcessor,
    BaseHandler() {
    private var panning: Boolean = false
    private val lastMouseClickPosition = Vector2()
    private val intersectionPoint = Vector3(-1F, -1F, -1F)
    private var ray: Ray? = null
    private var freelook: CameraInputController? = null

    override fun keyDown(keycode: Int): Boolean {
        var result = false
        if (keycode == Input.Keys.CONTROL_LEFT) {
            ray = handlersData.camera.getPickRay(Gdx.graphics.width / 2F, Gdx.graphics.height / 2F)
            result = true
        } else if (keycode == Input.Keys.ALT_LEFT) {
            panning = true
            result = true
        }
        return result
    }

    override fun keyUp(keycode: Int): Boolean {
        var result = false
        if (keycode == Input.Keys.CONTROL_LEFT) {
            ray = null
            intersectionPoint.set(-1F, -1F, -1F)
            result = true
        } else if (keycode == Input.Keys.ALT_LEFT) {
            panning = false
            result = true
        }
        return result
    }

    override fun keyTyped(character: Char): Boolean {
        return false
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (DebugSettings.FREELOOK) return false

        var result = false
        if (button == Input.Buttons.RIGHT && ray != null) {
            Intersector.intersectRayPlane(ray, groundPlane, intersectionPoint)
            result = true
        } else if (panning) {
            lastMouseClickPosition.set(screenX.toFloat(), screenY.toFloat())
        }
        return result
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        var result = false
        if (button == Input.Buttons.RIGHT && !intersectionPoint.epsilonEquals(-1F, -1F, -1F)) {
            intersectionPoint.set(-1F, -1F, -1F)
            result = true
        } else if (panning) {
            lastMouseClickPosition.set(screenX.toFloat(), screenY.toFloat())
        }
        return result
    }

    override fun touchCancelled(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        if (DebugSettings.FREELOOK) return false
        if (ray != null
            && intersectionPoint.x != -1F
            && intersectionPoint.y != -1F
            && intersectionPoint.z != -1F
        ) {
            rotate(screenX, screenY)
            return true
        } else if (panning) {
            pan(screenX, screenY)
            return true
        }
        return false
    }

    private fun pan(screenX: Int, screenY: Int) {
        val xFloat = screenX.toFloat()
        val yFloat = screenY.toFloat()
        val velocity: Vector2 = lastMouseClickPosition.sub(xFloat, yFloat).scl(0.024f)
        val left: Vector3 = auxVector1.set(handlersData.camera.direction).crs(handlersData.camera.up).nor().scl(0.3f)
        val x: Float = handlersData.camera.direction.x * -velocity.y + left.x * velocity.x
        val z: Float = handlersData.camera.direction.z * -velocity.y + left.z * velocity.x
        handlersData.camera.translate(x, 0F, z)
        lastMouseClickPosition.set(xFloat, yFloat)
    }

    private fun rotate(screenX: Int, screenY: Int) {
        val xFloat = screenX.toFloat()
        val yFloat = screenY.toFloat()
        val angle = MathUtils.clamp(
            (lastMouseClickPosition.x - screenX) * 0.1F,
            -MAX_STEP_ROTATION_ANGLE,
            MAX_STEP_ROTATION_ANGLE
        )
        handlersData.camera.rotateAround(
            intersectionPoint, Vector3.Y, angle
        )
        lastMouseClickPosition.set(xFloat, yFloat)
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return false
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        return false
    }

    override fun onInitialize(
        dispatcher: MessageDispatcher,
        gameAssetsManager: GameAssetsManager,
        handlersData: HandlersData
    ) {
        super.onInitialize(dispatcher, gameAssetsManager, handlersData)
        if (DebugSettings.FREELOOK) {
            freelook = CameraInputController(handlersData.camera)
            addToInputMultiplexer(freelook!!)
        }
        addToInputMultiplexer(this)
    }

    override fun onUpdate() {
        freelook?.update()
    }

    override fun onRender(batch: ModelBatch) {

    }

    override fun dispose() {
        GeneralUtils.disposeObject(this, CameraHandler::class)
    }

    companion object {
        const val MAX_STEP_ROTATION_ANGLE: Float = 6.4f
        val groundPlane = Plane(Vector3.Y, 0F)
        private val auxVector1 = Vector3()
    }

}
