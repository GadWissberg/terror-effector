package com.gadarts.te.common.assets.definitions;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.gadarts.te.common.assets.definitions.character.enemy.EnemiesDefinitions;
import com.gadarts.te.common.assets.definitions.env.EnvObjectsDefinitions;
import com.gadarts.te.common.assets.definitions.items.WeaponsDefinitions;
import com.gadarts.te.common.assets.definitions.character.player.PlayerWeaponsDefinitions;
import com.gadarts.te.common.assets.loaders.DefinitionsLoader;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Definitions implements DefinitionDeclaration {
    WEAPONS(WeaponsDefinitions.class, "items"),
    PLAYER_WEAPONS(PlayerWeaponsDefinitions.class, "character.player"),
    ENEMIES(EnemiesDefinitions.class, "character.enemy"),
    ENV_OBJECTS(EnvObjectsDefinitions.class, "env");

    private final Class<? extends Definition> clazz;
    private final String packageName;


    @SuppressWarnings("unchecked")
    @Override
    public AssetLoaderParameters<Definition> getParameters( ) {
        try {
            String clazz = this.clazz.getSimpleName();
            String address = "com.gadarts.te.common.assets.definitions.%s.%s";
            String packageName = this.packageName != null ? this.packageName : name().toLowerCase().replace("_", "");
            String format = String.format(address, packageName, clazz);
            return new DefinitionsLoader.DefinitionsLoaderParameter((Class<? extends Definition>) Class.forName(format));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getSubFolderName( ) {
        return null;
    }

    @Override
    public String getName( ) {
        return name().toLowerCase();
    }


}
