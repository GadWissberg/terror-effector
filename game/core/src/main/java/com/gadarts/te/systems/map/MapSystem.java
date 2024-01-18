package com.gadarts.te.systems.map;

import com.gadarts.te.DebugSettings;
import com.gadarts.te.common.assets.GameAssetsManager;
import com.gadarts.te.systems.GameSystem;
import com.gadarts.te.systems.data.SharedDataBuilder;

public class MapSystem extends GameSystem {
    private MapInflater mapInflater;

    @Override
    public void initialize(SharedDataBuilder sharedDataBuilder, GameAssetsManager assetsManager) {
        super.initialize(sharedDataBuilder, assetsManager);
        mapInflater = new MapInflater(assetsManager, getEngine());
        mapInflater.inflate(DebugSettings.TEST_LEVEL.toLowerCase());
    }


    @Override
    public void dispose( ) {
        mapInflater.dispose();
    }
}
