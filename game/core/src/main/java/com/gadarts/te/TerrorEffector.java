package com.gadarts.te;

import com.badlogic.gdx.Game;

public class TerrorEffector extends Game {
    public static final int FULL_SCREEN_RES_WIDTH = 1920;
    public static final int FULL_SCREEN_RES_HEIGHT = 1080;
    public static final int WINDOWED_RES_WIDTH = 1280;
    public static final int WINDOWED_RES_HEIGHT = 960;

    @Override
    public void create( ) {
        setScreen(new InGameScreen());
    }
}
