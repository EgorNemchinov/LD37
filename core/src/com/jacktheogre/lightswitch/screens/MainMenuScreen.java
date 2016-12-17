package com.jacktheogre.lightswitch.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jacktheogre.lightswitch.LightSwitch;
import com.jacktheogre.lightswitch.sprites.Button;
import com.jacktheogre.lightswitch.tools.AssetLoader;
import com.jacktheogre.lightswitch.tools.Assets;
import com.jacktheogre.lightswitch.tools.MainMenuInputHandler;

/**
 * Created by luna on 10.12.16.
 */
public class MainMenuScreen implements Screen{
    private final Color BACKGROUND_COLOR = new Color(31/255f, 24/255f, 44/255f, 1f);

    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private LightSwitch game;
    private Button play_button;
    private AssetLoader loader;

    public MainMenuScreen(LightSwitch game) {
        this.game = game;
        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(LightSwitch.WIDTH, LightSwitch.HEIGHT, gameCam);
        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        loader = Assets.getAssetLoader();
        loader.load();
//        loader.nullifyLevel();

//        RectangleMapObject rectangleMapObject = new RectangleMapObject();
        MapObject object = loader.getMap().getLayers().get(3).getObjects().get(0);
        if(ClassReflection.isInstance(RectangleMapObject.class, object))
//            rectangleMapObject = (RectangleMapObject) object;

        play_button = new Button(Assets.getAssetLoader().play_button, Button.State.ACTIVE);
        play_button.setPosition(gamePort.getWorldWidth() / 2 - play_button.getWidth() / 2, gamePort.getWorldHeight() / 2 - play_button.getHeight() / 2);
        play_button.setScale(1);

        Gdx.input.setInputProcessor(new MainMenuInputHandler(this));
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(BACKGROUND_COLOR.r,BACKGROUND_COLOR.g, BACKGROUND_COLOR.b, BACKGROUND_COLOR.a);
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

    public Button getPlayButton() {
        return play_button;
    }

    public LightSwitch getGame() {
        return game;
    }

    public Viewport getGamePort() {
        return gamePort;
    }
}
