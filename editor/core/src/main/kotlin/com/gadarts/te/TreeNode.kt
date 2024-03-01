package com.gadarts.te

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Tree

class TreeNode(text: String, icon: Texture) : Tree.Node<TreeNode, String, TreeNodeDisplay>(TreeNodeDisplay(text, icon))
