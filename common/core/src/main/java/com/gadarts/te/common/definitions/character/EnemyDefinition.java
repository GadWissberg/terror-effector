package com.gadarts.te.common.definitions.character;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EnemyDefinition implements CharacterDefinition {
    MAINT_BOT;


    @Override
    public CharacterType getCharacterType( ) {
        return CharacterType.ENEMY;
    }
}
