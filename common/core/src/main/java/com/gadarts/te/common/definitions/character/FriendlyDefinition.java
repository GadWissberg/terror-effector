package com.gadarts.te.common.definitions.character;

public enum FriendlyDefinition implements CharacterDefinition {
    PLAYER;

    @Override
    public CharacterType getCharacterType( ) {
        return CharacterType.PLAYER;
    }

}
