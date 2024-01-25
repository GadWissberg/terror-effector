package com.gadarts.te.renderer.handlers

import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.ai.msg.Telegraph
import com.badlogic.gdx.utils.Disposable

abstract class BaseHandler(val dispatcher: MessageDispatcher) : Telegraph, Disposable {
}
