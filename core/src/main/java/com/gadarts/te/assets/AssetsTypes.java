package com.gadarts.te.assets;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AssetsTypes {
    TEXTURE(TexturesTypes.getAllDefinitionsInSingleArray());

    private final AssetDefinition[] assetDefinitions;
    private final boolean manualLoad;
    private final boolean block;

    AssetsTypes(final AssetDefinition[] assetDefinitions) {
        this(assetDefinitions, false, false);
    }


}
