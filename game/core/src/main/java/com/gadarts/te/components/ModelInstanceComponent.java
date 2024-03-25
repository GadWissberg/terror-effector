package com.gadarts.te.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.utils.Pool;
import lombok.Getter;

@Getter
public class ModelInstanceComponent implements Component, Pool.Poolable {
    private ModelInstance modelInstance;
    private boolean applyEnvironment;

    @Override
    public void reset( ) {

    }

    public void init(ModelInstance modelInstance, boolean applyEnvironment) {
        this.modelInstance = modelInstance;
        this.applyEnvironment = applyEnvironment;
    }
}
