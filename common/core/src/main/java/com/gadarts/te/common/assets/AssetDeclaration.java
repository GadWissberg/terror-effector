package com.gadarts.te.common.assets;

import com.badlogic.gdx.assets.AssetLoaderParameters;

public interface AssetDeclaration {
    String getFilePath( );

    default String[] getFilesList( ) {
        return new String[0];
    }

    @SuppressWarnings("rawtypes")
    AssetLoaderParameters getParameters( );

    Class<?> getTypeClass( );
}
