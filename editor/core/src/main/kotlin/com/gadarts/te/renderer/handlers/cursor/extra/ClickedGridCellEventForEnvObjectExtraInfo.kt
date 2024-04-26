package com.gadarts.te.renderer.handlers.cursor.extra

import com.gadarts.te.common.assets.definitions.env.EnvObjectDefinition
import com.gadarts.te.common.map.Coords
import com.gadarts.te.common.map.element.Direction

class ClickedGridCellEventForEnvObjectExtraInfo(
    val coords: Coords,
    val definition: EnvObjectDefinition,
    val direction: Direction
)
