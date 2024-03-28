package com.gadarts.te.components.character;

import com.gadarts.te.common.map.element.Direction;
import com.gadarts.te.components.GameComponent;
import lombok.Getter;

@Getter
public class CharacterComponent implements GameComponent {
    private CharacterSpriteData characterSpriteData;
    private Direction facingDirection;

    public void init(CharacterSpriteData characterSpriteData, Direction direction) {
        this.characterSpriteData = characterSpriteData;
        this.facingDirection = direction;
    }

    @Override
    public void reset( ) {

    }
}
