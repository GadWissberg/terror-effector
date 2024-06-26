package com.gadarts.te.common.utils;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import squidpony.squidmath.Bresenham;
import squidpony.squidmath.Coord3D;

import java.util.ArrayDeque;

public final class CameraUtils {
    public static final int CAMERA_HEIGHT = 16;
    private static final int RESOLUTION_FACTOR = 75;
    private static final Vector3 auxVector3 = new Vector3();
    private static final Plane floorPlane = new Plane(new Vector3(0, 1, 0), 0);

    public static OrthographicCamera createCamera(int viewportWidth, int viewportHeight) {
        OrthographicCamera cam = new OrthographicCamera(
            (float) viewportWidth / RESOLUTION_FACTOR,
            (float) viewportHeight / RESOLUTION_FACTOR);
        cam.near = 0.01f;
        cam.far = 100f;
        cam.position.set(9.0F, CAMERA_HEIGHT, 9.0F);
        cam.direction.rotate(Vector3.X, -55.0F);
        cam.direction.rotate(Vector3.Y, 45.0F);
        return cam;
    }

    public static ArrayDeque<Coord3D> findAllCoordsOnRay(int screenX, int screenY,
                                                         float viewportX, float viewportY,
                                                         float viewportWidth, float viewportHeight,
                                                         Camera camera) {
        Ray ray = camera.getPickRay(screenX, screenY, viewportX, viewportY, viewportWidth, viewportHeight);
        Vector3 intersection = auxVector3;
        Intersector.intersectRayPlane(ray, floorPlane, intersection);
        return (ArrayDeque<Coord3D>) Bresenham.line3D(
            (int) ray.origin.x, (int) ray.origin.y, (int) ray.origin.z,
            (int) intersection.x, 0, (int) intersection.z);
    }
}
