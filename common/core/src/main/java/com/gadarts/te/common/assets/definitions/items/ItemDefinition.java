package com.gadarts.te.common.assets.definitions.items;

import com.gadarts.te.common.assets.texture.TextureDeclaration;
import com.gadarts.te.common.assets.definitions.ModelElementDefinition;

public interface ItemDefinition extends ModelElementDefinition {
    int getSymbolWidth( );

    String getId( );

    int[] getMask( );

    int getSymbolHeight( );


    TextureDeclaration getSymbol( );

}
