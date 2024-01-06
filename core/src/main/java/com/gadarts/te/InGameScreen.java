package com.gadarts.te;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Screen;
import com.gadarts.te.systems.GameSystem;
import com.gadarts.te.systems.Systems;
import com.gadarts.te.systems.data.SharedData;
import com.gadarts.te.systems.data.SharedDataBuilder;

import java.util.Arrays;

public class InGameScreen implements Screen {
    private PooledEngine engine;

    @Override
    public void show( ) {
        engine = new PooledEngine();
        SharedDataBuilder sharedDataBuilder = new SharedDataBuilder();
        Arrays.stream(Systems.values()).forEach(system -> {
            GameSystem instance = system.getInstance();
            engine.addSystem(instance);
            instance.initialize(sharedDataBuilder);
        });
        SharedData sharedData = sharedDataBuilder.build();
        Arrays.stream(Systems.values()).forEach(system -> system.getInstance().onSystemReady(sharedData));
    }

    @Override
    public void render(float delta) {
        engine.update(delta);
    }

    @Override
    public void resize(int width, int height) {
        // Resize your screen here. The parameters represent the new window size.
    }

    @Override
    public void pause( ) {
        // Invoked when your application is paused.
    }

    @Override
    public void resume( ) {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide( ) {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose( ) {
        Arrays.stream(Systems.values()).forEach(system -> system.getInstance().dispose());
    }
}
