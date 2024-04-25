package com.gadarts.te.common.assets.declarations.env;

import com.gadarts.te.common.assets.declarations.Declaration;

import java.util.List;

public record EnvObjectsDeclarations(List<EnvObjectsDeclarations> envObjectsDeclarations) implements Declaration {
}
