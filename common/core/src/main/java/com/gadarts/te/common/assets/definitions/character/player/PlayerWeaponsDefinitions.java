package com.gadarts.te.common.assets.definitions.character.player;

import com.gadarts.te.common.assets.definitions.Definition;

import java.util.List;

public record PlayerWeaponsDefinitions(
    List<PlayerWeaponDefinition> definitions) implements Definition {


}
