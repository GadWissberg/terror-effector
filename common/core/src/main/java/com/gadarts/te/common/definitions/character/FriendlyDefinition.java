package com.gadarts.te.common.definitions.character;

import com.gadarts.te.common.definitions.ElementDefinition;

public enum FriendlyDefinition implements CharacterDefinition {
    PLAYER;

    @Override
    public String getDisplayName( ) {
        return "Our Hero";
    }
}
