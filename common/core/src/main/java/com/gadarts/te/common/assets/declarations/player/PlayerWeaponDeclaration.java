package com.gadarts.te.common.assets.declarations.player;

import com.gadarts.te.common.assets.atlas.Atlases;
import com.gadarts.te.common.assets.model.Models;
import com.gadarts.te.common.assets.texture.TextureDefinition;
import com.gadarts.te.common.assets.declarations.items.ItemDeclaration;
import com.gadarts.te.common.assets.declarations.items.WeaponDeclaration;

public record PlayerWeaponDeclaration(String id,
                                      int hitFrameIndex,
                                      int symbolWidth,
                                      int symbolHeight,
                                      WeaponDeclaration declaration,
                                      Atlases relatedAtlas,
                                      int[] mask,
                                      int magazineSize) implements ItemDeclaration {

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
    public TextureDefinition getSymbol( ) {
        return null;
    }


    @Override
    public Models getModelDefinition( ) {
        return null;
    }
}
