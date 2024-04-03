package com.gadarts.te.common.utils;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.gadarts.te.common.assets.GameAssetsManager;
import com.gadarts.te.common.assets.model.Models;
import com.gadarts.te.common.definitions.env.EnvObjectDefinition;
import com.gadarts.te.common.definitions.env.EnvObjectType;
import com.gadarts.te.common.map.Coords;
import com.gadarts.te.common.map.element.Direction;

public final class EnvObjectUtils {

    public static ModelInstance createModelInstanceForEnvObject(GameAssetsManager assetsManager,
                                                                Coords coords,
                                                                float height,
                                                                EnvObjectDefinition definition,
                                                                Direction direction) {
        Models modelDefinition = definition.getModelDefinition();
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

    public static EnvObjectDefinition fromString(String name) {
        return EnvObjectType.allDefinitions.stream().filter(definition -> definition.name().equals(name)).findFirst().orElse(null);
    }
}
