package com.gadarts.te.renderer.handlers.cursor.types

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g3d.decals.Decal
import com.gadarts.te.common.assets.definitions.character.CharacterDefinition
import com.gadarts.te.common.utils.CharacterUtils

class DecalCursor {
    var characterDeclaration: CharacterDefinition? = null
        private set
    var decal: Decal? = null
        private set

    fun newDecal(region: TextureAtlas.AtlasRegion, characterDeclaration: CharacterDefinition) {
        decal = CharacterUtils.createCharacterDecal(region)
        this.characterDeclaration = characterDeclaration
    }

    fun updateAlpha(alpha: Float) {
        if (decal == null) return
        decal!!.setColor(
            decal!!.color.r,
            decal!!.color.r,
            decal!!.color.r,
            alpha
        )
    }

}
