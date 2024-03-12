package com.gadarts.te.common.utils;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;

public final class LightUtils {
    public static Environment createEnvironment( ) {
        Environment environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.5F, 0.5F, 0.5F, 0.1f));
        return environment;
    }
}
