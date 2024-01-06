package com.gadarts.te.systems;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Getter
public enum Systems {
    RENDER(new RenderSystem()),
    CAMERA(new CameraSystem());

    private final GameSystem instance;

}
