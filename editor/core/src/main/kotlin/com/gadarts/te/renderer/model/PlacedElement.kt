package com.gadarts.te.renderer.model

import com.gadarts.te.common.assets.declarations.ElementDeclaration
import com.gadarts.te.common.map.Coords
import com.gadarts.te.common.map.element.Direction

abstract class PlacedElement(
    val coords: Coords,
    val declaration: ElementDeclaration,
    val direction: Direction
)
