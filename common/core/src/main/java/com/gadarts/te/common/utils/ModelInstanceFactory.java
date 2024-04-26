package com.gadarts.te.common.utils;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.gadarts.te.common.assets.GameAssetsManager;
import com.gadarts.te.common.assets.model.ModelDeclaration;

import java.util.Optional;

@SuppressWarnings("GDXJavaUnsafeIterator")
public final class ModelInstanceFactory {
    private static void applyExplicitModelTexture(ModelDeclaration modelDefinition,
												  ModelInstance modelInstance,
												  GameAssetsManager assetsManager) {
        Optional.ofNullable(modelDefinition.getTextureFileName()).ifPresent(t -> {
            for (Material material : modelInstance.materials) {
                if (material.has(TextureAttribute.Diffuse)) {
                    TextureAttribute attribute = (TextureAttribute) material.get(TextureAttribute.Diffuse);
                    attribute.textureDescription.texture = assetsManager.getModelExplicitTexture(modelDefinition);
                }
            }
        });
    }

    public static ModelInstance create(GameAssetsManager assetsManager,
                                       ModelDeclaration modelDefinition) {
        ModelInstance modelInstance = new ModelInstance(assetsManager.getModel(modelDefinition));
        applyExplicitModelTexture(modelDefinition, modelInstance, assetsManager);
        return modelInstance;
    }
}
