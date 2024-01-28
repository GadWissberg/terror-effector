package com.gadarts.te.renderer.handlers.actions

import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.ai.msg.Telegram
import com.gadarts.te.common.assets.GameAssetsManager
import com.gadarts.te.renderer.handlers.HandlerOnEvent
import com.gadarts.te.renderer.handlers.HandlersData

class ActionsHandlerOnActionBegin(private val actionsHandler: ActionsHandler) :
    HandlerOnEvent {
    override fun react(
        msg: Telegram,
        handlersData: HandlersData,
        gameAssetsManager: GameAssetsManager,
        dispatcher: MessageDispatcher
    ) {
        actionsHandler.currentAction = msg.extraInfo as Action
        actionsHandler.currentAction!!.begin()
    }

}
