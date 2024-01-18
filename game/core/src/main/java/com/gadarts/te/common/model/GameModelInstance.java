package com.gadarts.te.common.model;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import lombok.Getter;

@Getter
public class GameModelInstance extends ModelInstance {

    public GameModelInstance(Model model) {
        super(model);
    }


    public GameModelInstance(ModelInstance modelInstance) {
        super(modelInstance);
    }
}
