package com.gadarts.te.renderer

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
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


class SceneRenderer() : Table(), Disposable {
    private var cameraController: CameraController
    private var gridModelInstance: ModelInstance
    private var gridModel: Model
    private var camera: OrthographicCamera = CameraUtils.createCamera(1280, 960)
    private var axisModelInstance: ModelInstance
    private var axisModel: Model
    private val batch = ModelBatch()

    init {
        val inputMultiplexer = Gdx.input.inputProcessor as InputMultiplexer
        val modelBuilder = ModelBuilder()
        axisModel = modelBuilder.createXYZCoordinates(
            1F, Material(ColorAttribute.createDiffuse(Color.RED)),
            ((VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong())
        )
        axisModelInstance = ModelInstance(axisModel)
        gridModel = modelBuilder.createLineGrid(
            MAP_SIZE,
            MAP_SIZE,
            1F,
            1F,
            Material(ColorAttribute.createDiffuse(Color.GRAY)),
            ((VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong())
        )
        gridModelInstance = ModelInstance(gridModel)
        gridModelInstance.transform.translate(MAP_SIZE.toFloat() / 2F, 0F, MAP_SIZE.toFloat() / 2F)
        cameraController = CameraController(camera)
        inputMultiplexer.addProcessor(cameraController)
    }

    fun render() {
        cameraController.update()
        camera.update()
        Gdx.gl.glViewport(
            0,
            0,
            Gdx.graphics.width,
            Gdx.graphics.height
        )
        batch.begin(camera)
        batch.render(axisModelInstance)
        batch.render(gridModelInstance)
        batch.end()
    }

    override fun dispose() {
        axisModel.dispose()
        gridModel.dispose()
    }

    companion object {
        const val MAP_SIZE: Int = 32
    }
}
