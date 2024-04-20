package com.gadarts.te.components.cd;

import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.gadarts.te.common.definitions.character.SpriteType;
import com.gadarts.te.common.map.element.Direction;
import com.gadarts.te.components.GameComponent;
import lombok.Getter;

import static com.gadarts.te.common.definitions.character.CharacterType.BILLBOARD_SCALE;
import static com.gadarts.te.common.definitions.character.CharacterType.BILLBOARD_Y;

@Getter
public class CharacterDecalComponent implements GameComponent {
    private final static Vector2 auxVector2 = new Vector2();
    private final static Vector3 auxVector3 = new Vector3();
    private Decal decal;
    private CharacterAnimations animations;
    private Direction direction;
    private SpriteType spriteType;

    public void initializeSprite(final SpriteType type, final Direction direction) {
        this.spriteType = type;
        this.direction = direction;
    }

    public Vector2 getNodePosition(final Vector2 output) {
        Vector3 position = decal.getPosition();
        Vector2 decalPosition = auxVector2.set(position.x, position.z);
        return output.set(decalPosition.set(MathUtils.floor(auxVector2.x), MathUtils.floor(auxVector2.y)));
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

    @Override
    public void reset( ) {

    }

    private void createCharacterDecal(final CharacterAnimations animations,
                                      final SpriteType type,
                                      final Direction direction,
                                      final Vector3 position) {
        decal = Decal.newDecal(animations.get(type, direction).getKeyFrames()[0], true);
        decal.setScale(BILLBOARD_SCALE);
        position.y += BILLBOARD_Y;
        decal.setPosition(position);
    }
}
