package com.gadarts.te.renderer.handlers.drawing

import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.ai.msg.Telegram
import com.gadarts.te.common.assets.GameAssetsManager
import com.gadarts.te.common.assets.texture.SurfaceTextures
import com.gadarts.te.renderer.handlers.HandlerOnEvent
import com.gadarts.te.renderer.handlers.HandlersData

class DrawingHandlerOnTextureSelected(private val drawingHandler: DrawingHandler) : HandlerOnEvent {
    override fun react(
        msg: Telegram,
        handlersData: HandlersData,
        gameAssetsManager: GameAssetsManager,
        dispatcher: MessageDispatcher,
    ) {
        drawingHandler.selectedTexture = msg.extraInfo as SurfaceTextures
    }


}
