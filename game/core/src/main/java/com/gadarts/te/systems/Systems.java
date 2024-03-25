package com.gadarts.te.systems;

import com.gadarts.te.systems.map.MapSystem;
import com.gadarts.te.systems.render.RenderSystem;
import com.gadarts.te.systems.ui.InterfaceSystem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Getter
public enum Systems {
    RENDER(new RenderSystem()),
    MAP(new MapSystem()),
    CAMERA(new CameraSystem()),
    INTERFACE(new InterfaceSystem());

    private final GameSystem instance;

}
