package com.gadarts.te.common.assets.declarations.player;

import com.gadarts.te.common.assets.declarations.Declaration;

import java.util.List;

public record PlayerWeaponsDeclarations(
    List<PlayerWeaponDeclaration> playerWeaponsDeclarations) implements Declaration {

    public PlayerWeaponDeclaration parse(String id) {
        return playerWeaponsDeclarations.stream()
            .filter(playerWeaponDeclaration -> playerWeaponDeclaration.getId().equalsIgnoreCase(id))
            .findFirst().orElse(null);
    }
}
