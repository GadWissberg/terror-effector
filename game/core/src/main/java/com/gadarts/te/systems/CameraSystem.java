package com.gadarts.te.systems;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.gadarts.te.common.assets.GameAssetsManager;
import com.gadarts.te.systems.data.SharedData;
import com.gadarts.te.systems.data.SharedDataBuilder;

import static com.gadarts.te.DebugSettings.FULL_SCREEN;
import static com.gadarts.te.TerrorEffector.*;

public class CameraSystem extends GameSystem implements InputProcessor {

    private static final int RESOLUTION_FACTOR = 75;
    private static final float FAR = 100f;
    private static final float NEAR = 0.01f;
    private static final float START_OFFSET_X = 9.0F;
    private static final float START_OFFSET_Z = 9.0F;
    private static final int CAMERA_HEIGHT = 16;
    private final Vector2 lastRightPressMousePosition = new Vector2();

    @Override
    public void initialize(SharedDataBuilder sharedDataBuilder, GameAssetsManager assetsManager) {
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

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        boolean result = false;
        if (button == Input.Buttons.RIGHT) {
            lastRightPressMousePosition.set(screenX, screenY);
            lastRightPressMousePosition.set(screenX, screenY);
            result = true;
        }
        return result;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
//        Entity player = getSystemsCommonData().getPlayer();
//        Vector3 rotationPoint = ComponentsMapper.characterDecal.get(player).getDecal().getPosition();
//        Camera camera = getSystemsCommonData().getCamera();
//        camera.rotateAround(rotationPoint, Vector3.Y, (lastRightPressMousePosition.x - screenX) / 2f);
//        MapGraph map = sharedData.getMapGraph();
//        pos.x = MathUtils.clamp(pos.x, -EXTRA_LEVEL_PADDING, map.getWidth() + EXTRA_LEVEL_PADDING);
//        pos.z = MathUtils.clamp(pos.z, -EXTRA_LEVEL_PADDING, map.getDepth() + EXTRA_LEVEL_PADDING);
//        lastRightPressMousePosition.set(screenX, screenY);
//        return false;
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
