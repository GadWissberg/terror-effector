package com.gadarts.te.common.assets.texture;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;

@Getter
public enum TexturesTypes {
    Floors(SurfaceTextures.values()),
    UI(UiTextures.values());

    private final TextureDeclaration[] definitions;

    TexturesTypes(final TextureDeclaration[] definitions) {
        this.definitions = definitions;
    }

    public static TextureDeclaration[] getAllDefinitionsInSingleArray( ) {
        ArrayList<TextureDeclaration> list = new ArrayList<>();
        Arrays.stream(values()).forEach(defs -> list.addAll(Arrays
            .stream(defs.getDefinitions()).toList())
        );
        return list.toArray(new TextureDeclaration[0]);
    }
}
