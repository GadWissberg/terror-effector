package com.gadarts.te.assets.textures;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.graphics.Texture;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SurfaceTextures implements TextureDefinition {
    INDUSTRIAL_FLOOR_0;

    private final Texture.TextureWrap textureWrap;

    SurfaceTextures( ) {
        this(Texture.TextureWrap.Repeat);
    }

    @Override
    public String getSubFolderName( ) {
        return "surfaces";
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
