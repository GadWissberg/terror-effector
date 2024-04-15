package com.gadarts.te;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.gadarts.te.common.assets.GameAssetsManager;
import com.gadarts.te.common.assets.melodies.Melodies;
import com.gadarts.te.common.assets.sounds.Sounds;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.math.MathUtils.random;
import static com.badlogic.gdx.math.MathUtils.randomBoolean;

public class SoundPlayer {
    private static final float MELODY_VOLUME = 0.4f;
    private static final float PITCH_OFFSET = 0.1f;
    private final GameAssetsManager assetManager;
    private final List<Sound> loopingSounds = new ArrayList<>();
    @Getter
    @Setter
    private boolean sfxEnabled;
    @Getter
    private boolean musicEnabled;

    public SoundPlayer(GameAssetsManager assetManager) {
        this.assetManager = assetManager;
        setSfxEnabled(DebugSettings.SFX_ENABLED);
        setMusicEnabled(DebugSettings.MELODY_ENABLED);
    }


    public void setMusicEnabled(final boolean musicEnabled) {
        this.musicEnabled = musicEnabled;
        if (musicEnabled) {
            playMusic(Melodies.TEST);
        } else {
            stopMusic(Melodies.TEST);
        }
    }

    public void playMusic(Melodies melody) {
        if (!isMusicEnabled()) return;
        Music music = assetManager.getMelody(melody);
        music.setVolume(MELODY_VOLUME);
        music.setLooping(true);
        music.play();
    }

    public void stopMusic(Melodies melody) {
        Music music = assetManager.getMelody(melody);
        music.stop();
    }

    public void playSound(Sounds soundDef) {
        if (soundDef == null) return;
        playSound(soundDef, 1F);
    }

    public void playSound(Sounds def, final float volume) {
        if (!isSfxEnabled()) return;
        float pitch = 1 + (def.isRandomPitch() ? (randomBoolean() ? 1 : -1) : 0) * random(-PITCH_OFFSET, PITCH_OFFSET);
        if (!def.isLoop()) {
            assetManager.getSound(getRandomSound(def)).play(volume, pitch, 0);
        } else {
            Sound sound = assetManager.getSound(getRandomSound(def));
            sound.loop(volume, 1, 0);
            loopingSounds.add(sound);
        }
    }

    private String getRandomSound(Sounds soundDef) {
        String filePath = soundDef.getFilePath();
        if (soundDef.getFiles().length > 0) {
            int random = MathUtils.random(soundDef.getFiles().length - 1);
            filePath = soundDef.getFiles()[random];
        }
        return filePath;
    }


}
