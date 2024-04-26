package com.gadarts.te.common.assets.loaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.gadarts.te.common.assets.definitions.Definition;
import com.gadarts.te.common.assets.definitions.items.WeaponDefinition;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.InputStreamReader;

import static com.gadarts.te.common.assets.AssetsType.generateDefinedGsonBuilder;
import static java.lang.String.format;

public class DefinitionsLoader extends AsynchronousAssetLoader<Definition, DefinitionsLoader.DefinitionsLoaderParameter> {
    private final Gson gson;

    @Getter
    @RequiredArgsConstructor
    public static class DefinitionsLoaderParameter extends AssetLoaderParameters<Definition> {
        private final Class<? extends Definition> typeClass;
    }

    public DefinitionsLoader(FileHandleResolver resolver,
                             JsonDeserializer<WeaponDefinition> weaponDefinitionDeserializer) {
        super(resolver);
        this.gson = generateDefinedGsonBuilder()
            .registerTypeAdapter(WeaponDefinition.class, weaponDefinitionDeserializer)
            .create();
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, DefinitionsLoaderParameter parameter) {
        return null;
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, DefinitionsLoaderParameter parameter) {

    }

    @Override
    public Definition loadSync(AssetManager manager, String fileName, FileHandle file, DefinitionsLoaderParameter parameter) {
        String path = format("%s", fileName);
        InputStreamReader reader = (InputStreamReader) Gdx.files.internal(path).reader();
        return gson.fromJson(reader, parameter.getTypeClass());
    }


}
