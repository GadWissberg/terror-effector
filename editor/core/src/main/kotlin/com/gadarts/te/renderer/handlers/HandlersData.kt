package com.gadarts.te.renderer.handlers

import com.badlogic.gdx.graphics.OrthographicCamera
import com.gadarts.te.renderer.model.MapData

class HandlersData(
    val camera: OrthographicCamera,
    val screenX: Float,
    val screenY: Float,
    val heightUnderBars: Float,
    val mapData: MapData
)
