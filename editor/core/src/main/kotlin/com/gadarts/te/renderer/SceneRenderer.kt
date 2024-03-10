package com.gadarts.te.renderer

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.*
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Plane
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.Ray
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Disposable
import com.gadarts.te.EditorEvents
import com.gadarts.te.GeneralUtils
import com.gadarts.te.Modes
import com.gadarts.te.TerrorEffectorEditor
import com.gadarts.te.common.CameraUtils
import com.gadarts.te.common.LightUtils
import com.gadarts.te.common.assets.GameAssetsManager
import com.gadarts.te.renderer.handlers.Handlers
import com.gadarts.te.renderer.handlers.HandlersData
import com.gadarts.te.renderer.model.MapData


class SceneRenderer(
    private val dispatcher: MessageDispatcher,
    private val gameAssetsManager: GameAssetsManager,
    editorAssetManager: AssetManager
) :
    Table(),
    Disposable, Telegraph {
    private var environment: Environment
    private lateinit var handlersData: HandlersData
    private var gridModelInstance: ModelInstance
    private var axisModelInstance: ModelInstance
    private lateinit var eastPointerModelInstance: ModelInstance
    private lateinit var northPointerModelInstance: ModelInstance
    private lateinit var eastPointerModel: Model
    private lateinit var northPointerModel: Model
    private lateinit var gridModel: Model
    private val camera: OrthographicCamera = CameraUtils.createCamera(1280, 960)
    private val axisModel: Model
    private val modelsShaderProvider: ModelsShaderProvider = ModelsShaderProvider(editorAssetManager)
    private val batch = ModelBatch(modelsShaderProvider)
    private val mapData = MapData(MAP_SIZE, gameAssetsManager)

    init {
        dispatcher.addListener(this, EditorEvents.CLICKED_BUTTON_MODE.ordinal)
        val modelBuilder = ModelBuilder()
        axisModel = modelBuilder.createXYZCoordinates(
            1F, Material(ColorAttribute.createDiffuse(Color.RED)),
            ((VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong())
        )
        axisModelInstance = ModelInstance(axisModel)
        gridModelInstance = addGrid(modelBuilder)
        addDirectionsIndicator(modelBuilder)
        environment = LightUtils.createEnvironment()
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
        updateDirectionsIndicator()
        Handlers.entries.forEach { it.handlerInstance.onUpdate() }
        camera.update()
        renderModels()
    }

    private fun renderModels() {
        batch.begin(camera)
        mapData.render(batch, environment)
        batch.render(northPointerModelInstance)
        batch.render(eastPointerModelInstance)
        batch.render(axisModelInstance)
        batch.render(gridModelInstance)
        Handlers.entries.forEach { it.handlerInstance.onRender(batch, environment) }
        batch.end()
    }

    private fun updateDirectionsIndicator() {
        northPointerModelInstance.transform.setTranslation(auxVector3_2)
        eastPointerModelInstance.transform.setTranslation(auxVector3_2)
    }

    override fun dispose() {
        GeneralUtils.disposeObject(this, SceneRenderer::class)
    }

    fun init(heightUnderBars: Float) {
        val screenPosition = localToScreenCoordinates(auxVector2_1.set(0F, 0F))
        val screenX = screenPosition.x
        val screenY = TerrorEffectorEditor.WINDOW_HEIGHT - screenPosition.y.toInt()
        handlersData = HandlersData(camera, screenX, screenY, heightUnderBars, mapData, Modes.FLOOR)
        Handlers.entries.forEach {
            it.handlerInstance.onInitialize(
                dispatcher,
                gameAssetsManager,
                handlersData,
            )
        }
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

    override fun handleMessage(msg: Telegram): Boolean {
        var handled = false
        if (msg.message == EditorEvents.CLICKED_BUTTON_MODE.ordinal) {
            handlersData.selectedMode = msg.extraInfo as Modes
            dispatcher.dispatchMessage(EditorEvents.MODE_CHANGED.ordinal)
            handled = true
        }
        return handled
    }

}
