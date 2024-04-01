package com.gadarts.te.renderer.model

import com.badlogic.gdx.graphics.g3d.decals.Decal
import com.gadarts.te.common.map.Coords
import com.gadarts.te.common.map.element.Direction

class PlacedCharacter(
    val coords: Coords,
    val decal: Decal,
    val direction: Direction
)
