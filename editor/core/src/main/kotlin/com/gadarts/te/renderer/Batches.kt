package com.gadarts.te.renderer

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch

class Batches(editorAssetManager: AssetManager, camera: OrthographicCamera) {
    private val modelsShaderProvider: ModelsShaderProvider = ModelsShaderProvider(editorAssetManager)

    val modelsBatch = ModelBatch(modelsShaderProvider)
    val decalsBatch = DecalBatch(200, CameraGroupStrategy(camera))


}
