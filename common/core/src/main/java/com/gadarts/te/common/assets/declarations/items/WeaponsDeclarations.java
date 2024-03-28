package com.gadarts.te.common.assets.declarations.items;

import com.gadarts.te.common.assets.declarations.Declaration;

import java.util.List;

public record WeaponsDeclarations(List<WeaponDeclaration> weaponsDeclarations) implements Declaration {

    public WeaponDeclaration parse(String id) {
        return weaponsDeclarations.stream()
            .filter(weaponDeclaration -> weaponDeclaration.getId().equalsIgnoreCase(id))
            .findFirst().orElse(null);
    }
}
