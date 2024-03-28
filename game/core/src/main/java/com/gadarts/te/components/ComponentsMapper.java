package com.gadarts.te.components;

import com.badlogic.ashley.core.ComponentMapper;
import com.gadarts.te.components.cd.CharacterDecalComponent;
import com.gadarts.te.components.character.CharacterComponent;

public final class ComponentsMapper {
    public static final ComponentMapper<ModelInstanceComponent> modelInstance = ComponentMapper.getFor(ModelInstanceComponent.class);
    public static final ComponentMapper<FloorComponent> floor = ComponentMapper.getFor(FloorComponent.class);
    public static final ComponentMapper<CharacterComponent> character = ComponentMapper.getFor(CharacterComponent.class);
    public static final ComponentMapper<AnimationComponent> animation = ComponentMapper.getFor(AnimationComponent.class);
    public static final ComponentMapper<CharacterDecalComponent> characterDecal = ComponentMapper.getFor(CharacterDecalComponent.class);
    public static final ComponentMapper<PlayerComponent> player = ComponentMapper.getFor(PlayerComponent.class);
}
