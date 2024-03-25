package com.gadarts.te.common.assets.texture;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.graphics.Texture;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UiTextures implements TextureDefinition {
    NODE_CURSOR;

    private final Texture.TextureWrap textureWrap;

    UiTextures( ) {
        this(Texture.TextureWrap.Repeat);
    }

    @Override
    public String getSubFolderName( ) {
        return "interface";
    }

    @Override
    public String getName( ) {
        return name();
    }

    @Override
    public AssetLoaderParameters<Texture> getParameters( ) {
        return null;
    }
}
