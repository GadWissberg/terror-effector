package com.gadarts.te

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.kotcrab.vis.ui.VisUI

class TreeNodeDisplay(text: String, icon: Texture) : Table() {
    init {
        add(Image(TextureRegionDrawable(icon)))
        add(TextButton(text, VisUI.getSkin()))
    }
}
