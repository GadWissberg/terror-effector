package com.gadarts.te.common.assets.definitions.character.enemy;

import com.gadarts.te.common.assets.atlas.Atlases;
import com.gadarts.te.common.assets.definitions.character.CharacterDefinition;
import com.gadarts.te.common.assets.definitions.items.WeaponDefinition;
import com.gadarts.te.common.assets.sounds.Sounds;
import com.gadarts.te.common.assets.texture.UiTextures;
import com.gadarts.te.common.definitions.character.CharacterType;
import com.gadarts.te.common.utils.ImmutableVector3;

public record EnemyDefinition(String id,
                              String displayName,
                              Atlases atlasDefinition,
                              Integer health,
                              int engine,
                              WeaponDefinition attackPrimary,
                              int attackPrimaryHitFrameIndex,
                              boolean singleDeathAnimation,
                              float height,
                              Sounds soundAwake,
                              Sounds soundMelee,
                              Sounds soundPain,
                              Sounds soundDeath,
                              Sounds soundStep,
                              Sounds explosionEffectOnDestroy,
                              float shadowRadius,
                              boolean human,
                              ImmutableVector3 bulletCreationOffset,
                              UiTextures hudIcon) implements CharacterDefinition {
    @Override
    public CharacterType getCharacterType( ) {
        return CharacterType.ENEMY;
    }

    @Override
    public boolean isSingleDeathAnimation( ) {
        return false;
    }

    @Override
    public String name( ) {
        return displayName;
    }

    @Override
    public int getPrimaryAttackHitFrameIndex( ) {
        return attackPrimaryHitFrameIndex;
    }

    @Override
    public Atlases getAtlasDefinition( ) {
        return atlasDefinition;
    }

    @Override
    public float getHeight( ) {
        return height;
    }


}
