package com.gadarts.te.common;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

public final class CameraUtils {
    private static final int RESOLUTION_FACTOR = 75;

    public static OrthographicCamera createCamera(int viewportWidth, int viewportHeight) {
        OrthographicCamera cam = new OrthographicCamera(
            (float) viewportWidth / RESOLUTION_FACTOR,
            (float) viewportHeight / RESOLUTION_FACTOR);
        cam.near = 0.01f;
        cam.far = 100f;
        cam.position.set(9.0F, 16, 9.0F);
        cam.direction.rotate(Vector3.X, -55.0F);
        cam.direction.rotate(Vector3.Y, 45.0F);
        return cam;
    }
}
