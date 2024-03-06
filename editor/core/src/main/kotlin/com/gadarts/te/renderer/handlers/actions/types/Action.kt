package com.gadarts.te.renderer.handlers.actions.types

import com.gadarts.te.renderer.model.MapData

interface Action {
    fun begin(mapData: MapData)
    fun takeStep(extraInfo: Any)

    fun isSingleStep(): Boolean {
        return false
    }

}
