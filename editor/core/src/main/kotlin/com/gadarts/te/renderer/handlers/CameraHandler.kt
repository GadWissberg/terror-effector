package com.gadarts.te.renderer.handlers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController
import com.badlogic.gdx.math.*
import com.badlogic.gdx.math.collision.Ray
import com.gadarts.te.DebugSettings
import com.gadarts.te.GeneralUtils

class CameraHandler : InputProcessor,
    BaseHandler() {
    private val lastMouseClickPosition = Vector2()
    private val intersectionPoint = Vector3(-1F, -1F, -1F)
    private var ray: Ray? = null
    private var freelook: CameraInputController? = null

    init {
        if (DebugSettings.FREELOOK) {
            freelook = CameraInputController(handlersData.camera)
        }
        addToInputMultiplexer(this)
    }

    override fun keyDown(keycode: Int): Boolean {
        var result = false
        if (keycode == Input.Keys.CONTROL_LEFT) {
            ray = handlersData.camera.getPickRay(Gdx.graphics.width / 2F, Gdx.graphics.height / 2F)
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
        }
        return result
    }

    override fun keyTyped(character: Char): Boolean {
        return false
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        var result = false
        if (button == Input.Buttons.RIGHT && ray != null) {
            Intersector.intersectRayPlane(ray, groundPlane, intersectionPoint)
            result = true
        }
        return result
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        var result = false
        if (button == Input.Buttons.RIGHT) {
            intersectionPoint.set(-1F, -1F, -1F)
            result = true
        }
        return result
    }

    override fun touchCancelled(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        if (DebugSettings.FREELOOK) return false
        var result = false
        if (ray != null
            && intersectionPoint.x != -1F
            && intersectionPoint.y != -1F
            && intersectionPoint.z != -1F
        ) {
            result = true
            val xFloat = screenX.toFloat()
            val yFloat = screenY.toFloat()
            val angle = MathUtils.clamp(
                (lastMouseClickPosition.x - screenX) * 0.1F,
                -MAX_ROTATION_ANGLE_STEP,
                MAX_ROTATION_ANGLE_STEP
            )
            handlersData.camera.rotateAround(
                intersectionPoint, Vector3.Y, angle
            )
            lastMouseClickPosition.set(xFloat, yFloat)
        }
        return result
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return false
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        return false
    }

    override fun onUpdate() {
        freelook?.update()
    }

    override fun onRender(batch: ModelBatch) {

    }

    companion object {
        const val MAX_ROTATION_ANGLE_STEP: Float = 6.4f
        val groundPlane = Plane(Vector3.Y, 0F)
    }

    override fun dispose() {
        GeneralUtils.disposeObject(this, CameraHandler::class)
    }

}
