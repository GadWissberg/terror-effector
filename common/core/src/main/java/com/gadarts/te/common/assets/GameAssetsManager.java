package com.gadarts.te.common.assets;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.gadarts.te.common.assets.texture.TextureDefinition;

import java.util.Arrays;

public class GameAssetsManager extends AssetManager {
    public static final String PATH_SEPARATOR = "/";
    private final String assetsLocation;

    public GameAssetsManager( ) {
        this("");
    }

    public GameAssetsManager(final String assetsLocation) {
        this.assetsLocation = assetsLocation;
        FileHandleResolver resolver = new InternalFileHandleResolver();
        setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
    }


    public void loadGameFiles(final AssetsTypes... assetsTypesToExclude) {
        Arrays.stream(AssetsTypes.values())
            .filter(type -> Arrays.stream(assetsTypesToExclude).noneMatch(toExclude -> toExclude == type))
            .filter(type -> !type.isManualLoad())
            .forEach(type -> Arrays.stream(type.getAssetDefinitions()).forEach(def -> {
                String[] filesList = def.getFilesList();
                if (filesList.length == 0) {
                    loadFile(def, def.getFilePath(), type.isBlock());
                } else {
                    Arrays.stream(filesList).forEach(file -> loadFile(def, file, type.isBlock()));
                }
            }));
        finishLoading();
    }

    @Override
    public <T> void addAsset(final String fileName, final Class<T> type, final T asset) {
        super.addAsset(fileName, type, asset);
        if (type == com.badlogic.gdx.graphics.g3d.Model.class) {
            com.badlogic.gdx.graphics.g3d.Model model = (com.badlogic.gdx.graphics.g3d.Model) asset;
            model.materials.forEach(material -> material.remove(ColorAttribute.Specular));
        }
    }


    public Texture getTexture(final TextureDefinition definition) {
        return get(assetsLocation + definition.getFilePath(), Texture.class);
    }

    private void loadFile(AssetDefinition def, String fileName, boolean block) {
        String path = Gdx.files.getFileHandle(assetsLocation + fileName, Files.FileType.Internal).path();
        if (def.getParameters() != null) {
            load(def.getAssetManagerKey() != null ? def.getAssetManagerKey() : path, def.getTypeClass(), def.getParameters());
        } else {
            load(path, def.getTypeClass());
        }
        if (block) {
            finishLoadingAsset(path);
        }
    }

}
