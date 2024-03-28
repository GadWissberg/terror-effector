package com.gadarts.te.common.assets.declarations;

import com.badlogic.gdx.math.Vector3;
import com.gadarts.te.common.assets.atlas.Atlases;
import com.gadarts.te.common.definitions.character.CharacterType;

public interface CharacterDeclaration extends ElementDeclaration {
    CharacterType getCharacterType( );

    boolean isSingleDeathAnimation( );

    String name( );

    int getPrimaryAttackHitFrameIndex( );

    Atlases getAtlasDefinition( );

    default float getShadowRadius( ) {
        return 0.2F;
    }

    float getHeight( );

    default Vector3 getBulletCreationOffset(Vector3 output) {
        return output.set(output.x, getHeight() / 2F, output.z);
    }


}
