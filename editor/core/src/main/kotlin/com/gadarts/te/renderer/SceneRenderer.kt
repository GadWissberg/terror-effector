package com.gadarts.te.renderer

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Disposable
import com.gadarts.te.EditorEvents
import com.gadarts.te.Modes
import com.gadarts.te.TerrorEffectorEditor
import com.gadarts.te.common.assets.GameAssetsManager
import com.gadarts.te.common.utils.CameraUtils
import com.gadarts.te.common.utils.GeneralUtils
import com.gadarts.te.common.utils.LightUtils
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
    private val auxiliaryModelInstances = AuxiliaryModelInstances()
    private val camera: OrthographicCamera = CameraUtils.createCamera(1280, 960)
    private val batches = Batches(editorAssetManager, camera)
    private val mapData = MapData(MAP_SIZE, gameAssetsManager)

    init {
        dispatcher.addListener(this, EditorEvents.CLICKED_BUTTON_MODE.ordinal)
        environment = LightUtils.createEnvironment()
    }


    fun render() {
        val screenPosition = localToScreenCoordinates(auxVector2_1.set(0F, 0F))
        Gdx.gl.glViewport(
            screenPosition.x.toInt(),
            stage.height.toInt() - screenPosition.y.toInt(),
            width.toInt(),
            height.toInt()
        )
        auxiliaryModelInstances.update(camera)
        Handlers.entries.forEach { it.handlerInstance.onUpdate() }
        camera.update()
        renderModels()
    }

    private fun renderModels() {
        batches.modelsBatch.begin(camera)
        auxiliaryModelInstances.render(batches.modelsBatch)
        mapData.onModelsRender(batches.modelsBatch, environment)
        Handlers.entries.forEach { it.handlerInstance.onModelsRender(batches.modelsBatch) }
        batches.modelsBatch.end()
        Gdx.gl.glDepthMask(false)
        mapData.onDecalsRender(batches.decalsBatch, handlersData.camera)
        Handlers.entries.forEach { it.handlerInstance.onDecalsRender(batches.decalsBatch) }
        batches.decalsBatch.flush()
        Gdx.gl.glDepthMask(true)
    }


    override fun dispose() {
        GeneralUtils.disposeObject(this, SceneRenderer::class.java)
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
        private val auxVector2_1 = Vector2()
        const val MAP_SIZE: Int = 32
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
