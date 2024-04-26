package com.gadarts.te.common.assets.model;

import com.gadarts.te.common.assets.AssetDeclaration;

public interface ModelDeclaration extends AssetDeclaration {
    String FOLDER = "models";
    String FORMAT = "g3dj";

    String getTextureFileName( );
}
