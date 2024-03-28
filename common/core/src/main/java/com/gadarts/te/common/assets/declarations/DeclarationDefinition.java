package com.gadarts.te.common.assets.declarations;

import com.gadarts.te.common.assets.AssetDefinition;

import static com.gadarts.te.common.assets.GameAssetsManager.PATH_SEPARATOR;

public interface DeclarationDefinition extends AssetDefinition {
    String DATA_FOLDER = "declarations";
    String FORMAT = "json";

    default String getFilePath( ) {
        String sub = getSubFolderName();
        String path = DATA_FOLDER + (sub != null ? PATH_SEPARATOR + sub : "") + PATH_SEPARATOR;
        return path + getName().toLowerCase() + "." + FORMAT;
    }

    String getSubFolderName( );

    @Override
    default Class<Declaration> getTypeClass( ) {
        return Declaration.class;
    }

    String getName( );
}
