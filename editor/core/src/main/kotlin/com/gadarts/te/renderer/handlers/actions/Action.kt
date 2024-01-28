package com.gadarts.te.renderer.handlers.actions

interface Action {
    fun begin()
    fun takeStep(extraInfo: Any)

}
