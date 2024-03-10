package com.gadarts.te.renderer.handlers.actions.handler

import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.gadarts.te.EditorEvents
import com.gadarts.te.GeneralUtils
import com.gadarts.te.renderer.handlers.BaseHandler
import com.gadarts.te.renderer.handlers.HandlerOnEvent
import com.gadarts.te.renderer.handlers.actions.types.Action

class ActionsHandlerImpl : ActionsHandler, BaseHandler(), InputProcessor {
    override var currentAction: Action? = null

    override fun getSubscribedEvents(): Map<EditorEvents, HandlerOnEvent> {
        return mapOf(
            EditorEvents.ACTION_BEGIN to ActionsHandlerOnActionBegin(this),
            EditorEvents.ACTION_TAKE_STEP to ActionsHandlerOnActionTakeStep(this),
            EditorEvents.ACTION_DONE to ActionsHandlerOnActionDone(this)
        )
    }

    override fun onUpdate() {
    }

    override fun onRender(batch: ModelBatch, environment: Environment) {
    }

    override fun dispose() {
        GeneralUtils.disposeObject(this, ActionsHandlerImpl::class)
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

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchCancelled(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        return false
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return false
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        return false
    }

}
