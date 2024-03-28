package com.gadarts.te.common.assets.declarations;

public interface ElementDeclaration extends Declaration {
    String displayName( );

    String id( );

    String name( );

    default boolean hiddenFromMap( ) {
        return false;
    }

}
