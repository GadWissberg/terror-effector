package com.gadarts.te.common.assets.texture;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;

@Getter
public enum TexturesTypes {
    Floors(SurfaceTextures.values()),
    UI(UiTextures.values());

    private final TextureDefinition[] definitions;

    TexturesTypes(final TextureDefinition[] definitions) {
        this.definitions = definitions;
    }

    public static TextureDefinition[] getAllDefinitionsInSingleArray( ) {
        ArrayList<TextureDefinition> list = new ArrayList<>();
        Arrays.stream(values()).forEach(defs -> list.addAll(Arrays
            .stream(defs.getDefinitions()).toList())
        );
        return list.toArray(new TextureDefinition[0]);
    }
}
