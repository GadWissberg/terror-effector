package com.gadarts.te.renderer.handlers.actions.types.place

import com.gadarts.te.common.assets.definitions.env.EnvObjectDefinition
import com.gadarts.te.common.map.Coords
import com.gadarts.te.common.map.element.Direction
import com.gadarts.te.renderer.handlers.actions.types.SingleStepAction
import com.gadarts.te.renderer.model.MapData

open class PlaceEnvObjectAction(
    private val coords: Coords,
    private val height: Float,
    private val definition: EnvObjectDefinition,
    private val direction: Direction
) : SingleStepAction {

    override fun begin(mapData: MapData) {
        mapData.insertEnvObject(coords, height, definition, direction)
    }


}
