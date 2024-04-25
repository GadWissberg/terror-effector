package com.gadarts.te.renderer.model

import com.badlogic.gdx.graphics.g3d.decals.Decal
import com.gadarts.te.common.assets.declarations.CharacterDeclaration
import com.gadarts.te.common.map.Coords
import com.gadarts.te.common.map.element.Direction

class PlacedCharacter(
    coords: Coords,
    declaration: CharacterDeclaration,
    direction: Direction,
    val decal: Decal
) : PlacedElement(coords, declaration, direction)
