package com.gadarts.te.components.character;

import com.gadarts.te.common.map.element.Direction;
import com.gadarts.te.components.GameComponent;
import lombok.Getter;
import lombok.Setter;

@Getter
public class CharacterComponent implements GameComponent {
    private final CharacterRotationData rotationData = new CharacterRotationData();
    private CharacterSpriteData characterSpriteData;
    @Setter
    private Direction facingDirection;

    public void init(CharacterSpriteData characterSpriteData, Direction direction) {
        this.characterSpriteData = characterSpriteData;
        this.facingDirection = direction;
    }

    @Override
    public void reset( ) {

    }
}
