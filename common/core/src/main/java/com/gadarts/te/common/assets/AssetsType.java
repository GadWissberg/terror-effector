package com.gadarts.te.common.assets;

import com.badlogic.gdx.graphics.Color;
import com.gadarts.te.common.assets.atlas.Atlases;
import com.gadarts.te.common.assets.declarations.Declarations;
import com.gadarts.te.common.assets.model.Models;
import com.gadarts.te.common.assets.shaders.Shaders;
import com.gadarts.te.common.assets.texture.TexturesTypes;
import com.gadarts.te.common.assets.texture.UiTextures;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AssetsType {
    TEXTURE(TexturesTypes.getAllDefinitionsInSingleArray()),
    MODEL(Models.values()),
    ATLAS(Atlases.values()),
    DECLARATIONS(Declarations.values(), false, true),
    SHADER(Shaders.values());

    private final AssetDefinition[] assetDefinitions;
    private final boolean manualLoad;
    private final boolean block;

    AssetsType(final AssetDefinition[] assetDefinitions) {
        this(assetDefinitions, false, false);
    }

    public static GsonBuilder generateDefinedGsonBuilder( ) {
        return new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(Color.class, (JsonDeserializer<Color>) (j, t, c) -> Color.valueOf(j.getAsString().toUpperCase()))
            .registerTypeAdapter(Atlases.class, (JsonDeserializer<Atlases>) (j, t, c) -> Atlases.valueOf(j.getAsString().toUpperCase()))
            .registerTypeAdapter(Models.class, (JsonDeserializer<Models>) (j, t, c) -> Models.valueOf(j.getAsString().toUpperCase()))
            .registerTypeAdapter(UiTextures.class, (JsonDeserializer<UiTextures>) (j, t, c) -> UiTextures.valueOf(j.getAsString().toUpperCase()));
    }

}