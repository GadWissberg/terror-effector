package com.gadarts.te.common.map;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.gadarts.te.common.assets.texture.SurfaceTextures;
import com.gadarts.te.common.map.element.EnvObject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

import static com.badlogic.gdx.math.Matrix4.M13;

@Getter
@Setter
public class MapNodeData {

    public static final float MAX_FLOOR_HEIGHT = 5F;
    private final static Vector3 auxVector = new Vector3();

    @Setter(AccessLevel.NONE)
    private Coords coords;

    @Setter(AccessLevel.NONE)
    private NodeWalls walls = new NodeWalls();

    private ModelInstance modelInstance;
    private MapNodesTypes mapNodeType;
    private SurfaceTextures textureDefinition;
    private float height;
    private ArrayList<EnvObject> envObjects = new ArrayList<>();

    public MapNodeData(int x, int z, MapNodesTypes type, ModelInstance modelInstance, SurfaceTextures textureDefinition) {
        this.mapNodeType = type;
        this.coords = new Coords(x, z);
        this.textureDefinition = textureDefinition;
        initializeModelInstance(modelInstance);
    }

    public void initializeModelInstance(final ModelInstance modelInstance) {
        this.modelInstance = modelInstance;
        Material material = modelInstance.materials.get(0);
        material.remove(ColorAttribute.Diffuse);
        modelInstance.transform.setTranslation(coords.getX() + 0.5F, 0, coords.getZ() + 0.5F);
    }

    @Override
    public String toString( ) {
        return "MapNodeData{" +
            "coords=" + coords +
            '}';
    }

    public void applyHeight(float value) {
        value = MathUtils.clamp(value, 0F, MAX_FLOOR_HEIGHT);

        height = value;
        if (modelInstance != null) {
            modelInstance.transform.val[M13] = value;
        }
    }

    public boolean equals(final int row, final int col) {
        return coords.equals(row, col);
    }

}
