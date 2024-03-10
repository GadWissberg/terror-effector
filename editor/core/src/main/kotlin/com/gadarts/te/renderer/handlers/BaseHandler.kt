package com.gadarts.te.renderer.handlers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.utils.Disposable
import com.gadarts.te.DebugSettings
import com.gadarts.te.EditorEvents
import com.gadarts.te.common.assets.GameAssetsManager
import com.gadarts.te.common.map.WallCreator

abstract class BaseHandler : Telegraph, Disposable {
    protected lateinit var wallCreator: WallCreator
    protected lateinit var handlersData: HandlersData
    protected lateinit var gameAssetsManager: GameAssetsManager
    protected lateinit var dispatcher: MessageDispatcher

    open fun onInitialize(
        dispatcher: MessageDispatcher,
        gameAssetsManager: GameAssetsManager,
        handlersData: HandlersData,
    ) {
        this.dispatcher = dispatcher
        this.gameAssetsManager = gameAssetsManager
        this.wallCreator = WallCreator(gameAssetsManager, true)
        this.handlersData = handlersData
        getSubscribedEvents().forEach { dispatcher.addListener(this, it.key.ordinal) }
    }

    protected open fun getSubscribedEvents(): Map<EditorEvents, HandlerOnEvent> {
        return emptyMap()
    }

    protected fun addToInputMultiplexer(inputProcessor: InputProcessor) {
        val inputMultiplexer = Gdx.input.inputProcessor as InputMultiplexer
        inputMultiplexer.addProcessor(inputProcessor)
    }

    override fun handleMessage(msg: Telegram): Boolean {
        if (DebugSettings.FREELOOK) return false

        var handled = false

        val subscribedEvents = getSubscribedEvents()
        if (subscribedEvents.containsKey(EditorEvents.entries[msg.message])) {
            subscribedEvents[EditorEvents.entries[msg.message]]?.react(
                msg,
                handlersData,
                gameAssetsManager,
                dispatcher,
                wallCreator
            )
            handled = true
        }

        return handled
    }

    abstract fun onUpdate()
    abstract fun onRender(batch: ModelBatch, environment: Environment)
}
