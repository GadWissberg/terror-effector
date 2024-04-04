package com.gadarts.te.systems.ui;

import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.gadarts.te.common.assets.GameAssetsManager;
import com.gadarts.te.common.assets.texture.UiTextures;
import com.gadarts.te.common.map.MapUtils;
import com.gadarts.te.common.utils.GeneralUtils;
import com.gadarts.te.systems.GameSystem;
import com.gadarts.te.systems.data.SharedData;
import com.gadarts.te.systems.data.SharedDataBuilder;

public class InterfaceSystem extends GameSystem {
    private CursorHandler cursorHandler;
    @SuppressWarnings("FieldCanBeLocal")
    private Stage uiStage;
    private final Model floorModel = MapUtils.createFloorModel();

    @Override
    public void initialize(SharedDataBuilder sharedDataBuilder, GameAssetsManager assetsManager, MessageDispatcher eventDispatcher) {
        super.initialize(sharedDataBuilder, assetsManager, eventDispatcher);
        uiStage = new Stage();
        cursorHandler = new CursorHandler(uiStage, eventDispatcher);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        cursorHandler.update();
    }

    @Override
    public void onSystemReady(SharedData sharedData) {
        super.onSystemReady(sharedData);
        cursorHandler.init(
            assetsManager.getTexture(UiTextures.NODE_CURSOR),
            getEngine(),
            floorModel,
            sharedData.mapGraph(),
            sharedData.camera());
    }

    @Override
    public void dispose( ) {
        GeneralUtils.disposeObject(this, InterfaceSystem.class);
    }
}
