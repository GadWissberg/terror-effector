package com.gadarts.te.systems.character;

import com.gadarts.te.common.definitions.character.SpriteType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CharacterCommandDefinition {
    GO_TO(SpriteType.RUN),
    ATTACK_MELEE(SpriteType.ATTACK_PRIMARY);

    private final SpriteType spriteType;

}
