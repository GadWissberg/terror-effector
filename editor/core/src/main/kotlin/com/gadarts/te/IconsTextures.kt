package com.gadarts.te

enum class IconsTextures {
    ICON_ROTATE_CLOCKWISE,
    ICON_ROTATE_COUNTER_CLOCKWISE;

    fun getFileName(): String {
        return "$name.png"
    }


}
