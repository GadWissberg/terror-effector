package com.gadarts.te.renderer.handlers.cursor.extra

import com.gadarts.te.common.assets.definitions.character.CharacterDefinition
import com.gadarts.te.common.map.Coords
import com.gadarts.te.common.map.element.Direction

class ClickedGridCellEventForCharacterExtraInfo(
    val coords: Coords,
    val direction: Direction,
    val characterDefinition: CharacterDefinition
)
