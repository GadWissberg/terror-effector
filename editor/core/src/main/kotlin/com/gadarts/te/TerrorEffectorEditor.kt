package com.gadarts.te

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.ai.msg.MessageDispatcher
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.gadarts.te.assets.IconsTextures
import com.gadarts.te.assets.IconsTextures.*
import com.gadarts.te.assets.ShaderLoader
import com.gadarts.te.assets.Shaders
import com.gadarts.te.common.assets.GameAssetsManager
import com.gadarts.te.common.assets.texture.SurfaceTextures
import com.gadarts.te.common.definitions.env.EnvObjectDefinition
import com.gadarts.te.common.definitions.env.Obstacles
import com.gadarts.te.common.definitions.env.WallObjects
import com.gadarts.te.common.utils.GeneralUtils
import com.gadarts.te.renderer.SceneRenderer
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.widget.*

class TerrorEffectorEditor : ApplicationAdapter() {
    private val modeToContentCatalog = mutableMapOf<Modes, WidgetGroup>()
    private lateinit var contentCatalogDisplay: Stack
    private lateinit var editorAssetManager: AssetManager
    private lateinit var sceneRenderer: SceneRenderer
    private lateinit var stage: Stage
    private val dispatcher: MessageDispatcher = MessageDispatcher()

    override fun create() {
        super.create()
        VisUI.load()
        val gameAssetsManager = GameAssetsManager("../game/assets/")
        gameAssetsManager.loadGameFiles()
        editorAssetManager = AssetManager()
        editorAssetManager.setLoader(
            String::class.java,
            ShaderLoader(editorAssetManager.fileHandleResolver)
        )
        loadEditorAssets(editorAssetManager)
        stage = Stage(ScreenViewport())
        stage.isDebugAll = DebugSettings.SHOW_BORDERS
        val root = VisTable(true)
        root.setFillParent(true)
        val menuBar = addMenuBar(root)
        val buttonsBarTable = VisTable()
        addMainButtonsBar(buttonsBarTable)
        addModeButtonsBar(buttonsBarTable)
        root.add(buttonsBarTable).fillX().expandX().row()
        Gdx.input.inputProcessor = InputMultiplexer(stage)
        stage.addActor(root)
        addSplitView(menuBar, buttonsBarTable, root, gameAssetsManager)
    }

    private fun addModeButtonsBar(buttonsBarTable: VisTable) {
        val modeButtonsBar = MenuBar()
        addButtonToButtonsBar(
            modeButtonsBar,
            ICON_ROTATE_CLOCKWISE,
            EditorEvents.CLICKED_BUTTON_ROTATE_CLOCKWISE.ordinal
        )
        addButtonToButtonsBar(
            modeButtonsBar,
            ICON_ROTATE_COUNTER_CLOCKWISE,
            EditorEvents.CLICKED_BUTTON_ROTATE_COUNTER_CLOCKWISE.ordinal
        )
        buttonsBarTable.add(modeButtonsBar.table).fillX().expandX().row()
        modeButtonsBar.table.pack()
    }

    private fun addSplitView(
        menuBar: MenuBar,
        buttonBar: VisTable,
        root: VisTable,
        gameAssetsManager: GameAssetsManager,
    ) {
        sceneRenderer = SceneRenderer(dispatcher, gameAssetsManager, editorAssetManager)
        val gallery = createGallery(gameAssetsManager)
        val galleryScrollPane = VisScrollPane(gallery)
        contentCatalogDisplay = Stack()
        val heightUnderBars = WINDOW_HEIGHT - (menuBar.table.height + buttonBar.height)
        contentCatalogDisplay.add(galleryScrollPane)
        val envObjectsTree = addEnvObjectsTree()
        contentCatalogDisplay.add(envObjectsTree)
        val charactersTree = addCharactersTree()
        contentCatalogDisplay.add(charactersTree)
        modeToContentCatalog[Modes.FLOOR] = galleryScrollPane
        modeToContentCatalog[Modes.WALLS] = galleryScrollPane
        modeToContentCatalog[Modes.ENV_OBJECTS] = envObjectsTree
        modeToContentCatalog[Modes.CHARACTERS] = charactersTree
        val splitPane = VisSplitPane(contentCatalogDisplay, sceneRenderer, false)
        splitPane.setSplitAmount(0.2F)
        root.add(splitPane).size(WINDOW_WIDTH, heightUnderBars)
        root.pack()
        sceneRenderer.init(heightUnderBars)
    }

    private fun addEnvObjectsTree(): VisTree<TreeNode, String> {
        val envObjectsTree = VisTree<TreeNode, String>()
        val treeRoot = createTreeRoot(envObjectsTree)
        addNodeToEnvObjectsTree(treeRoot, "Walls", TREE_ICON_WALL, WallObjects.entries.toTypedArray())
        addNodeToEnvObjectsTree(treeRoot, "Obstacles", TREE_ICON_OBSTACLE, Obstacles.entries.toTypedArray())
        envObjectsTree.add(treeRoot)
        envObjectsTree.isVisible = false
        return envObjectsTree
    }

    private fun addCharactersTree(): VisTree<TreeNode, String> {
        val tree = VisTree<TreeNode, String>()
        val treeRoot = createTreeRoot(tree)
        val iconTexture = editorAssetManager.get(TREE_ICON_CHARACTER.getFileName(), Texture::class.java)
        val definitionNode = TreeNode(
            "Player",
            iconTexture
        )
        definitionNode.actor.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                super.clicked(event, x, y)
                dispatcher.dispatchMessage(EditorEvents.CLICKED_TREE_NODE.ordinal)
            }
        })
        treeRoot.add(definitionNode)
        tree.add(treeRoot)
        tree.isVisible = false
        return tree
    }

    private fun addNodeToEnvObjectsTree(
        treeRoot: TreeNode, label: String, icon: IconsTextures, entries: Array<out EnvObjectDefinition>
    ) {
        val iconTexture = editorAssetManager.get(icon.getFileName(), Texture::class.java)
        val treeNode = TreeNode(label, iconTexture)
        entries.forEach {
            val definitionNode = TreeNode(
                it.displayName,
                iconTexture
            )
            definitionNode.actor.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    super.clicked(event, x, y)
                    dispatcher.dispatchMessage(EditorEvents.CLICKED_TREE_NODE.ordinal, it)
                }
            })
            treeNode.add(definitionNode)
        }
        treeRoot.add(treeNode)
    }

    private fun createTreeRoot(envObjectsTree: VisTree<TreeNode, String>): TreeNode {
        val treeRoot = TreeNode(
            "Environment Objects",
            editorAssetManager.get(TREE_ICON_ENV.getFileName(), Texture::class.java)
        )
        treeRoot.isExpanded = true
        envObjectsTree.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                super.clicked(event, x, y)
                treeRoot.isExpanded = true
            }
        })
        return treeRoot
    }

    private fun createGallery(gameAssetsManager: GameAssetsManager): Table {
        val gallery = Table()
        val buttonGroup = createButtonGroup()
        SurfaceTextures.entries.forEach {
            addGalleryButton(
                buttonGroup,
                it,
                gallery,
                gameAssetsManager
            )
            if (gallery.children.size % 2 == 0) {
                gallery.row()
            }
        }
        return gallery
    }

    private fun addMainButtonsBar(root: VisTable): MenuBar {
        val buttonBar = MenuBar()
        addButtonToButtonsBar(buttonBar, ICON_FILE_SAVE, EditorEvents.CLICKED_BUTTON_SAVE.ordinal)
        addButtonToButtonsBar(buttonBar, ICON_FILE_LOAD, EditorEvents.CLICKED_BUTTON_LOAD.ordinal)
        buttonBar.table.add(Separator("vertical")).width(10F).fillY().expandY()
        val buttonGroup = createButtonGroup()
        Modes.entries.forEach {
            addModeButton(buttonGroup, buttonBar, it, it.icon.getFileName())
        }
        root.add(buttonBar.table).fillX().expandX().row()
        buttonBar.table.pack()
        return buttonBar
    }

    private fun addButtonToButtonsBar(buttonBar: MenuBar, icon: IconsTextures, eventOrdinal: Int) {
        addButton(
            editorAssetManager.get(icon.getFileName(), Texture::class.java),
            object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    dispatcher.dispatchMessage(eventOrdinal)
                }
            }, buttonBar.table
        )
    }


    private fun addModeButton(
        buttonGroup: ButtonGroup<VisImageButton>,
        buttonBar: MenuBar,
        mode: Modes,
        icon: String
    ) {
        addBarRadioButton(
            buttonGroup,
            object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    super.clicked(event, x, y)
                    dispatcher.dispatchMessage(EditorEvents.CLICKED_BUTTON_MODE.ordinal, mode)
                    contentCatalogDisplay.children.forEach { it.isVisible = false }
                    modeToContentCatalog[mode]!!.isVisible = true
                }
            },
            editorAssetManager.get(icon, Texture::class.java), buttonBar.table
        )
    }

    private fun createButtonGroup(): ButtonGroup<VisImageButton> {
        val buttonGroup = ButtonGroup<VisImageButton>()
        buttonGroup.setMinCheckCount(1)
        buttonGroup.setMaxCheckCount(1)
        buttonGroup.setUncheckLast(true)
        return buttonGroup
    }

    private fun addGalleryButton(
        buttonGroup: ButtonGroup<VisImageButton>,
        icon: SurfaceTextures,
        table: Table,
        gameAssetsManager: GameAssetsManager,
    ) {
        val up = TextureRegionDrawable(editorAssetManager.get(BUTTON_GALLERY_UP.getFileName(), Texture::class.java))
        val style = VisImageButton.VisImageButtonStyle(
            up,
            TextureRegionDrawable(editorAssetManager.get(BUTTON_DOWN.getFileName(), Texture::class.java)),
            TextureRegionDrawable(editorAssetManager.get(BUTTON_GALLERY_CHECKED.getFileName(), Texture::class.java)),
            TextureRegionDrawable(gameAssetsManager.getTexture(icon)),
            null, null
        )
        val overTexture = editorAssetManager.get(BUTTON_GALLERY_OVER.getFileName(), Texture::class.java)
        style.over = TextureRegionDrawable(overTexture)
        val imageButton = VisImageButton(style)
        imageButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                super.clicked(event, x, y)
                dispatcher.dispatchMessage(EditorEvents.TEXTURE_SELECTED_VIA_GALLERY.ordinal, icon)
            }
        })
        imageButton.imageCell.size(48F, 48F)
        table.add(imageButton).pad(5F)
        buttonGroup.add(imageButton)
    }

    private fun addBarRadioButton(
        buttonGroup: ButtonGroup<VisImageButton>,
        clickListener: ClickListener,
        icon: Texture,
        table: Table,
    ) {
        val imageButton = addButton(icon, clickListener, table)
        buttonGroup.add(imageButton)
    }

    private fun addButton(
        icon: Texture,
        clickListener: ClickListener,
        table: Table
    ): VisImageButton {
        val up = TextureRegionDrawable(editorAssetManager.get(BUTTON_UP.getFileName(), Texture::class.java))
        val style = VisImageButton.VisImageButtonStyle(
            up,
            TextureRegionDrawable(editorAssetManager.get(BUTTON_DOWN.getFileName(), Texture::class.java)),
            TextureRegionDrawable(editorAssetManager.get(BUTTON_CHECKED.getFileName(), Texture::class.java)),
            TextureRegionDrawable(icon),
            null, null
        )
        style.over = TextureRegionDrawable(editorAssetManager.get(BUTTON_OVER.getFileName(), Texture::class.java))
        val imageButton = VisImageButton(style)
        imageButton.addListener(clickListener)
        table.add(imageButton).pad(5F)
        return imageButton
    }

    private fun loadEditorAssets(editorAssetsManager: AssetManager) {
        IconsTextures.entries.forEach {
            editorAssetsManager.load(
                it.getFileName(),
                Texture::class.java
            )
        }
        Shaders.entries.forEach {
            editorAssetsManager.load(
                it.getFileName(),
                String::class.java
            )
        }
        editorAssetsManager.finishLoading()
    }

    private fun addMenuBar(root: VisTable): MenuBar {
        val menuBar = MenuBar()
        addMenus(menuBar)
        root.add(menuBar.table).pad(20F, 0F, 0F, 0F).fillX().expandX().row()
        menuBar.table.pack()
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
        GeneralUtils.disposeObject(this, TerrorEffectorEditor::class.java)
        VisUI.dispose()
    }

    companion object {
        const val WINDOW_WIDTH = 1280F
        const val WINDOW_HEIGHT = 960F
    }
}
