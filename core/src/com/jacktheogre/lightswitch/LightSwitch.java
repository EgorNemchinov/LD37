package com.jacktheogre.lightswitch;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jacktheogre.lightswitch.screens.*;
import com.jacktheogre.lightswitch.tools.ColorLoader;

public class LightSwitch extends Game {

    public SpriteBatch batch;
    public static final int WIDTH = 400;
    public static final int HEIGHT = 240;
    private static boolean playingHuman = true;

    public enum State {
        SINGLEPLAYER, MULTIPLAYER
    }
    private static State state;


    @Override
	public void create () {
		batch = new SpriteBatch();
        ColorLoader.load();
        this.setScreen(new MainMenuScreen(this));
        state = State.SINGLEPLAYER;
	}

	@Override
	public void render () {
		super.render();
	}

    public static boolean isPlayingHuman() {
        return playingHuman;
    }

    public static void setPlayingHuman(boolean playingHuman) {
        LightSwitch.playingHuman = playingHuman;
    }

    public static State getState() {
        return state;
    }

    public static void setState(State state) {
        LightSwitch.state = state;
    }
}
