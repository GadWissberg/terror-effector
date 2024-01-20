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
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Plane
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.Ray
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Disposable
import com.gadarts.te.GeneralUtils
import com.gadarts.te.common.CameraUtils


class SceneRenderer : Table(), Disposable {
    private lateinit var eastPointerModelInstance: ModelInstance
    private lateinit var northPointerModelInstance: ModelInstance
    private lateinit var eastPointerModel: Model
    private lateinit var northPointerModel: Model
    private var cameraController: CameraController
    private lateinit var gridModel: Model
    private var camera: OrthographicCamera = CameraUtils.createCamera(1280, 960)
    private var axisModel: Model
    private val batch = ModelBatch()
    private val modelInstances = mutableListOf<ModelInstance>()

    init {
        val inputMultiplexer = Gdx.input.inputProcessor as InputMultiplexer
        val modelBuilder = ModelBuilder()
        axisModel = modelBuilder.createXYZCoordinates(
            1F, Material(ColorAttribute.createDiffuse(Color.RED)),
            ((VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong())
        )
        modelInstances.add(ModelInstance(axisModel))
        val gridModelInstance = addGrid(modelBuilder)
        modelInstances.add(gridModelInstance)
        cameraController = CameraController(camera)
        inputMultiplexer.addProcessor(cameraController)
        addDirectionsIndicator(modelBuilder)
    }

    private fun addGrid(modelBuilder: ModelBuilder): ModelInstance {
        gridModel = modelBuilder.createLineGrid(
            MAP_SIZE,
            MAP_SIZE,
            1F,
            1F,
            Material(ColorAttribute.createDiffuse(Color.GRAY)),
            ((VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong())
        )
        val gridModelInstance = ModelInstance(gridModel)
        gridModelInstance.transform.translate(MAP_SIZE.toFloat() / 2F, 0F, MAP_SIZE.toFloat() / 2F)
        return gridModelInstance
    }

    private fun addDirectionsIndicator(modelBuilder: ModelBuilder) {
        createDirectionsIndicatorModels(modelBuilder)
        northPointerModelInstance = ModelInstance(northPointerModel)
        northPointerModelInstance.transform.scale(
            DIRECTIONS_INDICATOR_ARROW_SCALE,
            DIRECTIONS_INDICATOR_ARROW_SCALE,
            0.5F
        )
        eastPointerModelInstance = ModelInstance(eastPointerModel)
        eastPointerModelInstance.transform.scale(
            0.5F,
            DIRECTIONS_INDICATOR_ARROW_SCALE,
            DIRECTIONS_INDICATOR_ARROW_SCALE
        )
        modelInstances.add(northPointerModelInstance)
        modelInstances.add(eastPointerModelInstance)
    }

    private fun createDirectionsIndicatorModels(modelBuilder: ModelBuilder) {
        northPointerModel = modelBuilder.createArrow(
            Vector3(),
            Vector3(0F, 0F, -DIRECTIONS_INDICATOR_ARROW_SIZE),
            Material(ColorAttribute.createDiffuse(Color.RED)),
            (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong()
        )
        eastPointerModel = modelBuilder.createArrow(
            Vector3(),
            Vector3(DIRECTIONS_INDICATOR_ARROW_SIZE, 0F, 0F),
            Material(ColorAttribute.createDiffuse(Color.GREEN)),
            (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong()
        )
    }

    fun render() {
        Gdx.gl.glViewport(
            0,
            0,
            width.toInt(),
            height.toInt()
        )
        Intersector.intersectRayPlane(
            auxRay.set(camera.unproject(auxVector1.set(50F, 50F, 0F)), camera.direction),
            groundPlane, auxVector2
        )
        northPointerModelInstance.transform.setTranslation(auxVector2)
        eastPointerModelInstance.transform.setTranslation(auxVector2)
        cameraController.update()
        camera.update()
        batch.begin(camera)
        modelInstances.forEach {
            batch.render(it)
        }
        batch.end()
    }

    override fun dispose() {
        GeneralUtils.disposeObject(this, SceneRenderer::class)
    }


    companion object {
        val groundPlane = Plane(Vector3.Y, 0F)
        val auxRay = Ray()
        val auxVector1 = Vector3()
        val auxVector2 = Vector3()
        const val DIRECTIONS_INDICATOR_ARROW_SCALE = 2.5F
        const val DIRECTIONS_INDICATOR_ARROW_SIZE = 1F
        const val MAP_SIZE: Int = 32
    }
}
