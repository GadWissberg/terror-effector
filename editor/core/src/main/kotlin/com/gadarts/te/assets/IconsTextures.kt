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
    ICON_ROTATE_CLOCKWISE,
    ICON_ROTATE_COUNTER_CLOCKWISE;

    fun getFileName(): String {
        return "textures/$name.png"
    }


}
