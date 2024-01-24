package com.gadarts.te.renderer.handlers

import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import com.badlogic.gdx.math.Vector2
import com.gadarts.te.EditorEvents
import com.gadarts.te.common.map.MapNodeData
import com.gadarts.te.renderer.handlers.actions.PlaceFloorTilesAction

class DrawingHandler(dispatcher: MessageDispatcher, mapSize: Int) : Telegraph, BaseHandler(dispatcher) {
    private val mapData = arrayOfNulls<MapNodeData>(mapSize * mapSize)

    init {
        dispatcher.addListener(this, EditorEvents.CLICKED_GRID_CELL.ordinal)
    }

    override fun handleMessage(msg: Telegram): Boolean {
        var handled = false

        if (msg.message == EditorEvents.CLICKED_GRID_CELL.ordinal) {
            val action = PlaceFloorTilesAction(msg.extraInfo as Vector2, mapData)
            dispatcher.dispatchMessage(EditorEvents.ACTION_BEGIN.ordinal, action)
            handled = true
        }

        return handled
    }


}
