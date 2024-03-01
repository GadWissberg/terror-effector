package com.gadarts.te.assets

enum class IconsTextures {
    BUTTON_UP,
    BUTTON_DOWN,
    BUTTON_OVER,
    BUTTON_CHECKED,
    BUTTON_GALLERY_UP,
    BUTTON_GALLERY_DOWN,
    BUTTON_GALLERY_OVER,
    BUTTON_GALLERY_CHECKED,
    ICON_MODE_FLOOR,
    ICON_MODE_WALLS,
    ICON_MODE_ENV_OBJECTS,
    ICON_ROTATE_CLOCKWISE,
    ICON_ROTATE_COUNTER_CLOCKWISE,
    ICON_FILE_SAVE,
    ICON_FILE_LOAD,
    TREE_ICON_ENV,
    TREE_ICON_WALL;

    fun getFileName(): String {
        return "textures/$name.png"
    }


}
