package com.gadarts.te.common.map;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.gadarts.te.common.WallObjects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class EnvObject {
    private final Coords coords;
    private final WallObjects definition;
    private final ModelInstance modelInstance;
}
