package com.gadarts.te.common.assets.definitions.character.player;

import com.gadarts.te.common.assets.atlas.Atlases;
import com.gadarts.te.common.assets.model.Models;
import com.gadarts.te.common.assets.texture.TextureDeclaration;
import com.gadarts.te.common.assets.definitions.items.ItemDefinition;
import com.gadarts.te.common.assets.definitions.items.WeaponDefinition;

public record PlayerWeaponDefinition(String id,
                                     int hitFrameIndex,
                                     int symbolWidth,
                                     int symbolHeight,
                                     WeaponDefinition declaration,
                                     Atlases relatedAtlas,
                                     int[] mask,
                                     int magazineSize) implements ItemDefinition {

    @Override
    public String displayName( ) {
        return declaration.displayName();
    }

    @Override
    public String name( ) {
        return id;
    }

    @Override
    public int getSymbolWidth( ) {
        return symbolWidth;
    }

    @Override
    public String getId( ) {
        return id;
    }

    @Override
    public int[] getMask( ) {
        return mask;
    }

    @Override
    public int getSymbolHeight( ) {
        return symbolHeight;
    }

    @Override
    public TextureDeclaration getSymbol( ) {
        return null;
    }


    @Override
    public Models getModelDefinition( ) {
        return null;
    }
}
