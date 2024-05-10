package com.gadarts.te;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.gadarts.te.common.assets.GameAssetsManager;
import com.gadarts.te.common.assets.atlas.Atlases;
import com.gadarts.te.common.definitions.character.SpriteType;
import com.gadarts.te.common.map.element.Direction;
import com.gadarts.te.common.utils.GeneralUtils;
import com.gadarts.te.components.cd.CharacterAnimation;
import com.gadarts.te.components.cd.CharacterAnimations;
import com.gadarts.te.systems.CameraSystem;
import com.gadarts.te.systems.EnemySystem;
import com.gadarts.te.systems.GameSystem;
import com.gadarts.te.systems.PlayerSystem;
import com.gadarts.te.systems.character.CharacterSystem;
import com.gadarts.te.systems.data.SharedData;
import com.gadarts.te.systems.data.SharedDataBuilder;
import com.gadarts.te.systems.map.MapSystem;
import com.gadarts.te.systems.render.RenderSystem;
import com.gadarts.te.systems.turns.TurnsSystem;
import com.gadarts.te.systems.ui.InterfaceSystem;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class InGameScreen implements Screen {
    private final List<GameSystem> systems = List.of(
        new CameraSystem(),
        new CharacterSystem(),
        new InterfaceSystem(),
        new RenderSystem(),
        new PlayerSystem(),
        new MapSystem(),
        new EnemySystem(),
        new TurnsSystem());

    private PooledEngine engine;
    private GameAssetsManager assetsManager;
    private SharedData sharedData;
    private SoundPlayer soundPlayer;

    @Override
    public void show( ) {
        engine = new PooledEngine();
        SharedDataBuilder sharedDataBuilder = new SharedDataBuilder();
        assetsManager = new GameAssetsManager();
        assetsManager.loadGameFiles();
        soundPlayer = new SoundPlayer(assetsManager);
        generateCharactersAnimations();
        MessageDispatcher eventDispatcher = new MessageDispatcher();
        systems.forEach(system -> {
            engine.addSystem(system);
            system.initialize(sharedDataBuilder, assetsManager, eventDispatcher, soundPlayer);
        });
        sharedData = sharedDataBuilder.build();
        systems.forEach(system -> system.onSystemReady(sharedData));
    }

    private void generateCharactersAnimations( ) {
        Arrays.stream(Atlases.values())
            .forEach(atlas -> assetsManager.addAsset(
                atlas.name(),
                CharacterAnimations.class,
                createCharacterAnimations(atlas)));
    }

    @SuppressWarnings("GDXJavaUnsafeIterator")
    private boolean checkIfAtlasContainsSpriteType(SpriteType spriteType, TextureAtlas atlas) {
        boolean result = false;
        for (TextureAtlas.AtlasRegion region : atlas.getRegions()) {
            if (region.name.startsWith(spriteType.name().toLowerCase())) {
                result = true;
                break;
            }
        }
        return result;
    }

    private CharacterAnimations createCharacterAnimations(final Atlases character) {
        CharacterAnimations animations = new CharacterAnimations();
        TextureAtlas atlas = assetsManager.getAtlas(character);
        Arrays.stream(SpriteType.values())
            .filter(spriteType -> checkIfAtlasContainsSpriteType(spriteType, atlas))
            .forEach(spriteType -> {
                if (spriteType.isSingleDirection()) {
                    inflateCharacterAnimation(animations, atlas, spriteType, Direction.SOUTH);
                } else {
                    Direction[] directions = Direction.values();
                    Arrays.stream(directions).forEach(dir -> inflateCharacterAnimation(animations, atlas, spriteType, dir));
                }
            });
        return animations;
    }

    private void inflateCharacterAnimation(final CharacterAnimations animations,
                                           final TextureAtlas atlas,
                                           final SpriteType spriteType,
                                           final Direction dir) {
        String sprTypeName = spriteType.name().toLowerCase();
        int vars = spriteType.getVariations();
        IntStream.range(0, vars).forEach(variationIndex -> {
            String name = formatNameForVariation(dir, sprTypeName, vars, variationIndex, spriteType.isSingleDirection());
            CharacterAnimation a = createAnimation(atlas, spriteType, name, dir);
            if (a == null && variationIndex == 0) {
                name = formatNameForVariation(dir, sprTypeName, 1, 0, spriteType.isSingleDirection());
                a = createAnimation(atlas, spriteType, name, dir);
                animations.put(spriteType, 0, dir, a);
            } else if (a != null && a.getKeyFrames().length > 0) {
                animations.put(spriteType, variationIndex, dir, a);
            }
        });
    }

    private CharacterAnimation createAnimation(final TextureAtlas atlas,
                                               final SpriteType spriteType,
                                               final String name,
                                               final Direction dir) {
        Array<TextureAtlas.AtlasRegion> regions = atlas.findRegions(name);
        CharacterAnimation animation = null;
        if (!regions.isEmpty()) {
            animation = new CharacterAnimation(
                spriteType.getFrameDuration(),
                regions,
                spriteType.getPlayMode(),
                dir);
        }
        return animation;
    }

    private String formatNameForVariation(Direction dir,
                                          String sprTypeName,
                                          int vars,
                                          int variationIndex,
                                          boolean singleDirection) {
        return String.format("%s%s%s",
            sprTypeName,
            vars > 1 ? "_" + variationIndex : "",
            singleDirection ? "" : "_" + dir.name().toLowerCase());
    }

    @Override
    public void render(float delta) {
        engine.update(delta);
    }

    @Override
    public void resize(int width, int height) {
        // Resize your screen here. The parameters represent the new window size.
    }

    @Override
    public void pause( ) {
        // Invoked when your application is paused.
    }

    @Override
    public void resume( ) {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide( ) {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose( ) {
        systems.forEach(Disposable::dispose);
        GeneralUtils.disposeObject(this, InGameScreen.class);
    }
}
