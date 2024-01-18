package com.gadarts.te.common.assets;

import com.gadarts.te.common.assets.model.Models;
import com.gadarts.te.common.assets.texture.TexturesTypes;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AssetsTypes {
    TEXTURE(TexturesTypes.getAllDefinitionsInSingleArray()),
    MODEL(Models.values());

    private final AssetDefinition[] assetDefinitions;
    private final boolean manualLoad;
    private final boolean block;

    AssetsTypes(final AssetDefinition[] assetDefinitions) {
        this(assetDefinitions, false, false);
    }


}
