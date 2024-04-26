package com.gadarts.te.common.assets.definitions;

public interface ElementDefinition extends Definition {
    String displayName( );

    String id( );

    String name( );

    default boolean hiddenFromMap( ) {
        return false;
    }

}
