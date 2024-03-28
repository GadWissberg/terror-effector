package com.gadarts.te.common.assets.declarations;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.gadarts.te.common.assets.loaders.DeclarationsLoader;
import com.gadarts.te.common.assets.declarations.items.WeaponsDeclarations;
import com.gadarts.te.common.assets.declarations.player.PlayerWeaponsDeclarations;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Declarations implements DeclarationDefinition {
    WEAPONS(WeaponsDeclarations.class, "items"),
    PLAYER_WEAPONS(PlayerWeaponsDeclarations.class, "player");

    private final Class<? extends Declaration> clazz;
    private final String packageName;


    @SuppressWarnings("unchecked")
    @Override
    public AssetLoaderParameters<Declaration> getParameters( ) {
        try {
            String clazz = this.clazz.getSimpleName();
            String address = "com.gadarts.te.common.assets.declarations.%s.%s";
            String packageName = this.packageName != null ? this.packageName : name().toLowerCase().replace("_", "");
            String format = String.format(address, packageName, clazz);
            return new DeclarationsLoader.DeclarationsLoaderParameter((Class<? extends Declaration>) Class.forName(format));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Class<Declaration> getTypeClass( ) {
        return Declaration.class;
    }

    @Override
    public String getSubFolderName( ) {
        return null;
    }

    @Override
    public String getName( ) {
        return name();
    }


}
