package com.gadarts.te.common.assets.declarations.enemy;

import com.gadarts.te.common.assets.atlas.Atlases;
import com.gadarts.te.common.assets.declarations.CharacterDeclaration;
import com.gadarts.te.common.assets.declarations.items.WeaponDeclaration;
import com.gadarts.te.common.assets.sounds.Sounds;
import com.gadarts.te.common.assets.texture.UiTextures;
import com.gadarts.te.common.definitions.character.CharacterType;
import com.gadarts.te.common.utils.ImmutableVector3;

public record EnemyDeclaration(String id,
                               String displayName,
                               Atlases atlasDefinition,
                               Integer health,
                               int engine,
                               WeaponDeclaration attackPrimary,
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
                               UiTextures hudIcon) implements CharacterDeclaration {
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
