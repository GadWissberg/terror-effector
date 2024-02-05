package com.gadarts.te.renderer.handlers.drawing.react

import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.ai.msg.Telegram
import com.gadarts.te.EditorEvents
import com.gadarts.te.common.map.Coords
import com.gadarts.te.common.map.MapNodeData
import com.gadarts.te.common.map.WallCreator
import com.gadarts.te.renderer.handlers.HandlerOnEvent
import com.gadarts.te.renderer.handlers.HandlersData
import com.gadarts.te.renderer.handlers.actions.types.ChangeFloorHeightAction

abstract class DrawingHandlerOnScrolledEvent : HandlerOnEvent {
    protected fun invokeHeightChange(
        msg: Telegram,
        handlersData: HandlersData,
        wallCreator: WallCreator,
        dispatcher: MessageDispatcher,
        value: Int,
    ) {
        val nodes = msg.extraInfo as List<*>
        auxList.clear()
        nodes.forEach {
            it as Coords
            val nodeData = handlersData.mapData.getNode(it.x, it.z)
            if (nodeData != null) {
                auxList.add(nodeData)
            }
        }
        val action = ChangeFloorHeightAction(
            auxList,
            handlersData.mapData,
            value,
            wallCreator
        )
        dispatcher.dispatchMessage(EditorEvents.ACTION_BEGIN.ordinal, action)
    }

    companion object {
        private val auxList = mutableListOf<MapNodeData>()
    }


}
