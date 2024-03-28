package com.gadarts.te.common.assets.declarations.player;

import com.gadarts.te.common.assets.atlas.Atlases;
import com.gadarts.te.common.definitions.character.CharacterType;
import com.gadarts.te.common.assets.declarations.CharacterDeclaration;

public class PlayerDeclaration implements CharacterDeclaration {
    private static final PlayerDeclaration instance = new PlayerDeclaration();

    private PlayerDeclaration( ) {
    }

    @SuppressWarnings({"LombokGetterMayBeUsed", "RedundantSuppression"})
    public static PlayerDeclaration getInstance( ) {
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
