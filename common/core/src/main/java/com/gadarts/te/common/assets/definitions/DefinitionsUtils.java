package com.gadarts.te.common.assets.definitions;

import java.util.List;

public final class DefinitionsUtils {
    public static <T extends ElementDefinition> T parse(String id, List<T> definitions) {
        return definitions.stream()
            .filter(definition -> definition.id().equalsIgnoreCase(id))
            .findFirst().orElse(null);
    }
}
