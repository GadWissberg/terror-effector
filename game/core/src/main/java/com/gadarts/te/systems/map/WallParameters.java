package com.gadarts.te.systems.map;

import com.gadarts.te.common.assets.texture.SurfaceTextures;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class WallParameters {
    private final float vScale;
    private final float hOffset;
    private final float vOffset;
    private final SurfaceTextures definition;

}
