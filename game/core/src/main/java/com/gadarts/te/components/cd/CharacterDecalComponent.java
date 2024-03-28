package com.gadarts.te.components.cd;

import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector3;
import com.gadarts.te.common.definitions.character.SpriteType;
import com.gadarts.te.common.map.element.Direction;
import com.gadarts.te.components.GameComponent;
import lombok.Getter;

import static com.gadarts.te.common.definitions.character.CharacterType.BILLBOARD_SCALE;

@Getter
public class CharacterDecalComponent implements GameComponent {
    private Decal decal;
    private CharacterAnimations animations;
    private Direction direction;
    private SpriteType spriteType;

    public void initializeSprite(final SpriteType type, final Direction direction) {
        this.spriteType = type;
        this.direction = direction;
    }

    public void init(final CharacterAnimations animations,
                     final SpriteType type,
                     final Direction direction,
                     final Vector3 position) {
        this.animations = animations;
        this.direction = direction;
        this.spriteType = type;
        createCharacterDecal(animations, type, direction, position);
    }

    private void createCharacterDecal(final CharacterAnimations animations,
                                      final SpriteType type,
                                      final Direction direction,
                                      final Vector3 position) {
        decal = Decal.newDecal(animations.get(type, direction).getKeyFrames()[0], true);//Optimize this - it creates an object each time.
        decal.setScale(BILLBOARD_SCALE);
        decal.setPosition(position);
    }

    @Override
    public void reset( ) {

    }
}
