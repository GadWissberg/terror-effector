package com.gadarts.te.systems.ui;

import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.gadarts.te.SoundPlayer;
import com.gadarts.te.common.assets.GameAssetsManager;
import com.gadarts.te.common.assets.texture.UiTextures;
import com.gadarts.te.common.map.MapUtils;
import com.gadarts.te.common.utils.GeneralUtils;
import com.gadarts.te.systems.GameSystem;
import com.gadarts.te.systems.data.GameSessionData;
import com.gadarts.te.systems.data.SharedDataBuilder;

public class InterfaceSystem extends GameSystem {
    private CursorHandler cursorHandler;
    @SuppressWarnings("FieldCanBeLocal")
    private final Model floorModel = MapUtils.createFloorModel();

    @Override
    public void initialize(SharedDataBuilder sharedDataBuilder,
                           GameAssetsManager assetsManager,
                           MessageDispatcher eventDispatcher,
                           SoundPlayer soundPlayer) {
        super.initialize(sharedDataBuilder, assetsManager, eventDispatcher, soundPlayer);
        Stage uiStage = new Stage();
        sharedDataBuilder.setUiStage(uiStage);
        cursorHandler = new CursorHandler(uiStage, eventDispatcher);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        cursorHandler.update();
        sessionData.uiStage().act(deltaTime);
    }

    @Override
    public void onSystemReady(GameSessionData gameSessionData) {
        super.onSystemReady(gameSessionData);
        cursorHandler.init(
            assetsManager.getTexture(UiTextures.NODE_CURSOR),
            getEngine(),
            floorModel,
            gameSessionData.mapGraph(),
            gameSessionData.camera());
    }

    @Override
    public void dispose( ) {
        GeneralUtils.disposeObject(this, InterfaceSystem.class);
    }
}
