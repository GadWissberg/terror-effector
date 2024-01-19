package com.gadarts.te.renderer

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.ScreenUtils

class SceneRenderer : Table() {
    fun render() {
        val localToScreenCoordinates = localToScreenCoordinates(auxVector.set(0F, 0F))
        Gdx.gl.glViewport(
            0,
            0,
            100,
            100
        )
        ScreenUtils.clear(Color.RED, true)
    }

    companion object {
        val auxVector = Vector2()
    }
}
