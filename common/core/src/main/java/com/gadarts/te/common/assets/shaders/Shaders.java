package com.gadarts.te.common.assets.shaders;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.graphics.g3d.Model;
import lombok.Getter;

import static com.gadarts.te.common.assets.GameAssetsManager.PATH_SEPARATOR;

@Getter
public enum Shaders implements ShaderDefinition {
    DECAL_VERTEX,
    DECAL_FRAGMENT;

    private final String filePath;

    Shaders( ) {
        this.filePath = FOLDER + PATH_SEPARATOR + name().toLowerCase() + "." + FORMAT;
    }


    @Override
    public AssetLoaderParameters<String> getParameters( ) {
        return null;
    }

    @Override
    public Class<String> getTypeClass( ) {
        return String.class;
    }

}
