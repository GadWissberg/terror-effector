package com.gadarts.te.systems;

import com.gadarts.te.systems.map.MapSystem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Getter
public enum Systems {
    RENDER(new RenderSystem()),
    MAP(new MapSystem()),
    CAMERA(new CameraSystem());

    private final GameSystem instance;

}
