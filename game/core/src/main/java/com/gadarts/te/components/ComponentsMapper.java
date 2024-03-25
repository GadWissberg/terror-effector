package com.gadarts.te.components;

import com.badlogic.ashley.core.ComponentMapper;

public final class ComponentsMapper {
    public static final ComponentMapper<ModelInstanceComponent> modelInstance = ComponentMapper.getFor(ModelInstanceComponent.class);
    public static final ComponentMapper<FloorComponent> floor = ComponentMapper.getFor(FloorComponent.class);
}
