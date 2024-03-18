package com.gadarts.te.common.definitions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

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

    public static EnvObjectDefinition findDefinition(String name) {
        String lowerCase = name.toLowerCase();
        Optional<EnvObjectDefinition> result = allDefinitions.stream().filter(def -> def.name().toLowerCase().equals(lowerCase)).findAny();
        return result.orElse(null);
    }
}
