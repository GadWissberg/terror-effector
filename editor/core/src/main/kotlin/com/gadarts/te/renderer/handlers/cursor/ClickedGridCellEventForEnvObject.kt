package com.gadarts.te.renderer.handlers.cursor

import com.gadarts.te.common.definitions.EnvObjectDefinition
import com.gadarts.te.common.map.Coords
import com.gadarts.te.common.map.element.Direction

class ClickedGridCellEventForEnvObject(
    val coords: Coords,
    val definition: EnvObjectDefinition,
    val direction: Direction
)
