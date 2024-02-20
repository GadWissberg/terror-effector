package com.gadarts.te.assets

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Array

class ShaderLoader(resolver: FileHandleResolver) :
    AsynchronousAssetLoader<String, ShaderLoader.ShaderParameters>(resolver) {
    override fun getDependencies(
        fileName: String?,
        file: FileHandle?,
        parameter: ShaderParameters?
    ): Array<AssetDescriptor<Any>>? {
        return null
    }

    override fun loadAsync(manager: AssetManager?, fileName: String?, file: FileHandle?, parameter: ShaderParameters?) {
    }

    override fun loadSync(
        manager: AssetManager?,
        fileName: String?,
        file: FileHandle,
        parameter: ShaderParameters?
    ): String {
        return file.readString()
    }

    class ShaderParameters : AssetLoaderParameters<String?>()
}
