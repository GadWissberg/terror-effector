package com.gadarts.te.renderer.handlers.actions

import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.gadarts.te.EditorEvents
import com.gadarts.te.GeneralUtils
import com.gadarts.te.renderer.handlers.BaseHandler

class ActionsHandler : BaseHandler(), InputProcessor {
    private var currentAction: Action? = null

    override fun getSubscribedEvents(): List<EditorEvents> {
        return listOf(
            EditorEvents.ACTION_BEGIN,
            EditorEvents.ACTION_TAKE_STEP,
            EditorEvents.ACTION_DONE
        )
    }

    override fun onUpdate() {
    }

    override fun onRender(batch: ModelBatch) {
    }

    override fun handleMessage(msg: Telegram): Boolean {
        var handled = false

        when (msg.message) {
            EditorEvents.ACTION_BEGIN.ordinal -> {
                currentAction = msg.extraInfo as Action
                currentAction!!.takeStep()
                handled = true
            }

            EditorEvents.ACTION_TAKE_STEP.ordinal -> {
                currentAction!!.takeStep()
                handled = true
            }

            EditorEvents.ACTION_DONE.ordinal -> {
                currentAction = null
                handled = true
            }
        }

        return handled
    }

    override fun dispose() {
        GeneralUtils.disposeObject(this, ActionsHandler::class)
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
