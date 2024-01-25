package com.gadarts.te.renderer.handlers.actions

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.gadarts.te.common.assets.texture.SurfaceTextures
import com.gadarts.te.renderer.model.MapData

class PlaceFloorTilesAction(
    private val position: Vector2,
    private val mapData: MapData,
    private val selectedTexture: Texture,
    private val textureDefinition: SurfaceTextures
) : Action {

    override fun takeStep() {
        mapData.setTile(position.x.toInt(), position.y.toInt(), selectedTexture, textureDefinition)
    }

}
