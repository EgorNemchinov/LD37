package com.jacktheogre.lightswitch;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jacktheogre.lightswitch.screens.*;

public class LightSwitch extends Game {

    // TODO: 11.12.16 move camera
    public SpriteBatch batch;
    public static final int WIDTH = 400;
    public static final int HEIGHT = 240;
    private boolean playingHuman = false;

    @Override
	public void create () {
		batch = new SpriteBatch();
		this.setScreen(new MainMenuScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}

    public boolean isPlayingHuman() {
        return playingHuman;
    }

    public void setPlayingHuman(boolean playingHuman) {
        this.playingHuman = playingHuman;
    }
}
