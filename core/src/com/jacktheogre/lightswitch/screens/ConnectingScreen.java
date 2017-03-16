package com.jacktheogre.lightswitch.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.jacktheogre.lightswitch.LightSwitch;
import com.jacktheogre.lightswitch.multiplayer.BluetoothSingleton;
import com.jacktheogre.lightswitch.tools.Assets;
import com.jacktheogre.lightswitch.tools.input.ConnectInputHandler;

/**
 * Created by luna on 13.02.17.
 */

public class ConnectingScreen extends GameScreen {

    public ConnectingScreen(LightSwitch game) {
        super();
        this.game = game;

        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(LightSwitch.WIDTH, LightSwitch.HEIGHT, gameCam);
        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(gameCam.combined);
        shapeRenderer.setAutoShapeType(true);

        Gdx.input.setCatchBackKey(true);
        Gdx.input.setInputProcessor(new ConnectInputHandler(this));
    }

    public void update(float dt) {
        if(BluetoothSingleton.getInstance().getBluetoothManager().isConnected())
            game.setScreen(new GeneratingScreen(game));
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0f, 0f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        Assets.getAssetLoader().font.draw(game.batch, "" + BluetoothSingleton.getInstance().getBluetoothManager().getDevices().length,
                gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2);
        game.batch.end();
    }

    @Override
    protected void initializeButtons() {

    }

    @Override
    public void show() {

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
