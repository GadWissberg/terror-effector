package com.gadarts.te.renderer.model

import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.gadarts.te.common.assets.declarations.EnvObjectDeclaration
import com.gadarts.te.common.map.Coords
import com.gadarts.te.common.map.element.Direction

class PlacedEnvObject(
    coords: Coords,
    declaration: EnvObjectDeclaration,
    direction: Direction,
    val modelInstance: ModelInstance
) : PlacedElement(coords, declaration, direction)
