package com.gadarts.te.systems.data;

import com.badlogic.gdx.graphics.OrthographicCamera;
import lombok.Getter;

@Getter
public class SharedData {
    SharedData(OrthographicCamera camera) {
        this.camera = camera;
    }

    private final OrthographicCamera camera;
}
