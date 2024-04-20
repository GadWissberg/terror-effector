package com.gadarts.te.common.assets;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.gadarts.te.common.assets.atlas.AtlasDefinition;
import com.gadarts.te.common.assets.declarations.Declaration;
import com.gadarts.te.common.assets.declarations.Declarations;
import com.gadarts.te.common.assets.declarations.items.WeaponDeclaration;
import com.gadarts.te.common.assets.declarations.items.WeaponsDeclarations;
import com.gadarts.te.common.assets.loaders.DeclarationsLoader;
import com.gadarts.te.common.assets.loaders.ShaderLoader;
import com.gadarts.te.common.assets.melodies.Melodies;
import com.gadarts.te.common.assets.model.ModelDefinition;
import com.gadarts.te.common.assets.model.Models;
import com.gadarts.te.common.assets.shaders.Shaders;
import com.gadarts.te.common.assets.sounds.Sounds;
import com.gadarts.te.common.assets.texture.TextureDefinition;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.Optional;

import static com.gadarts.te.common.assets.AssetsType.generateDefinedGsonBuilder;

@SuppressWarnings("unchecked")
public class GameAssetsManager extends AssetManager {
    public static final String PATH_SEPARATOR = "/";
    private final String assetsLocation;
    private final Gson gson = generateDefinedGsonBuilder().create();

    public GameAssetsManager() {
        this("");
    }

    public GameAssetsManager(final String assetsLocation) {
        this.assetsLocation = assetsLocation;
        FileHandleResolver resolver = new InternalFileHandleResolver();
        setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        setLoader(String.class, new ShaderLoader(getFileHandleResolver()));
        setLoader(Declaration.class, DeclarationDefinition.FORMAT, new DeclarationsLoader(resolver, (json, t, c) -> {
            WeaponDeclaration result;
            if (json.isJsonPrimitive()) {
                WeaponsDeclarations declaration = (WeaponsDeclarations) getDeclaration(Declarations.WEAPONS);
                result = declaration.parse(json.getAsString());
            } else {
                result = gson.fromJson(json, t);
            }
            return result;
        }));
    }

    public TextureAtlas getAtlas(final AtlasDefinition atlas) {
        return get(assetsLocation + atlas.getFilePath(), TextureAtlas.class);
    }

    public Texture getModelExplicitTexture(final ModelDefinition model) {
        return get(assetsLocation + Models.FOLDER + "/" + model.getTextureFileName() + ".png", Texture.class);
    }

    public Sound getSound(Sounds sound) {
        return getSound(sound.getFilePath());
    }

    public Sound getSound(String filePath) {
        return get(assetsLocation + filePath, Sound.class);
    }

    public Declaration getDeclaration(Declarations declaration) {
        return get(assetsLocation + declaration.getFilePath(), Declaration.class);
    }

    public void loadGameFiles(final AssetsType... assetsTypeToExclude) {
        Arrays.stream(AssetsType.values())
            .filter(type -> Arrays.stream(assetsTypeToExclude).noneMatch(toExclude -> toExclude == type))
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

    public Music getMelody(Melodies definition) {
        return get(assetsLocation + definition.getFilePath(), Music.class);
    }


    public com.badlogic.gdx.graphics.g3d.Model getModel(final ModelDefinition model) {
        return get(assetsLocation + model.getFilePath(), com.badlogic.gdx.graphics.g3d.Model.class);
    }

    public String getShader(Shaders shaders) {
        return get(assetsLocation + shaders.getFilePath(), String.class);
    }

    private void loadFile(AssetDefinition def, String fileName, boolean block) {
        String path = Gdx.files.getFileHandle(assetsLocation + fileName, Files.FileType.Internal).path();
        if (def.getParameters() != null) {
            load(path, def.getTypeClass(), def.getParameters());
        } else {
            load(path, def.getTypeClass());
        }
        if (block) {
            finishLoadingAsset(path);
        }
        loadModelExplicitTexture(def);
    }

    private void loadModelExplicitTexture(AssetDefinition def) {
        if (def instanceof ModelDefinition modelDef) {
            Optional.ofNullable(modelDef.getTextureFileName()).ifPresent(t -> {
                String fileName = assetsLocation + ModelDefinition.FOLDER + "/" + t + ".png";
                load(fileName, Texture.class);
            });
        }
    }


}
