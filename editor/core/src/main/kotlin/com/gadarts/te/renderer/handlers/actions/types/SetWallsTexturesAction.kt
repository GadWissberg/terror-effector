package com.gadarts.te.renderer.handlers.actions.types

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.gadarts.te.common.assets.texture.SurfaceTextures
import com.gadarts.te.common.map.Wall

class SetWallsTexturesAction(
    private val selected: List<Wall>,
    private val texture: Texture,
    private val selectedTexture: SurfaceTextures?,
) : SingleStepAction {
    override fun begin() {
        selected.forEach {
            (it.modelInstance.materials.get(0)
                .get(TextureAttribute.Diffuse) as TextureAttribute).textureDescription.texture = texture
            it.definition = selectedTexture
        }
    }

}
