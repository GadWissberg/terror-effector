package com.gadarts.te.renderer

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
import com.badlogic.gdx.utils.Disposable
import com.gadarts.te.common.utils.GeneralUtils

class AuxiliaryModelInstances : Disposable {
    private var gridModelInstance: ModelInstance
    private var axisModelInstance: ModelInstance
    private lateinit var eastPointerModelInstance: ModelInstance
    private lateinit var northPointerModelInstance: ModelInstance
    private lateinit var eastPointerModel: Model
    private lateinit var northPointerModel: Model
    private lateinit var gridModel: Model
    private val axisModel: Model

    init {
        val modelBuilder = ModelBuilder()
        addDirectionsIndicator(modelBuilder)
        axisModel = modelBuilder.createXYZCoordinates(
            1F, Material(ColorAttribute.createDiffuse(Color.RED)),
            ((VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong())
        )
        axisModelInstance = ModelInstance(axisModel)
        gridModelInstance = addGrid(modelBuilder)
    }

    private fun addGrid(modelBuilder: ModelBuilder): ModelInstance {
        gridModel = modelBuilder.createLineGrid(
            SceneRenderer.MAP_SIZE,
            SceneRenderer.MAP_SIZE,
            1F,
            1F,
            Material(ColorAttribute.createDiffuse(Color.GRAY)),
            ((VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong())
        )
        val gridModelInstance = ModelInstance(gridModel)
        gridModelInstance.transform.translate(
            SceneRenderer.MAP_SIZE.toFloat() / 2F,
            0F,
            SceneRenderer.MAP_SIZE.toFloat() / 2F
        )
        return gridModelInstance
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

    fun render(batch: ModelBatch) {
        batch.render(northPointerModelInstance)
        batch.render(eastPointerModelInstance)
        batch.render(axisModelInstance)
        batch.render(gridModelInstance)
    }


    fun update(camera: OrthographicCamera) {
        Intersector.intersectRayPlane(
            auxRay.set(camera.unproject(auxVector3_1.set(50F, 50F, 0F)), camera.direction),
            groundPlane, auxVector3_2
        )
        northPointerModelInstance.transform.setTranslation(auxVector3_2)
        eastPointerModelInstance.transform.setTranslation(auxVector3_2)
    }

    override fun dispose() {
        GeneralUtils.disposeObject(this, AuxiliaryModelInstances::class.java)
    }

    companion object {
        private const val DIRECTIONS_INDICATOR_ARROW_SCALE = 2.5F
        private const val DIRECTIONS_INDICATOR_ARROW_SIZE = 1F
        private val groundPlane = Plane(Vector3.Y, 0F)
        private val auxRay = Ray()
        private val auxVector3_1 = Vector3()
        private val auxVector3_2 = Vector3()
    }
}
