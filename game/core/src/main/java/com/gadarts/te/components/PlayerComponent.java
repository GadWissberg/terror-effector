package com.gadarts.te.components;

import com.gadarts.te.components.cd.CharacterAnimations;
import lombok.Getter;

@Getter
public class PlayerComponent implements GameComponent {
    private CharacterAnimations generalAnimations;

    @Override
    public void reset( ) {

    }

    public void init(CharacterAnimations general) {
        this.generalAnimations = general;
    }
}
