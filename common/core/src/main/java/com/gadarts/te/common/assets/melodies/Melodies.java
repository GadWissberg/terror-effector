package com.gadarts.te.common.assets.melodies;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.audio.Music;
import lombok.Getter;

import static com.gadarts.te.common.assets.GameAssetsManager.PATH_SEPARATOR;

@Getter
public enum Melodies implements MelodyDeclaration {
    TEST;

    private final String filePath;

    Melodies( ) {
        this.filePath = FOLDER + PATH_SEPARATOR + name().toLowerCase() + "." + FORMAT;
    }


    @Override
    public AssetLoaderParameters<Music> getParameters( ) {
        return null;
    }

    @Override
    public Class<Music> getTypeClass( ) {
        return Music.class;
    }

}
