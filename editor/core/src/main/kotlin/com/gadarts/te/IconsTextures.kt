package com.gadarts.te

enum class IconsTextures {
    BUTTON_UP,
    BUTTON_DOWN,
    BUTTON_OVER,
    BUTTON_CHECKED,
    ICON_MODE_FLOOR,
    ICON_MODE_WALLS,
    ICON_ROTATE_CLOCKWISE,
    ICON_ROTATE_COUNTER_CLOCKWISE;

    fun getFileName(): String {
        return "$name.png"
    }


}
