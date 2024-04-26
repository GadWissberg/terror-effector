package com.gadarts.te.common.assets.definitions.character.player;

import com.gadarts.te.common.assets.atlas.Atlases;
import com.gadarts.te.common.definitions.character.CharacterType;
import com.gadarts.te.common.assets.definitions.character.CharacterDefinition;

public class PlayerDefinition implements CharacterDefinition {
    private static final PlayerDefinition instance = new PlayerDefinition();

    private PlayerDefinition( ) {
    }

    @SuppressWarnings({"LombokGetterMayBeUsed", "RedundantSuppression"})
    public static PlayerDefinition getInstance( ) {
        return instance;
    }

    @Override
    public String displayName( ) {
        return "Player";
    }

    @Override
    public String id( ) {
        return name();
    }

    @Override
    public String toString( ) {
        return displayName();
    }

    @Override
    public CharacterType getCharacterType( ) {
        return CharacterType.PLAYER;
    }

    @Override
    public boolean isSingleDeathAnimation( ) {
        return true;
    }

    @Override
    public String name( ) {
        return "player";
    }

    @Override
    public int getPrimaryAttackHitFrameIndex( ) {
        return -1;
    }

    @Override
    public Atlases getAtlasDefinition( ) {
        return Atlases.PLAYER_GLOCK;
    }

    @Override
    public float getHeight( ) {
        return 1.5F;
    }

}
