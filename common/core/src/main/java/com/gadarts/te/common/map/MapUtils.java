package com.gadarts.te.common.map;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

public final class MapUtils {
    private static final Vector3 auxVector3_1 = new Vector3();
    private static final Vector3 auxVector3_2 = new Vector3();
    private static final Vector3 auxVector3_3 = new Vector3();
    private static final Vector3 auxVector3_4 = new Vector3();
    private static final Vector3 auxVector3_5 = new Vector3();

    public static Model createFloorModel( ) {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();

        Material floorMaterial = new Material();
        floorMaterial.id = "floor_material";

        MeshPartBuilder meshPartBuilder = modelBuilder.part("floor",
            GL20.GL_TRIANGLES,
            Usage.Position | Usage.Normal | Usage.TextureCoordinates,
            floorMaterial);

        meshPartBuilder.setUVRange(0, 0, 1, 1);
        final float OFFSET = -0.5f;
        meshPartBuilder.rect(
            auxVector3_4.set(OFFSET, 0, 1 + OFFSET),
            auxVector3_1.set(1 + OFFSET, 0, 1 + OFFSET),
            auxVector3_2.set(1 + OFFSET, 0, OFFSET),
            auxVector3_3.set(OFFSET, 0, OFFSET),
            auxVector3_5.set(0, 1, 0));

        return modelBuilder.end();
    }
}
