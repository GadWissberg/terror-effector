package com.gadarts.te.common.utils;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.gadarts.te.common.assets.GameAssetsManager;
import com.gadarts.te.common.assets.declarations.env.EnvObjectDeclaration;
import com.gadarts.te.common.assets.model.Models;
import com.gadarts.te.common.map.Coords;
import com.gadarts.te.common.map.element.Direction;

public final class EnvObjectUtils {

    public static ModelInstance createModelInstanceForEnvObject(GameAssetsManager assetsManager,
                                                                Coords coords,
                                                                float height,
                                                                EnvObjectDeclaration definition,
                                                                Direction direction) {
        Models modelDefinition = definition.modelDefinition();
        ModelInstance modelInstance = ModelInstanceFactory.create(assetsManager, modelDefinition);
        Models.ModelOffset modelOffset = modelDefinition.getModelOffset();
        modelInstance.transform.setTranslation(
            new Vector3(
                ((float) coords.getX()) + 0.5F,
                height + modelOffset.y(),
                ((float) coords.getZ()) + 0.5F
            )
        );
        modelInstance.transform.rotate(Vector3.Y, direction.getDirection(new Vector2()).angleDeg());
        modelInstance.transform.translate(modelOffset.x(), 0F, modelOffset.z());
        return modelInstance;
    }

}
