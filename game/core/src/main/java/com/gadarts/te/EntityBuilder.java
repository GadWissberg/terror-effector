package com.gadarts.te;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.gadarts.te.common.assets.texture.SurfaceTextures;
import com.gadarts.te.common.definitions.character.SpriteType;
import com.gadarts.te.common.map.element.Direction;
import com.gadarts.te.components.*;
import com.gadarts.te.components.cd.CharacterAnimation;
import com.gadarts.te.components.cd.CharacterAnimations;
import com.gadarts.te.components.cd.CharacterDecalComponent;
import com.gadarts.te.components.character.CharacterComponent;
import com.gadarts.te.components.character.CharacterSpriteData;
import com.gadarts.te.systems.map.graph.MapGraphNode;

public class EntityBuilder {
    private static final EntityBuilder instance = new EntityBuilder();
    private Engine engine;
    private Entity currentEntity;

    public static EntityBuilder beginBuildingEntity(Engine engine) {
        instance.init(engine);
        return instance;
    }

    public EntityBuilder addCharacterDecalComponent(final CharacterAnimations animations,
                                                    final SpriteType spriteType,
                                                    final Direction direction,
                                                    final Vector3 position) {
        CharacterDecalComponent characterDecalComponent = engine.createComponent(CharacterDecalComponent.class);
        characterDecalComponent.init(animations, spriteType, direction, position);
        currentEntity.add(characterDecalComponent);
        return instance;
    }

    public void addAnimationComponent(CharacterAnimation characterAnimation) {
        AnimationComponent animComponent = engine.createComponent(AnimationComponent.class);
        animComponent.init(characterAnimation.getFrameDuration(), characterAnimation);
        currentEntity.add(animComponent);
    }

    public EntityBuilder addCharacterComponent(CharacterSpriteData characterSpriteData,
                                               Direction direction) {
        CharacterComponent charComponent = engine.createComponent(CharacterComponent.class);
        charComponent.init(characterSpriteData, direction);
        currentEntity.add(charComponent);
        return instance;
    }

    public EntityBuilder addPlayerComponent(CharacterAnimations general) {
        PlayerComponent playerComponent = engine.createComponent(PlayerComponent.class);
        playerComponent.init(general);
        currentEntity.add(playerComponent);
        return instance;
    }

    public EntityBuilder addModelInstanceComponent(ModelInstance modelInstance) {
        return addModelInstanceComponent(modelInstance, Vector3.Zero, true);
    }

    public EntityBuilder addModelInstanceComponent(ModelInstance modelInstance, boolean applyEnvironment) {
        return addModelInstanceComponent(modelInstance, Vector3.Zero, applyEnvironment);
    }

    public EntityBuilder addModelInstanceComponent(ModelInstance modelInstance, Vector3 position, boolean applyEnvironment) {
        ModelInstanceComponent component = engine.createComponent(ModelInstanceComponent.class);
        if (!position.isZero()) {
            modelInstance.transform.setTranslation(position);
        }
        component.init(modelInstance, applyEnvironment);
        currentEntity.add(component);
        modelInstance.userData = currentEntity;
        return instance;
    }

    public Entity finishAndAddToEngine( ) {
        engine.addEntity(currentEntity);
        Entity entity = currentEntity;
        instance.reset();
        return entity;
    }

    public EntityBuilder addWallComponent(final MapGraphNode parentNode) {
        WallComponent component = engine.createComponent(WallComponent.class);
        component.init(parentNode);
        currentEntity.add(component);
        return instance;
    }

    public EntityBuilder addFloorComponent(MapGraphNode node, SurfaceTextures definition) {
        FloorComponent floorComponent = engine.createComponent(FloorComponent.class);
        floorComponent.init(node, definition);
        currentEntity.add(floorComponent);
        return instance;
    }

    private void reset( ) {
        engine = null;
        currentEntity = null;
    }

    private void init(Engine engine) {
        this.engine = engine;
        this.currentEntity = engine.createEntity();
    }
}
