package com.gadarts.te.renderer.model

import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.gadarts.te.common.definitions.env.EnvObjectDefinition
import com.gadarts.te.common.map.Coords
import com.gadarts.te.common.map.element.Direction

class PlacedEnvObject(
    coords: Coords,
    definition: EnvObjectDefinition,
    direction: Direction,
    val modelInstance: ModelInstance
) : PlacedElement(coords, definition, direction)
