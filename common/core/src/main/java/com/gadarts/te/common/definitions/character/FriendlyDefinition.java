package com.gadarts.te.common.definitions.character;

import com.gadarts.te.common.assets.atlas.AtlasDefinition;

public enum FriendlyDefinition implements CharacterDefinition {
    PLAYER;

    @Override
    public String getDisplayName( ) {
        return "Our Hero";
    }

    @Override
    public CharacterType getCharacterType( ) {
        return CharacterType.PLAYER;
    }

    @Override
    public AtlasDefinition getAtlasDefinition( ) {
        return null;
    }
}
