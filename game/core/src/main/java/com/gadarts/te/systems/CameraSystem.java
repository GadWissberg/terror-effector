package com.gadarts.te.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.collision.Ray;
import com.gadarts.te.SoundPlayer;
import com.gadarts.te.common.assets.GameAssetsManager;
import com.gadarts.te.common.utils.CameraUtils;
import com.gadarts.te.components.ComponentsMapper;
import com.gadarts.te.systems.data.GameSessionData;
import com.gadarts.te.systems.data.SharedDataBuilder;
import com.gadarts.te.systems.map.graph.MapGraph;

import static com.gadarts.te.DebugSettings.FULL_SCREEN;
import static com.gadarts.te.TerrorEffector.*;
import static com.gadarts.te.common.utils.CameraUtils.CAMERA_HEIGHT;

public class CameraSystem extends GameSystem implements InputProcessor {

    private static final float EXTRA_LEVEL_PADDING = 16;
    private static final Vector3 auxVector1 = new Vector3();
    private static final Vector3 auxVector2 = new Vector3();
    private static final Vector3 auxVector3 = new Vector3();
    private static final Plane groundPlane = new Plane(new Vector3(0, 1, 0), 0);
    private final Vector2 lastRightPressMousePosition = new Vector2(-1F, -1F);

    @Override
    public void initialize(SharedDataBuilder sharedDataBuilder, GameAssetsManager assetsManager, MessageDispatcher eventDispatcher, SoundPlayer soundPlayer) {
        super.initialize(sharedDataBuilder, assetsManager, eventDispatcher, soundPlayer);
        int viewportWidth = (FULL_SCREEN ? FULL_SCREEN_RES_WIDTH : WINDOWED_RES_WIDTH);
        int viewportHeight = (FULL_SCREEN ? FULL_SCREEN_RES_HEIGHT : WINDOWED_RES_HEIGHT);
        OrthographicCamera cam = CameraUtils.createCamera(viewportWidth, viewportHeight);
        sharedDataBuilder.setCamera(cam);
        InputMultiplexer multiplexer = (InputMultiplexer) Gdx.input.getInputProcessor();
        multiplexer.addProcessor(this);
    }

    @Override
    public void update(float deltaTime) {
        handleCameraFollow();
        sessionData.camera().update();
    }

    private void handleCameraFollow( ) {
        Entity player = sessionData.player();
        Vector3 playerPos = ComponentsMapper.characterDecal.get(player).getDecal().getPosition();
        Camera camera = sessionData.camera();
        Ray ray = camera.getPickRay(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
        groundPlane.d = playerPos.y;
        Intersector.intersectRayPlane(ray, groundPlane, auxVector1);
        Vector3 diff = auxVector2.set(playerPos).sub(auxVector1);
        Vector3 cameraPosDest = auxVector3.set(camera.position).add(diff.x, 0F, diff.z);
        cameraPosDest.y = playerPos.y + CAMERA_HEIGHT;
        camera.position.interpolate(cameraPosDest, 0.1F, Interpolation.bounce);
    }

    @Override
    public void onSystemReady(GameSessionData gameSessionData) {
        super.onSystemReady(gameSessionData);
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
            result = true;
        }
        return result;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        boolean handled = false;
        if (button == Input.Buttons.RIGHT) {
            lastRightPressMousePosition.set(-1F, -1F);
            handled = true;
        }
        return handled;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        boolean handled = false;
        if (!lastRightPressMousePosition.epsilonEquals(-1F, -1F)) {
            Vector3 rotationPoint = ComponentsMapper.characterDecal.get(sessionData.player()).getDecal().getPosition();
            OrthographicCamera camera = sessionData.camera();
            camera.rotateAround(rotationPoint, Vector3.Y, (lastRightPressMousePosition.x - screenX) / 2f);
            MapGraph mapGraph = sessionData.mapGraph();
            camera.position.x = MathUtils.clamp(camera.position.x, -EXTRA_LEVEL_PADDING, mapGraph.getWidth() + EXTRA_LEVEL_PADDING);
            camera.position.z = MathUtils.clamp(camera.position.z, -EXTRA_LEVEL_PADDING, mapGraph.getDepth() + EXTRA_LEVEL_PADDING);
            lastRightPressMousePosition.set(screenX, screenY);
            handled = true;
        }
        return handled;
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
