package com.gadarts.te.renderer.handlers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.ai.msg.Telegraph
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.utils.Disposable
import com.gadarts.te.EditorEvents
import com.gadarts.te.common.assets.GameAssetsManager

abstract class BaseHandler : Telegraph, Disposable {
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
        this.handlersData = handlersData
        getSubscribedEvents().forEach { dispatcher.addListener(this, it.ordinal) }
    }

    protected open fun getSubscribedEvents(): List<EditorEvents> {
        return listOf()
    }

    protected fun addToInputMultiplexer(inputProcessor: InputProcessor) {
        val inputMultiplexer = Gdx.input.inputProcessor as InputMultiplexer
        inputMultiplexer.addProcessor(inputProcessor)
    }

    abstract fun onUpdate()
    abstract fun onRender(batch: ModelBatch)
}
