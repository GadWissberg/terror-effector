package com.gadarts.te.assets;

import com.badlogic.gdx.assets.AssetLoaderParameters;

public interface AssetDefinition {
    String getFilePath( );

    default String[] getFilesList( ) {
        return new String[0];
    }

    AssetLoaderParameters getParameters( );

    default String getAssetManagerKey( ) {
        return null;
    }

    Class<?> getTypeClass( );
}
