package com.gadarts.te.common.assets.declarations.items;

import com.gadarts.te.common.assets.texture.TextureDefinition;
import com.gadarts.te.common.assets.declarations.ModelElementDeclaration;

public interface ItemDeclaration extends ModelElementDeclaration {
    int getSymbolWidth( );

    String getId( );

    int[] getMask( );

    int getSymbolHeight( );


    TextureDefinition getSymbol( );

}
