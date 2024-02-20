package com.gadarts.te.renderer

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.Shader
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider
import com.gadarts.te.assets.Shaders

class ModelsShaderProvider(assetsManager: AssetManager) : DefaultShaderProvider() {
    private var shaderConfig: DefaultShader.Config = DefaultShader.Config()

    init {
        shaderConfig.vertexShader = assetsManager.get(Shaders.VERTEX.getFileName())
        shaderConfig.fragmentShader = assetsManager.get(Shaders.FRAGMENT.getFileName())
    }

    override fun createShader(renderable: Renderable): Shader {
        return ModelsShader(renderable)
    }

}
