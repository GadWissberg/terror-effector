package com.gadarts.te.common;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

public final class CameraUtils {

    public static void positionCamera(OrthographicCamera cam) {
        cam.near = 0.01f;
        cam.far = 100f;
        cam.position.set(9.0F, 16, 9.0F);
        cam.direction.rotate(Vector3.X, -55.0F);
        cam.direction.rotate(Vector3.Y, 45.0F);
    }
}
