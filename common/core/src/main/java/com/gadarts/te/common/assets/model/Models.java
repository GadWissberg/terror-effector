package com.gadarts.te.common.assets.model;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.graphics.g3d.Model;
import lombok.Getter;

import static com.gadarts.te.common.assets.GameAssetsManager.PATH_SEPARATOR;

@Getter
public enum Models implements ModelDefinition {
    ;
    private final String filePath;
    private final float alpha;
    private final String textureFileName;

    Models( ) {
        this(1.0f, null, null);
    }

    Models(final float alpha, String fileName, String textureFileName) {
        String name = fileName != null ? fileName : name().toLowerCase();
        this.filePath = FOLDER + PATH_SEPARATOR + name + "." + FORMAT;
        this.textureFileName = textureFileName;
        this.alpha = alpha;
    }

    Models(float alpha) {
        this(alpha, null, null);
    }

    Models(String fileName, String textureFileName) {
        this(1.0F, fileName, textureFileName);
    }

    @Override
    public AssetLoaderParameters<Model> getParameters( ) {
        return null;
    }

    @Override
    public Class<Model> getTypeClass( ) {
        return Model.class;
    }
}
