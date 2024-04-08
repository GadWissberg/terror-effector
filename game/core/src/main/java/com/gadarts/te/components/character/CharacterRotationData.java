package com.gadarts.te.components.character;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CharacterRotationData {
    private boolean rotating;
    private long lastRotation;

    public void reset( ) {
        rotating = false;
        lastRotation = 0;
    }
}
