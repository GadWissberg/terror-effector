package com.gadarts.te.renderer.handlers.actions.types

interface SingleStepAction : Action {
    override fun takeStep(extraInfo: Any) {

    }

    override fun isSingleStep(): Boolean {
        return true
    }

}
