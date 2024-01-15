package com.gadarts.te.assets;

import com.gadarts.te.assets.textures.SurfaceTextures;
import com.gadarts.te.assets.textures.TextureDefinition;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;

@Getter
public enum TexturesTypes {
    Floors(SurfaceTextures.values());

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
