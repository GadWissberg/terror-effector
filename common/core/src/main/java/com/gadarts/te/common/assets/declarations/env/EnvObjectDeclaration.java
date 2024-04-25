package com.gadarts.te.common.assets.declarations.env;

import com.gadarts.te.common.assets.declarations.ElementDeclaration;
import com.gadarts.te.common.assets.model.Models;

public record EnvObjectDeclaration(String id,
                                   String displayName,
                                   Models modelDefinition) implements ElementDeclaration {


    @Override
    public String name( ) {
        return displayName;
    }

    @Override
    public boolean hiddenFromMap( ) {
        return ElementDeclaration.super.hiddenFromMap();
    }
}
