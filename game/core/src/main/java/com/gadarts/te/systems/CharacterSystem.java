package com.gadarts.te.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.gadarts.te.common.assets.GameAssetsManager;
import com.gadarts.te.common.definitions.character.SpriteType;
import com.gadarts.te.common.utils.GeneralUtils;
import com.gadarts.te.components.ComponentsMapper;
import com.gadarts.te.components.character.CharacterComponent;
import com.gadarts.te.components.character.CharacterSpriteData;
import com.gadarts.te.systems.data.SharedDataBuilder;

import static com.gadarts.te.common.definitions.character.SpriteType.IDLE;

public class CharacterSystem extends GameSystem {
    public static final long MAX_IDLE_ANIMATION_INTERVAL = 10000L;
    private static final long MIN_IDLE_ANIMATION_INTERVAL = 2000L;

    private ImmutableArray<Entity> characters;

    @Override
    public void initialize(SharedDataBuilder sharedDataBuilder, GameAssetsManager assetsManager, MessageDispatcher eventDispatcher) {
        super.initialize(sharedDataBuilder, assetsManager, eventDispatcher);
        characters = getEngine().getEntitiesFor(Family.all(CharacterComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        long now = TimeUtils.millis();
        for (int i = 0; i < characters.size(); i++) {
            Entity character = characters.get(i);
            handleIdle(character, now);
        }
    }


    private void handleIdle(Entity character, long now) {
        CharacterSpriteData spriteData = ComponentsMapper.character.get(character).getCharacterSpriteData();
        SpriteType spriteType = spriteData.getSpriteType();
        if (spriteType == IDLE && spriteData.getNextIdleAnimationPlay() < now) {
            long random = MathUtils.random(MAX_IDLE_ANIMATION_INTERVAL - MIN_IDLE_ANIMATION_INTERVAL);
            spriteData.setNextIdleAnimationPlay(now + MIN_IDLE_ANIMATION_INTERVAL + random);
            ComponentsMapper.animation.get(character).getAnimation().setFrameDuration(spriteType.getFrameDuration());
        }
    }

    @Override
    public void dispose( ) {
        GeneralUtils.disposeObject(this, CharacterSystem.class);
    }
}
