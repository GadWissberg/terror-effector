package com.gadarts.te.common.assets.model;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.graphics.g3d.Model;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.gadarts.te.common.assets.GameAssetsManager.PATH_SEPARATOR;

@Getter
public enum Models implements ModelDefinition {
    WALL_WITH_RAILING(new ModelOffset(0.5F, 0F, 0.5F)),
    WALL_WITH_RAILING_HALF(new ModelOffset(1F, 0F, 0.5F)),
    CRATE_0_CLEAN("crate_0", "crate_0_texture_0"),
    CRATE_0_UCI("crate_0", "crate_0_texture_1"),
    CRATE_1_CLEAN(new ModelOffset(0.5F, 0.25F, 0.5F), "crate_1", "crate_1_texture_0"),
    CRATE_1_UCI(new ModelOffset(0.5F, 0.25F, 0.5F), "crate_1", "crate_1_texture_1");

    private final String filePath;
    private final ModelOffset modelOffset;
    private final String textureFileName;

    Models(ModelOffset offset) {
        this(offset, null, null);
    }

    Models(String modelFileName, String textureFileName) {
        this(new ModelOffset(0.5F, 0.5F, 0.5F), modelFileName, textureFileName);
    }

    Models(ModelOffset offset, String modelFileName, String textureFileName) {
        this.filePath = FOLDER + PATH_SEPARATOR + ((modelFileName != null) ? modelFileName : name().toLowerCase()) + "." + FORMAT;
        this.modelOffset = offset;
        this.textureFileName = textureFileName;
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
