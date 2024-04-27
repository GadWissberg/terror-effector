package com.gadarts.te.renderer.handlers.actions.types.place

import com.gadarts.te.common.assets.definitions.character.CharacterDefinition
import com.gadarts.te.common.assets.definitions.character.player.PlayerDefinition
import com.gadarts.te.common.map.Coords
import com.gadarts.te.common.map.element.Direction
import com.gadarts.te.renderer.handlers.actions.types.SingleStepAction
import com.gadarts.te.renderer.model.MapData

open class PlaceCharacterAction(
    private val coords: Coords,
    private val direction: Direction,
    private val characterDefinition: CharacterDefinition
) : SingleStepAction {

    override fun begin(mapData: MapData) {
        if (characterDefinition.id().equals(PlayerDefinition.getInstance().id())) {
            val placedCharacter =
                mapData.placedCharacters.find { placedCharacter ->
                    placedCharacter.elementDefinition.id().equals(characterDefinition.id())
                }
            if (placedCharacter != null) {
                mapData.placedCharacters.remove(placedCharacter)
            }
        }
        mapData.insertCharacter(
            coords,
            mapData.matrix[coords.z][coords.x]?.height ?: 0F,
            direction,
            characterDefinition
        )
    }


}
