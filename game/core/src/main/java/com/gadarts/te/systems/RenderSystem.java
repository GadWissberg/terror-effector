package com.gadarts.te.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.utils.ScreenUtils;
import com.gadarts.te.common.assets.GameAssetsManager;
import com.gadarts.te.components.ComponentsMapper;
import com.gadarts.te.components.ModelInstanceComponent;
import com.gadarts.te.systems.data.SharedData;
import com.gadarts.te.systems.data.SharedDataBuilder;
import com.gadarts.te.systems.render.AxisModelHandler;

public class RenderSystem extends GameSystem {
    private AxisModelHandler axisModelHandler;
    private ImmutableArray<Entity> modelEntities;
    private ModelBatch modelBatch;


    @Override
    public void initialize(SharedDataBuilder sharedDataBuilder, GameAssetsManager assetsManager) {
        axisModelHandler = new AxisModelHandler();
        Engine engine = getEngine();
        axisModelHandler.addAxis(engine);
        modelEntities = engine.getEntitiesFor(Family.all(ModelInstanceComponent.class).get());
        modelBatch = new ModelBatch();
    }

    @Override
    public void onSystemReady(SharedData sharedData) {
        super.onSystemReady(sharedData);
    }

    @Override
    public void update(float deltaTime) {
        ScreenUtils.clear(Color.BLACK, true);
        modelBatch.begin(sharedData.camera());
        for (Entity entity : modelEntities) {
            ModelInstanceComponent modelInstanceComponent = ComponentsMapper.modelInstance.get(entity);
            ModelInstance modelInstance = modelInstanceComponent.getModelInstance();
            modelBatch.render(modelInstance);
        }
        modelBatch.end();
    }


    @Override
    public void dispose( ) {
        axisModelHandler.dispose();
        modelBatch.dispose();
    }
}
