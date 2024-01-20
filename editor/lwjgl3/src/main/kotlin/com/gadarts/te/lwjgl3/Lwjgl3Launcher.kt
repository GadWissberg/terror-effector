@file:JvmName("Lwjgl3Launcher")

package com.gadarts.te.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.gadarts.te.TerrorEffectorEditor


fun main() {
    if (StartupHelper.startNewJvmIfRequired())
        return
    Lwjgl3Application(TerrorEffectorEditor(), Lwjgl3ApplicationConfiguration().apply {
        setTitle("terror-effector-editor")
        setWindowedMode(1280, 960)
        setWindowIcon(*(arrayOf(128, 64, 32, 16).map { "libgdx$it.png" }.toTypedArray()))
    })
}
