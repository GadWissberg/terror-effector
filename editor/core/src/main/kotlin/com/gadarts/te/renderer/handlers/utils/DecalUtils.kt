package com.gadarts.te.renderer.handlers.utils

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g3d.decals.Decal
import com.badlogic.gdx.math.Vector3
import com.gadarts.te.TerrorEffectorEditor
import com.gadarts.te.common.assets.GameAssetsManager
import com.gadarts.te.common.assets.definitions.character.player.PlayerDefinition
import com.gadarts.te.common.map.element.Direction
import com.gadarts.te.common.utils.CharacterUtils
import java.util.*

object DecalUtils {
    private val auxVector = Vector3()
    fun applyFrameSeenFromCameraForCharacterDecal(
        decal: Decal,
        camera: Camera,
        gameAssetsManager: GameAssetsManager,
    ) {
        val dirSeenFromCamera: Direction =
            CharacterUtils.calculateDirectionSeenFromCamera(camera, Direction.SOUTH)
        val name: String =
            java.lang.String.format(TerrorEffectorEditor.FRAMES_KEY_CHARACTER, PlayerDefinition.getInstance().name())
        val hashMap: EnumMap<Direction, TextureAtlas.AtlasRegion> = gameAssetsManager.get(name)
        val textureRegion = hashMap[dirSeenFromCamera]
        if (textureRegion !== decal.textureRegion) {
            decal.textureRegion = textureRegion
        }
        decal.lookAt(
            auxVector.set(decal.position).sub(camera.direction),
            camera.up
        )
    }

}
