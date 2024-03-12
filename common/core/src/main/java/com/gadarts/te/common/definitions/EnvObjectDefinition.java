package com.gadarts.te.common.definitions;

import com.gadarts.te.common.assets.model.Models;

public interface EnvObjectDefinition {
    String getDisplayName( );

    Models getModelDefinition( );

    String name( );

}
