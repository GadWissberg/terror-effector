package com.gadarts.te.renderer.model

import com.gadarts.te.common.definitions.ElementDefinition
import com.gadarts.te.common.map.Coords
import com.gadarts.te.common.map.element.Direction

abstract class PlacedElement(
    val coords: Coords,
    val definition: ElementDefinition,
    val direction: Direction
)
