package com.gadarts.te.common.assets.model;

import com.gadarts.te.common.assets.AssetDefinition;

public interface ModelDefinition extends AssetDefinition {
    String FOLDER = "models";
    String FORMAT = "g3dj";

    String getTextureFileName( );
}
