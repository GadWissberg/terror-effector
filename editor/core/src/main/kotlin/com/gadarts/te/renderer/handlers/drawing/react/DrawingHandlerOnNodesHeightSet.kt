package com.gadarts.te.renderer.handlers.drawing.react

import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.math.Matrix4.M13
import com.gadarts.te.common.assets.GameAssetsManager
import com.gadarts.te.common.definitions.env.EnvObjectDefinition
import com.gadarts.te.common.map.MapNodeData
import com.gadarts.te.common.map.WallCreator
import com.gadarts.te.renderer.handlers.HandlerOnEvent
import com.gadarts.te.renderer.handlers.HandlersData

@Suppress("UNCHECKED_CAST")
class DrawingHandlerOnNodesHeightSet : HandlerOnEvent {
    override fun react(
        msg: Telegram,
        handlersData: HandlersData,
        gameAssetsManager: GameAssetsManager,
        dispatcher: MessageDispatcher,
        wallCreator: WallCreator
    ) {
        val nodes = msg.extraInfo as List<MapNodeData>
        handlersData.mapData.placedEnvObjects.filter {
            nodes.find { node -> it.coords.equals(node.coords) } != null
        }.forEach {
            it.modelInstance.transform.values[M13] =
                nodes[0].height + (it.declaration as EnvObjectDefinition).modelDefinition.modelOffset.y
        }
    }

}
