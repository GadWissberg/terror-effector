package com.gadarts.te.common.definitions.character;

public enum FriendlyDefinition implements CharacterDefinition {
    PLAYER;

    @Override
    public String getDisplayName( ) {
        return "Our Hero";
    }
}