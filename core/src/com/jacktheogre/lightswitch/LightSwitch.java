package com.jacktheogre.lightswitch;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jacktheogre.lightswitch.screens.*;

public class LightSwitch extends Game {

    // TODO: 11.12.16 move camera
    // TODO: 11.12.16 pathfinding: zelda cuts corners(hi russian)
    // TODO: 11.12.16 spawn-in-the-stone problem
    // TODO: 11.12.16 energy scale
    // TODO: 11.12.16 come up with a story, find artist
    // TODO: 10.12.16 replays
    public SpriteBatch batch;
    public static final int WIDTH = 400;
    public static final int HEIGHT = 240;

    @Override
	public void create () {
		batch = new SpriteBatch();
		this.setScreen(new GeneratingScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
}
