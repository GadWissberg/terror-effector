package com.gadarts.te.renderer.handlers

import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.ai.msg.Telegram
import com.gadarts.te.EditorEvents
import com.gadarts.te.renderer.handlers.actions.Action

class ActionsHandler(dispatcher: MessageDispatcher) : BaseHandler(dispatcher) {
    private var currentAction: Action? = null

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

}
