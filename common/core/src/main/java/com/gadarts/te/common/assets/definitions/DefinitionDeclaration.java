package com.gadarts.te.common.assets.definitions;

import com.gadarts.te.common.assets.AssetDeclaration;

import static com.gadarts.te.common.assets.GameAssetsManager.PATH_SEPARATOR;

public interface DefinitionDeclaration extends AssetDeclaration {
    String DATA_FOLDER = "definitions";
    String FORMAT = "json";

    default String getFilePath( ) {
        String sub = getSubFolderName();
        String path = DATA_FOLDER + (sub != null ? PATH_SEPARATOR + sub : "") + PATH_SEPARATOR;
        return path + getName().toLowerCase() + "." + FORMAT;
    }

    String getSubFolderName( );

    @Override
    default Class<Definition> getTypeClass( ) {
        return Definition.class;
    }

    String getName( );
}
