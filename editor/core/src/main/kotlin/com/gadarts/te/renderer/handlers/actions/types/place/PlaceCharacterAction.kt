package com.gadarts.te.renderer.handlers.actions.types.place

import com.gadarts.te.common.assets.definitions.character.player.PlayerDefinition
import com.gadarts.te.common.map.Coords
import com.gadarts.te.common.map.element.Direction
import com.gadarts.te.renderer.handlers.actions.types.SingleStepAction
import com.gadarts.te.renderer.model.MapData

open class PlaceCharacterAction(
    private val coords: Coords,
    private val direction: Direction
) : SingleStepAction {

    override fun begin(mapData: MapData) {
        val placedCharacter =
            mapData.placedCharacters.find { placedCharacter -> placedCharacter.declaration == PlayerDefinition.getInstance() }
        if (placedCharacter != null) {
            mapData.placedCharacters.clear()
        }
        mapData.insertCharacter(coords, mapData.matrix[coords.z][coords.x]?.height ?: 0F, direction)
    }


}
