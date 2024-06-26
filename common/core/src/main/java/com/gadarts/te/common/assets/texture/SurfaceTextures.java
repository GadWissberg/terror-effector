package com.gadarts.te.common.assets.texture;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.graphics.Texture;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SurfaceTextures implements TextureDeclaration {
    MISSING,
    BOARD_WALL_0,
    MARBLE_FLOOR_0,
    INDUSTRIAL_FLOOR_0,
    BLANK;

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
