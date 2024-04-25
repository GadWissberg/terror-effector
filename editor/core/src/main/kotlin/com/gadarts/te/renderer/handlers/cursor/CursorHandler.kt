package com.gadarts.te.renderer.handlers.cursor

import com.badlogic.gdx.math.Vector3
import com.gadarts.te.common.assets.declarations.CharacterDeclaration
import com.gadarts.te.common.definitions.env.EnvObjectDefinition
import com.gadarts.te.common.map.Wall
import com.gadarts.te.renderer.handlers.cursor.types.DecalCursor
import com.gadarts.te.renderer.handlers.cursor.types.ObjectModelCursor

interface CursorHandler {
    fun setCursorToFloorModel()
    fun displayObjectOfTreeNode(envObjectDefinition: EnvObjectDefinition)
    fun fetchGridCellAtMouse(): Vector3
    fun clearSelection()
    fun displayCharacterCursor(characterDeclaration: CharacterDeclaration)
    fun turnOnSelectingCursor()

    val selectedNodes: List<SelectedNode>
    val selectedWalls: List<Wall>
    var objectModelCursor: ObjectModelCursor?
    var highlightWall: Wall?
    var decalCursor: DecalCursor
}
