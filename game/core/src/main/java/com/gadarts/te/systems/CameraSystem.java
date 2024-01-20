package com.gadarts.te.systems;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.gadarts.te.common.CameraUtils;
import com.gadarts.te.common.assets.GameAssetsManager;
import com.gadarts.te.systems.data.SharedData;
import com.gadarts.te.systems.data.SharedDataBuilder;

import static com.gadarts.te.DebugSettings.FULL_SCREEN;
import static com.gadarts.te.TerrorEffector.*;

public class CameraSystem extends GameSystem implements InputProcessor {

    private final Vector2 lastRightPressMousePosition = new Vector2();

    @Override
    public void initialize(SharedDataBuilder sharedDataBuilder, GameAssetsManager assetsManager) {
        int viewportWidth = (FULL_SCREEN ? FULL_SCREEN_RES_WIDTH : WINDOWED_RES_WIDTH);
        int viewportHeight = (FULL_SCREEN ? FULL_SCREEN_RES_HEIGHT : WINDOWED_RES_HEIGHT);
        OrthographicCamera cam = CameraUtils.createCamera(viewportWidth, viewportHeight);
        sharedDataBuilder.setCamera(cam);
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
