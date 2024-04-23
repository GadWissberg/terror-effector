package com.gadarts.te.common.assets.atlas;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Model;
import lombok.Getter;

import static com.gadarts.te.common.assets.GameAssetsManager.PATH_SEPARATOR;

@Getter
public enum Atlases implements AtlasDefinition {
    PLAYER_GENERIC,
    PLAYER_GLOCK,
    PLAYER_MELEE,
    MAINT_BOT;

    private final String filePath;

    Atlases( ) {
        this.filePath = FOLDER + PATH_SEPARATOR + name().toLowerCase() + "." + FORMAT;
    }


    @Override
    public AssetLoaderParameters<Model> getParameters( ) {
        return null;
    }

    @Override
    public Class<TextureAtlas> getTypeClass( ) {
        return TextureAtlas.class;
    }

}
