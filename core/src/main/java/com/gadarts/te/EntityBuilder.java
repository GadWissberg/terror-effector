package com.gadarts.te;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.gadarts.te.components.ModelInstanceComponent;

public class EntityBuilder {
    private static final EntityBuilder instance = new EntityBuilder();
    private Engine engine;
    private Entity currentEntity;

    public static EntityBuilder beginBuildingEntity(Engine engine) {
        instance.init(engine);
        return instance;
    }

    private void init(Engine engine) {
        this.engine = engine;
        this.currentEntity = engine.createEntity();
    }

    public EntityBuilder addModelInstanceComponent(ModelInstance modelInstance) {
        return addModelInstanceComponent(modelInstance, Vector3.Zero);
    }

    public EntityBuilder addModelInstanceComponent(ModelInstance modelInstance, Vector3 position) {
        ModelInstanceComponent component = engine.createComponent(ModelInstanceComponent.class);
        if (!position.isZero()) {
            modelInstance.transform.setTranslation(position);
        }
        component.init(modelInstance);
        currentEntity.add(component);
        modelInstance.userData = currentEntity;
        return instance;
    }

    public Entity finishAndAddToEngine( ) {
        engine.addEntity(currentEntity);
        Entity entity = currentEntity;
        instance.reset();
        return entity;
    }

    private void reset( ) {
        engine = null;
        currentEntity = null;
    }
}
