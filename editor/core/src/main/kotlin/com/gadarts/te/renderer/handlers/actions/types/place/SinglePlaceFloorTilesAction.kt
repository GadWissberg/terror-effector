package com.gadarts.te.renderer.handlers.actions.types.place

import com.badlogic.gdx.graphics.Texture
import com.gadarts.te.common.assets.texture.SurfaceTextures
import com.gadarts.te.common.map.Coords
import com.gadarts.te.common.map.WallCreator
import com.gadarts.te.renderer.handlers.actions.types.SingleStepAction
import com.gadarts.te.renderer.model.MapData

class SinglePlaceFloorTilesAction(
    private val nodes: List<Coords>,
    mapData: MapData,
    selectedTexture: Texture,
    textureDefinition: SurfaceTextures,
    wallCreator: WallCreator
) : PlaceFloorTilesAction(nodes, mapData, selectedTexture, textureDefinition, wallCreator), SingleStepAction {

    override fun begin(mapData: MapData) {
        nodes.forEach {
            applyAction(it.x, it.z)
        }
    }

    override fun takeStep(extraInfo: Any) {
    }

}
