package com.gadarts.te.renderer.model

import com.badlogic.gdx.graphics.g3d.decals.Decal
import com.gadarts.te.common.assets.definitions.character.CharacterDefinition
import com.gadarts.te.common.map.Coords
import com.gadarts.te.common.map.element.Direction

class PlacedCharacter(
    coords: Coords,
    declaration: CharacterDefinition,
    direction: Direction,
    val decal: Decal
) : PlacedElement(coords, declaration, direction)
