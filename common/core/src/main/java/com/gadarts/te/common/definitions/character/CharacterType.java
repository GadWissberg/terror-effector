package com.gadarts.te.common.definitions.character;

import lombok.Getter;

@Getter
public enum CharacterType {
    PLAYER, ENEMY, NPC;

    public static final float BILLBOARD_SCALE = 0.013F;
    public static final float BILLBOARD_Y = 0.7f;
}
