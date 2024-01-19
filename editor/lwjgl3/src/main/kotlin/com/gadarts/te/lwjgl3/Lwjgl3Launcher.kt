@file:JvmName("Lwjgl3Launcher")

package com.gadarts.te.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.gadarts.te.TerrorEffectorEditor
import com.kotcrab.vis.ui.widget.VisTable



fun main() {
    if (StartupHelper.startNewJvmIfRequired())
      return
    Lwjgl3Application(TerrorEffectorEditor(), Lwjgl3ApplicationConfiguration().apply {
        setTitle("terror-effector-editor")
        setWindowedMode(640, 480)
        setWindowIcon(*(arrayOf(128, 64, 32, 16).map { "libgdx$it.png" }.toTypedArray()))
    })
}
