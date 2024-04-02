package com.gadarts.te.renderer.handlers.actions.types.delete

import com.gadarts.te.renderer.handlers.actions.types.SingleStepAction
import com.gadarts.te.renderer.model.MapData
import com.gadarts.te.renderer.model.PlacedCharacter

class DeleteCharacterAction(private val toDelete: PlacedCharacter) : SingleStepAction {
    override fun begin(mapData: MapData) {
        mapData.deleteCharacter(toDelete)
    }

}
