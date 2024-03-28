package com.gadarts.te.common.assets.declarations.items;

import com.badlogic.gdx.graphics.Color;
import com.gadarts.te.common.assets.model.Models;
import com.gadarts.te.common.assets.texture.UiTextures;

public record WeaponDeclaration(
    String id,
    float frameDuration,
    String displayName,
    Integer damage,
    int numberOfBulletsMin,
    int numberOfBulletsMax,
    int engineConsumption,
    int actionPointsConsumption,
    float bulletSpeed,
    Color bulletLightColor,
    boolean lightOnCreation,
    boolean melee,
    float duration
) implements ItemDeclaration {

    @Override
    public String displayName( ) {
        return displayName;
    }

    @Override
    public String name( ) {
        return displayName;
    }


    @Override
    public int getSymbolWidth( ) {
        return 0;
    }

    @Override
    public String getId( ) {
        return id;
    }

    @Override
    public int[] getMask( ) {
        return new int[0];
    }

    @Override
    public int getSymbolHeight( ) {
        return 0;
    }

    @Override
    public UiTextures getSymbol( ) {
        return null;
    }


    @Override
    public Models getModelDefinition( ) {
        return null;
    }

    @Override
    public String toString( ) {
        return displayName;
    }
}
