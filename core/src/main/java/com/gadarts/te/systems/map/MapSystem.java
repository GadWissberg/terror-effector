package com.gadarts.te.systems.map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.gadarts.te.EntityBuilder;
import com.gadarts.te.assets.GameAssetsManager;
import com.gadarts.te.assets.textures.SurfaceTextures;
import com.gadarts.te.systems.GameSystem;
import com.gadarts.te.systems.data.SharedDataBuilder;
import com.gadarts.te.systems.map.graph.MapGraph;

public class MapSystem extends GameSystem {
    private static final int TEST_MAP_SIZE = 32;
    private Model floorTileModel;

    @Override
    public void initialize(SharedDataBuilder sharedDataBuilder, GameAssetsManager assetsManager) {
        ModelBuilder modelBuilder = new ModelBuilder();
        floorTileModel = modelBuilder.createRect(
            1, 0, 1,
            1, 0, 0,
            0, 0, 0,
            0, 0, 1,
            0, 1, 0,
            new Material(TextureAttribute.createDiffuse((Texture) null)),
            Usage.Position | Usage.Normal | Usage.TextureCoordinates);
        for (int z = 0; z < TEST_MAP_SIZE; z++) {
            for (int x = 0; x < TEST_MAP_SIZE; x++) {
                ModelInstance modelInstance = new ModelInstance(floorTileModel);
                Material attributes = modelInstance.materials.get(0);
                TextureAttribute textureAttribute = (TextureAttribute) attributes.get(TextureAttribute.Diffuse);
                textureAttribute.textureDescription.texture = assetsManager.getTexture(SurfaceTextures.INDUSTRIAL_FLOOR_0);
                EntityBuilder.beginBuildingEntity(getEngine())
                    .addModelInstanceComponent(modelInstance, new Vector3(x, 0F, z))
                    .finishAndAddToEngine();
            }
        }
        MapGraph graph = new MapGraph(TEST_MAP_SIZE);
    }

    @Override
    public void dispose( ) {
        floorTileModel.dispose();
    }
}
