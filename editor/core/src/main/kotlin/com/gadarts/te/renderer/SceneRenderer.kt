package com.gadarts.te.renderer

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
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
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.Ray
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Disposable
import com.gadarts.te.GeneralUtils
import com.gadarts.te.Modes
import com.gadarts.te.UiEvents
import com.gadarts.te.common.CameraUtils


class SceneRenderer(dispatcher: MessageDispatcher) : Table(), Disposable, Telegraph {
    private lateinit var mode: Modes
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
        dispatcher.addListener(this, UiEvents.MODE_SELECTED.ordinal)
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

    override fun handleMessage(msg: Telegram?): Boolean {
        var result = false
        if (msg!!.message == UiEvents.MODE_SELECTED.ordinal) {
            mode = (msg.extraInfo as Modes)
            result = true
        }
        return result
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
        val screenPosition = localToScreenCoordinates(auxVector2_1.set(0F, 0F))
        Gdx.gl.glViewport(
            screenPosition.x.toInt(),
            stage.height.toInt() - screenPosition.y.toInt(),
            width.toInt(),
            height.toInt()
        )
        Intersector.intersectRayPlane(
            auxRay.set(camera.unproject(auxVector3_1.set(50F, 50F, 0F)), camera.direction),
            groundPlane, auxVector3_2
        )
        northPointerModelInstance.transform.setTranslation(auxVector3_2)
        eastPointerModelInstance.transform.setTranslation(auxVector3_2)
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
        private val groundPlane = Plane(Vector3.Y, 0F)
        private val auxRay = Ray()
        private val auxVector2_1 = Vector2()
        private val auxVector3_1 = Vector3()
        private val auxVector3_2 = Vector3()
        private const val DIRECTIONS_INDICATOR_ARROW_SCALE = 2.5F
        private const val DIRECTIONS_INDICATOR_ARROW_SIZE = 1F
        private const val MAP_SIZE: Int = 32
    }

}
