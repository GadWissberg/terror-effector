package com.gadarts.te.systems.render;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.utils.ScreenUtils;
import com.gadarts.te.common.assets.GameAssetsManager;
import com.gadarts.te.common.utils.LightUtils;
import com.gadarts.te.components.ComponentsMapper;
import com.gadarts.te.components.ModelInstanceComponent;
import com.gadarts.te.systems.GameSystem;
import com.gadarts.te.systems.data.SharedData;
import com.gadarts.te.systems.data.SharedDataBuilder;

public class RenderSystem extends GameSystem {
    private AxisModelHandler axisModelHandler;
    private ImmutableArray<Entity> modelEntities;
    private ModelBatch modelBatch;
    private Environment environment;


    @Override
    public void initialize(SharedDataBuilder sharedDataBuilder, GameAssetsManager assetsManager) {
        axisModelHandler = new AxisModelHandler();
        Engine engine = getEngine();
        axisModelHandler.addAxis(engine);
        modelEntities = engine.getEntitiesFor(Family.all(ModelInstanceComponent.class).get());
        modelBatch = new ModelBatch();
        environment = LightUtils.createEnvironment();
    }

    @Override
    public void onSystemReady(SharedData sharedData) {
        super.onSystemReady(sharedData);
    }

    @Override
    public void update(float deltaTime) {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        ScreenUtils.clear(Color.BLACK, true);
        modelBatch.begin(sharedData.getCamera());
        for (Entity entity : modelEntities) {
            ModelInstanceComponent modelInstanceComponent = ComponentsMapper.modelInstance.get(entity);
            ModelInstance modelInstance = modelInstanceComponent.getModelInstance();
            if (modelInstanceComponent.isApplyEnvironment()) {
                modelBatch.render(modelInstance, environment);
            } else {
                modelBatch.render(modelInstance);
            }
        }
        modelBatch.end();
    }


    @Override
    public void dispose( ) {
        axisModelHandler.dispose();
        modelBatch.dispose();
    }
}
