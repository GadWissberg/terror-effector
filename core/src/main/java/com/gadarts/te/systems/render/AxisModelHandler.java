package com.gadarts.te.systems.render;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.gadarts.te.EntityBuilder;

public class AxisModelHandler implements Disposable {
    private static final Vector3 auxVector3_1 = new Vector3();
    private static final Vector3 auxVector3_2 = new Vector3();
    private Model axisModelX;
    private Model axisModelY;
    private Model axisModelZ;

    public void addAxis(Engine engine) {
        ModelBuilder modelBuilder = new ModelBuilder();
        axisModelX = createAndAddAxisModel(engine, modelBuilder, auxVector3_2.set(1f, 0f, 0f));
        axisModelY = createAndAddAxisModel(engine, modelBuilder, auxVector3_2.set(0f, 1f, 0f));
        axisModelZ = createAndAddAxisModel(engine, modelBuilder, auxVector3_2.set(0f, 0f, 1f));
    }

    private Model createAndAddAxisModel(Engine engine, ModelBuilder modelBuilder, Vector3 vector) {
        Model axisModel = createAxisModel(modelBuilder, vector, new Color(vector.x, vector.y, vector.z, 1f));
        ModelInstance axisModelInstanceX = new ModelInstance(axisModel);
        EntityBuilder.beginBuildingEntity(engine)
            .addModelInstanceComponent(axisModelInstanceX)
            .finishAndAddToEngine();
        return axisModel;
    }

    private Model createAxisModel(ModelBuilder modelBuilder, Vector3 dir, Color color) {
        return modelBuilder.createArrow(
            auxVector3_1.setZero(),
            dir,
            new Material(ColorAttribute.createDiffuse(color)),
            (VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal)
        );
    }


    public void dispose( ) {
        axisModelX.dispose();
        axisModelY.dispose();
        axisModelZ.dispose();
    }
}
