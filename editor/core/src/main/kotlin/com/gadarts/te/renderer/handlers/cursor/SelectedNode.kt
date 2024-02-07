package com.gadarts.te.renderer.handlers.cursor

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.gadarts.te.common.map.Coords

class SelectedNode(val coords: Coords, val modelInstance: ModelInstance) {
    init {
        val material = modelInstance.materials.get(0)
        material.set(ColorAttribute.createDiffuse(Color.SKY))
        material.set(BlendingAttribute(0.5F))
        modelInstance.transform.setTranslation(coords.x + 0.5F, 0.01F, coords.z + 0.5F)
    }
}
