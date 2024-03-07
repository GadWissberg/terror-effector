package com.gadarts.te.renderer.handlers.actions.types

import com.gadarts.te.common.map.element.EnvObject
import com.gadarts.te.renderer.model.MapData

class DeleteEnvObjectAction(private val toDelete: EnvObject) : SingleStepAction {
    override fun begin(mapData: MapData) {
        mapData.deleteEnvObject(toDelete)
    }

}
