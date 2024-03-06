package com.gadarts.te.renderer.handlers.actions.handler

import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.ai.msg.Telegram
import com.gadarts.te.EditorEvents
import com.gadarts.te.common.assets.GameAssetsManager
import com.gadarts.te.common.map.WallCreator
import com.gadarts.te.renderer.handlers.HandlerOnEvent
import com.gadarts.te.renderer.handlers.HandlersData
import com.gadarts.te.renderer.handlers.actions.types.Action

class ActionsHandlerOnActionBegin(private val actionsHandler: ActionsHandler) :
    HandlerOnEvent {
    override fun react(
        msg: Telegram,
        handlersData: HandlersData,
        gameAssetsManager: GameAssetsManager,
        dispatcher: MessageDispatcher,
        wallCreator: WallCreator
    ) {
        actionsHandler.currentAction = msg.extraInfo as Action
        actionsHandler.currentAction!!.begin(handlersData.mapData)

        if (actionsHandler.currentAction!!.isSingleStep()) {
            dispatcher.dispatchMessage(EditorEvents.ACTION_DONE.ordinal)
        }
    }

}
