package com.gadarts.te.common.map.element;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.gadarts.te.common.WallObjects;
import com.gadarts.te.common.map.Coords;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class EnvObject {
    private final Coords coords;
    private final WallObjects definition;
    private final ModelInstance modelInstance;
    private final Direction direction;
}
