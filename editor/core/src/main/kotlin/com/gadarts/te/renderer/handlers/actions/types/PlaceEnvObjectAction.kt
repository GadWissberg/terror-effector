package com.gadarts.te.renderer.handlers.actions.types

import com.gadarts.te.common.WallObjects
import com.gadarts.te.common.map.Coords
import com.gadarts.te.common.map.MapNodeData
import com.gadarts.te.renderer.model.MapData

open class PlaceEnvObjectAction(
    private val mapNodeData: MapNodeData,
    private val definition: WallObjects
) : SingleStepAction {

    override fun begin(mapData: MapData) {
        mapData.insertEnvObject(Coords(mapNodeData.coords), definition)
    }


}
