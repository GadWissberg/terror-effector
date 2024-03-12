package com.gadarts.te.renderer.handlers.actions.types

import com.gadarts.te.renderer.model.MapData
import com.gadarts.te.renderer.model.PlacedEnvObject

class DeleteEnvObjectAction(private val toDelete: PlacedEnvObject) : SingleStepAction {
    override fun begin(mapData: MapData) {
        mapData.deleteEnvObject(toDelete)
    }

}
