package com.gadarts.te.renderer

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Disposable
import com.gadarts.te.common.CameraUtils


class SceneRenderer : Table(), Disposable {
    private var axisModelInstance: ModelInstance
    private var axisModel: Model
    private val batch = ModelBatch()
    private var camera: OrthographicCamera = OrthographicCamera()

    init {
        camera.setToOrtho(false, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        val modelBuilder = ModelBuilder()
        axisModel = modelBuilder.createXYZCoordinates(
            1F, Material(ColorAttribute.createDiffuse(Color.RED)),
            ((VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong())
        )
        CameraUtils.positionCamera(camera)
        axisModelInstance = ModelInstance(axisModel)
    }

    fun render() {
        Gdx.gl.glViewport(
            0,
            0,
            100,
            100
        )
        batch.begin(camera)
        batch.render(axisModelInstance)
        batch.end()
    }

    override fun dispose() {
        axisModel.dispose()
    }
}
