package com.gadarts.te.assets

enum class Shaders {
    VERTEX,
    FRAGMENT;

    fun getFileName(): String {
        return "shaders/$name.glsl"
    }


}
