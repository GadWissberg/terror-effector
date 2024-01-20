package com.gadarts.te

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.gadarts.te.common.assets.GameAssetsManager
import com.gadarts.te.renderer.SceneRenderer
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.widget.*

class TerrorEffectorEditor : ApplicationAdapter() {
    private lateinit var sceneRenderer: SceneRenderer
    private lateinit var stage: Stage

    override fun create() {
        super.create()
        VisUI.load()
        val gameAssetsManager = GameAssetsManager("../game/assets/")
        gameAssetsManager.loadGameFiles()
        val editorAssetsManager = AssetManager()
        IconsTextures.entries.forEach {
            editorAssetsManager.load(
                it.getFileName(),
                Texture::class.java
            )
        }
        editorAssetsManager.finishLoading()
        stage = Stage(ScreenViewport())
        stage.isDebugAll = DebugSettings.SHOW_BORDERS
        val root = VisTable(true)
        root.setFillParent(true)
        val menuBar = addMenuBar(root)
        menuBar.table.pack()
        Gdx.input.inputProcessor = InputMultiplexer(stage)
        sceneRenderer = SceneRenderer()
        root.add(sceneRenderer).size(1280F, 960F - menuBar.table.height)
        stage.addActor(root)
    }

    private fun addMenuBar(root: VisTable): MenuBar {
        val menuBar = MenuBar()
        addMenus(menuBar)
        root.add(menuBar.table).fillX().expandX().row()
        return menuBar
    }

    private fun addMenus(menuBar: MenuBar) {
        val fileMenu = Menu("File")
        val editMenu = Menu("Edit")
        val windowMenu = Menu("Window")
        val helpMenu = Menu("Help")
        fileMenu.addItem(MenuItem("menuitem #1"))
        fileMenu.addItem(MenuItem("menuitem #2").setShortcut("f1"))
        fileMenu.addItem(MenuItem("menuitem #3").setShortcut("f2"))
        fileMenu.addItem(MenuItem("menuitem #4").setShortcut("alt + f4"))

        val subMenuItem = MenuItem("submenu #1")
        subMenuItem.setShortcut("alt + insert")
        subMenuItem.subMenu = createSubMenu()
        fileMenu.addItem(subMenuItem)

        val subMenuItem2 = MenuItem("submenu #2")
        subMenuItem2.subMenu = createSubMenu()
        fileMenu.addItem(subMenuItem2)

        val subMenuItem3 = MenuItem("submenu disabled")
        subMenuItem3.isDisabled = true
        subMenuItem3.subMenu = createSubMenu()
        fileMenu.addItem(subMenuItem3)
        menuBar.addMenu(fileMenu)
        menuBar.addMenu(editMenu)
        menuBar.addMenu(windowMenu)
        menuBar.addMenu(helpMenu)
    }

    private fun createSubMenu(): PopupMenu {
        val menu = PopupMenu()
        menu.addItem(MenuItem("submenuitem #1"))
        menu.addItem(MenuItem("submenuitem #2"))
        menu.addSeparator()
        menu.addItem(MenuItem("submenuitem #3"))
        menu.addItem(MenuItem("submenuitem #4"))
        return menu
    }

    override fun render() {
        super.render()
        Gdx.gl.glViewport(
            0,
            0,
            Gdx.graphics.width,
            Gdx.graphics.height
        )
        ScreenUtils.clear(Color.BLACK, true)
        stage.act()
        stage.draw()
        sceneRenderer.render()
    }

    override fun dispose() {
        GeneralUtils.disposeObject(this, TerrorEffectorEditor::class)
        VisUI.dispose()
    }
}
