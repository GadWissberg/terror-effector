package com.gadarts.te.common.assets.declarations.enemy;

import com.gadarts.te.common.assets.declarations.Declaration;

import java.util.List;

public record EnemiesDeclarations(List<EnemyDeclaration> enemiesDeclarations) implements Declaration {
}
