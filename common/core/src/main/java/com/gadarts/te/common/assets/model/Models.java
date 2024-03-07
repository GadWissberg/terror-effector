package com.gadarts.te.common.assets.model;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.graphics.g3d.Model;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.gadarts.te.common.assets.GameAssetsManager.PATH_SEPARATOR;

@Getter
public enum Models implements ModelDefinition {
    WALL_WITH_RAILING,
    WALL_WITH_RAILING_HALF(new ModelOffset(1F, 0F, 0.5F));

    private final String filePath;
    private final float alpha;
    private final String textureFileName;
    private final ModelOffset modelOffset;

    Models( ) {
        this(new ModelOffset(0.5F, 0F, 0.5F));
    }

    Models(ModelOffset offset) {
        this(1.0F, null, null, offset);
    }

    Models(final float alpha, String fileName, String textureFileName, ModelOffset offset) {
        String name = fileName != null ? fileName : name().toLowerCase();
        this.filePath = FOLDER + PATH_SEPARATOR + name + "." + FORMAT;
        this.textureFileName = textureFileName;
        this.alpha = alpha;
        this.modelOffset = offset;
    }


    @Override
    public AssetLoaderParameters<Model> getParameters( ) {
        return null;
    }

    @Override
    public Class<Model> getTypeClass( ) {
        return Model.class;
    }

    @RequiredArgsConstructor
    @Getter
    public static class ModelOffset {
        private final float x;
        private final float y;
        private final float z;
    }
}
