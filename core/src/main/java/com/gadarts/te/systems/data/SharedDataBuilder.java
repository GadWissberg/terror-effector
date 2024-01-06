package com.gadarts.te.systems.data;

import com.badlogic.gdx.graphics.OrthographicCamera;
import lombok.Setter;

@Setter
public class SharedDataBuilder {
    private OrthographicCamera camera;

    public SharedData build( ) {
        return new SharedData(camera);
    }
}
