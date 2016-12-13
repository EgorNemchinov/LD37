package com.jacktheogre.lightswitch.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jacktheogre.lightswitch.LightSwitch;
import com.jacktheogre.lightswitch.tools.AssetLoader;
import com.jacktheogre.lightswitch.tools.Assets;

/**
 * Created by luna on 10.12.16.
 */
public class MainMenuScreen implements Screen{
    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private LightSwitch game;
    private Sprite play_button;
    private AssetLoader loader;

    public MainMenuScreen(LightSwitch game) {
        this.game = game;
        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(LightSwitch.WIDTH, LightSwitch.HEIGHT, gameCam);
        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        loader = Assets.getAssetLoader();
        loader.load();

        play_button = new Sprite(Assets.getAssetLoader().play_button);
        play_button.setPosition(gamePort.getWorldWidth() / 2 - play_button.getWidth() / 2, gamePort.getWorldHeight() / 2 - play_button.getHeight() / 2);
        play_button.setScale(1);
    }

    public void handleInput(float dt) {
        if(Gdx.input.justTouched()) {
            Vector3 screenTouch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            Vector3 point = gamePort.unproject(screenTouch.cpy());
            if(play_button.getBoundingRectangle().contains(point.x,point.y))
                game.setScreen(new GeneratingScreen(game));
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        handleInput(delta);
        Gdx.gl.glClearColor(0f, 0f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        play_button.draw(game.batch);
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
