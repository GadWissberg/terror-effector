package com.gadarts.te.common.assets.definitions.env;

import com.gadarts.te.common.assets.definitions.Definition;

import java.util.List;

public record EnvObjectsDefinitions(List<EnvObjectDefinition> definitions) implements Definition {
}
