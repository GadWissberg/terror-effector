package com.gadarts.te.common.definitions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum EnvObjectsTypes {
    OBSTACLES(Obstacles.values()),
    WALL_OBJECTS(WallObjects.values());

    public static final ArrayList<EnvObjectDefinition> allDefinitions = new ArrayList<>();

    static {
        Arrays.stream(EnvObjectsTypes.values()).forEach(type -> allDefinitions.addAll(Arrays.stream(type.getValues()).toList()));
    }

    private final EnvObjectDefinition[] values;
}
