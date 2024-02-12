package com.gadarts.te.renderer.handlers.cursor.react

import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.math.Matrix4
import com.gadarts.te.common.assets.GameAssetsManager
import com.gadarts.te.common.map.WallCreator
import com.gadarts.te.renderer.handlers.HandlerOnEvent
import com.gadarts.te.renderer.handlers.HandlersData
import com.gadarts.te.renderer.handlers.cursor.CursorHandler

class CursorHandlerOnNodesHeightSet(private val cursorHandler: CursorHandler) : HandlerOnEvent {
    override fun react(
        msg: Telegram,
        handlersData: HandlersData,
        gameAssetsManager: GameAssetsManager,
        dispatcher: MessageDispatcher,
        wallCreator: WallCreator
    ) {
        cursorHandler.selectedNodes.forEach {
            val node = handlersData.mapData.getNode(it.coords.x, it.coords.z)
            if (node != null) {
                it.modelInstance.transform.values[Matrix4.M13] = node.height
            }
        }
    }

}
