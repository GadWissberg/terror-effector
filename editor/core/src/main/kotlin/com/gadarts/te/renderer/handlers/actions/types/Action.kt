package com.gadarts.te.renderer.handlers.actions.types

interface Action {
    fun begin()
    fun takeStep(extraInfo: Any)

    fun isSingleStep(): Boolean {
        return false
    }

}
