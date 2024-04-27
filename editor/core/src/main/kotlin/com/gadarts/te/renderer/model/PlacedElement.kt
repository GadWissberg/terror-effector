package com.gadarts.te.renderer.model

import com.gadarts.te.common.assets.definitions.ElementDefinition
import com.gadarts.te.common.map.Coords
import com.gadarts.te.common.map.element.Direction

abstract class PlacedElement(
    val coords: Coords,
    val elementDefinition: ElementDefinition,
    val direction: Direction
)
