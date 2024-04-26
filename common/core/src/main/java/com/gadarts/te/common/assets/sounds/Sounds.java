package com.gadarts.te.common.assets.sounds;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.audio.Sound;
import lombok.Getter;

import java.util.stream.IntStream;

import static com.gadarts.te.common.assets.GameAssetsManager.PATH_SEPARATOR;

@Getter
public enum Sounds implements SoundDeclaration {
    STEP(true, false, "step_concrete_0", "step_concrete_1", "step_concrete_2", "step_concrete_3");

    private final String filePath;
    private final boolean randomPitch;
    private final boolean loop;
    private final String[] files;

    Sounds( ) {
        this(true);
    }

    Sounds(final boolean randomPitch) {
        this(randomPitch, false);
    }

    Sounds(final boolean randomPitch, final boolean loop) {
        this(randomPitch, loop, new String[0]);
    }

    Sounds(final boolean randomPitch, final boolean loop, final String... files) {
        this.filePath = FOLDER + PATH_SEPARATOR + name().toLowerCase() + "." + FORMAT;
        this.randomPitch = randomPitch;
        this.loop = loop;
        this.files = files;
        IntStream.range(0, files.length).forEach(i -> files[i] = FOLDER + PATH_SEPARATOR + files[i] + "." + FORMAT);
    }

    @Override
    public String[] getFilesList( ) {
        return files;
    }

    @Override
    public AssetLoaderParameters<Sound> getParameters( ) {
        return null;
    }

    @Override
    public Class<Sound> getTypeClass( ) {
        return Sound.class;
    }

}
