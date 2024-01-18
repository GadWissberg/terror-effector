package com.gadarts.te.common.map;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.gadarts.te.common.assets.texture.SurfaceTextures;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Wall {

    @Setter(AccessLevel.NONE)
    private final ModelInstance modelInstance;
    private SurfaceTextures definition;
    private Float vScale;
    private Float hOffset;
    private Float vOffset;

    public Wall(ModelInstance modelInstance, SurfaceTextures definition) {
        this.modelInstance = modelInstance;
        this.definition = definition;
    }
}
