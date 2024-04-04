package com.gadarts.te.systems.ui;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Disposable;
import com.gadarts.te.DebugSettings;
import com.gadarts.te.EntityBuilder;
import com.gadarts.te.common.map.MapNodesTypes;
import com.gadarts.te.common.utils.CameraUtils;
import com.gadarts.te.common.utils.GeneralUtils;
import com.gadarts.te.components.ComponentsMapper;
import com.gadarts.te.components.FloorComponent;
import com.gadarts.te.systems.SystemEvent;
import com.gadarts.te.systems.map.graph.MapGraph;
import com.gadarts.te.systems.map.graph.MapGraphNode;
import squidpony.squidmath.Coord3D;

import java.util.ArrayDeque;

import static com.badlogic.gdx.math.Matrix4.M00;
import static com.badlogic.gdx.math.Matrix4.M22;

public class CursorHandler implements Disposable, InputProcessor {
    private static final Color POSITION_LABEL_COLOR = Color.WHITE;
    private static final String POSITION_LABEL_FORMAT = "X: %s , Z: %s";

    private static final float POSITION_LABEL_Y = 10F;
    private static final Vector3 auxVector3 = new Vector3();
    private static final Vector2 auxVector2 = new Vector2();
    @SuppressWarnings("FieldCanBeLocal")
    private final BitmapFont cursorCellPositionLabelFont;
    private final Label cursorCellPositionLabel;
    private final MessageDispatcher eventDispatcher;
    private ModelInstance cursorModelInstance;
    private MapGraph mapGraph;
    private OrthographicCamera camera;
    private float cursorAnimation = 0F;
    private boolean cursorAnimationPositive = true;

    public CursorHandler(Stage uiStage, MessageDispatcher eventDispatcher) {
        if (DebugSettings.DISPLAY_CURSOR_POSITION) {
            cursorCellPositionLabelFont = new BitmapFont();
            Label.LabelStyle style = new Label.LabelStyle(cursorCellPositionLabelFont, POSITION_LABEL_COLOR);
            cursorCellPositionLabel = new Label(null, style);
            uiStage.addActor(cursorCellPositionLabel);
            cursorCellPositionLabel.setPosition(0, POSITION_LABEL_Y);
        }
        this.eventDispatcher = eventDispatcher;
    }

    @Override
    public void dispose( ) {
        GeneralUtils.disposeObject(this, CursorHandler.class);
    }

    public void init(Texture texture, Engine engine, Model floorModel, MapGraph mapGraph, OrthographicCamera camera) {
        ModelInstance cursorModelInstance = new ModelInstance(floorModel);
        this.cursorModelInstance = cursorModelInstance;
        this.mapGraph = mapGraph;
        this.camera = camera;
        TextureAttribute textureAttribute = TextureAttribute.createDiffuse(texture);
        Material material = cursorModelInstance.materials.get(0);
        material.set(textureAttribute);
        material.set(new BlendingAttribute(0.75F));
        EntityBuilder.beginBuildingEntity(engine)
            .addModelInstanceComponent(cursorModelInstance, false)
            .finishAndAddToEngine();
        InputMultiplexer multiplexer = (InputMultiplexer) Gdx.input.getInputProcessor();
        multiplexer.addProcessor(this);
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
        boolean handled = false;
        if (button == Input.Buttons.LEFT) {
            Vector3 position = cursorModelInstance.transform.getTranslation(auxVector3);
            eventDispatcher.dispatchMessage(SystemEvent.USER_CLICKED_NODE.ordinal(), auxVector2.set((int) position.x, (int) position.z));
            handled = true;
        }
        return handled;
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
        return false;
    }

    private MapGraphNode calculateNewNode(int screenX, int screenY) {
        ArrayDeque<Coord3D> nodes = CameraUtils.findAllCoordsOnRay(
            screenX, screenY,
            0F, 0F,
            Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
            camera);
        return findNearestNodeOnCameraLineOfSight(nodes);
    }

    private MapGraphNode findNearestNodeOnCameraLineOfSight(ArrayDeque<Coord3D> nodes) {
        for (Coord3D coord : nodes) {
            MapGraphNode node = mapGraph.getNode(coord.x, coord.z);
            if (node != null && (coord.getY() <= node.getHeight() || coord.y == 0) && node.getEntity() != null) {
                FloorComponent floorComponent = ComponentsMapper.floor.get(node.getEntity());
                MapNodesTypes nodeType = floorComponent.getNode().getType();
                if (nodeType == MapNodesTypes.PASSABLE_NODE) {
                    return node;
                }
            }
        }
        return null;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        MapGraphNode newNode = calculateNewNode(screenX, screenY);
        Vector3 translation = cursorModelInstance.transform.getTranslation(auxVector3);
        MapGraphNode oldNode = mapGraph.getNode((int) translation.x, (int) translation.z);
        if (newNode != null && !newNode.equals(oldNode)) {
            int x = newNode.getX();
            int z = newNode.getZ();
            cursorModelInstance.transform.setTranslation(x + 0.5f, newNode.getHeight() + 0.01F, z + 0.5f);
            if (cursorCellPositionLabel != null) {
                cursorCellPositionLabel.setText(String.format(POSITION_LABEL_FORMAT, x, z));
            }
        }
        return true;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    public void update( ) {
        float alpha = MathUtils.sin(cursorAnimation);
        float apply = Interpolation.circle.apply(alpha);
        cursorModelInstance.transform.val[M00] = apply;
        cursorModelInstance.transform.val[M22] = apply;
        cursorAnimation += (cursorAnimationPositive ? 1 : -1) * 0.005F;
        if (cursorAnimation >= 1F) {
            cursorAnimationPositive = false;
        } else if (cursorAnimation <= 0.8F) {
            cursorAnimationPositive = true;
        }
    }
}
