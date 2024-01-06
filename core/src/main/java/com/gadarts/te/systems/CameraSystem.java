package com.gadarts.te.systems;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.gadarts.te.systems.data.SharedData;
import com.gadarts.te.systems.data.SharedDataBuilder;

import static com.gadarts.te.DebugSettings.FULL_SCREEN;
import static com.gadarts.te.TerrorEffector.*;

public class CameraSystem extends GameSystem {

    private static final int RESOLUTION_FACTOR = 75;
    private static final float FAR = 100f;
    private static final float NEAR = 0.01f;
    private static final float START_OFFSET_X = 9.0F;
    private static final float START_OFFSET_Z = 9.0F;
    private static final int CAMERA_HEIGHT = 16;

    @Override
    public void initialize(SharedDataBuilder sharedDataBuilder) {
        int viewportWidth = (FULL_SCREEN ? FULL_SCREEN_RESOLUTION_WIDTH : WINDOWED_RESOLUTION_WIDTH) / RESOLUTION_FACTOR;
        int viewportHeight = (FULL_SCREEN ? FULL_SCREEN_RESOLUTION_HEIGHT : WINDOWED_RESOLUTION_HEIGHT) / RESOLUTION_FACTOR;
        OrthographicCamera cam = new OrthographicCamera(viewportWidth, viewportHeight);
        sharedDataBuilder.setCamera(cam);
        cam.near = NEAR;
        cam.far = FAR;
        cam.position.set(START_OFFSET_X, CAMERA_HEIGHT, START_OFFSET_Z);
        cam.direction.rotate(Vector3.X, -55.0F);
        cam.direction.rotate(Vector3.Y, 45.0F);
    }

    @Override
    public void update(float deltaTime) {
        sharedData.getCamera().update();
    }

    @Override
    public void onSystemReady(SharedData sharedData) {
        super.onSystemReady(sharedData);
    }

    @Override
    public void dispose( ) {

    }
}
