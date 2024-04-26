package com.gadarts.te.common.assets.texture;

import com.badlogic.gdx.graphics.Texture;
import com.gadarts.te.common.assets.AssetDeclaration;

import static com.gadarts.te.common.assets.GameAssetsManager.PATH_SEPARATOR;

public interface TextureDeclaration extends AssetDeclaration {
    String TEXTURES_FOLDER = "textures";
    String TEXTURE_FORMAT = "png";

    default String getFilePath( ) {
        String path = TEXTURES_FOLDER + PATH_SEPARATOR + getSubFolderName() + PATH_SEPARATOR;
        return path + getName().toLowerCase() + "." + TEXTURE_FORMAT;
    }

    String getSubFolderName( );

    @Override
    default Class<Texture> getTypeClass( ) {
        return Texture.class;
    }

    String getName( );
}
