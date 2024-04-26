package com.gadarts.te.common.assets.definitions.env;

import com.gadarts.te.common.assets.definitions.ElementDefinition;
import com.gadarts.te.common.assets.model.Models;
import com.gadarts.te.common.definitions.env.EnvObjectType;

public record EnvObjectDefinition(String id,
                                  String displayName,
                                  Models model,
                                  EnvObjectType type) implements ElementDefinition {


    @Override
    public String name( ) {
        return displayName;
    }

    @Override
    public boolean hiddenFromMap( ) {
        return ElementDefinition.super.hiddenFromMap();
    }
}
