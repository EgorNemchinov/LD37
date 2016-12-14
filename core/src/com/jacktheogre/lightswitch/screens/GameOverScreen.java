package com.jacktheogre.lightswitch.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jacktheogre.lightswitch.LightSwitch;
import com.jacktheogre.lightswitch.tools.AssetLoader;
import com.jacktheogre.lightswitch.tools.Assets;

/**
 * Created by luna on 10.12.16.
 */
public class GameOverScreen implements Screen{

    private LightSwitch game;
    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private Sprite next_level;

    public enum State {WIN, LOSE}
    private State state;

    private Sprite replay;
    private Rectangle bounds;

    public GameOverScreen(LightSwitch game, State state) {
        this.game = game;
        this.state = state;
        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(LightSwitch.WIDTH, LightSwitch.HEIGHT, gameCam);
        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        replay = new Sprite(Assets.getAssetLoader().replay_button);
        replay.setPosition(gamePort.getWorldWidth() / 2 - replay.getWidth() - 5, gamePort.getWorldHeight() / 2 - replay.getHeight() / 2 - 80);
        replay.setScale(2, 2);
        next_level = new Sprite(Assets.getAssetLoader().next_level_button);
        next_level.setPosition(replay.getX()+2*replay.getWidth() + 5, replay.getY());
        next_level.setScale(1);
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
        replay.draw(game.batch);
        if(!Assets.getAssetLoader().isMaxLevel() && state == State.WIN)
            next_level.draw(game.batch);
        switch (state) {
            case WIN:
                Assets.getAssetLoader().font.draw(game.batch, "YOU",
                        gamePort.getWorldWidth() / 2 - 1.5f* AssetLoader.LETTER_WIDTH,
                        gamePort.getWorldHeight() / 2 + 1.2f * AssetLoader.LETTER_HEIGHT);
                Assets.getAssetLoader().font.draw(game.batch, "WIN!",
                        gamePort.getWorldWidth() / 2 - 2f* AssetLoader.LETTER_WIDTH,
                        gamePort.getWorldHeight() / 2);
                break;
            case LOSE:
                Assets.getAssetLoader().font.draw(game.batch, "GAME",
                        gamePort.getWorldWidth() / 2 - 2f* AssetLoader.LETTER_WIDTH,
                        gamePort.getWorldHeight() / 2 + 1.2f * AssetLoader.LETTER_HEIGHT);
                Assets.getAssetLoader().font.draw(game.batch, "OVER",
                        gamePort.getWorldWidth() / 2 - 2f* AssetLoader.LETTER_WIDTH,
                        gamePort.getWorldHeight() / 2);
                break;
        }
        game.batch.end();
        /*ShapeRenderer shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(gameCam.combined);
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.begin();
        shapeRenderer.rect(replay.getBoundingRectangle().getX(), replay.getBoundingRectangle().getY(),
                replay.getBoundingRectangle().getWidth(), replay.getBoundingRectangle().getHeight());
        shapeRenderer.end();*/
    }

    private void handleInput(float dt) {
        if(Gdx.input.justTouched()) {
            Vector3 screenTouch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            Vector3 point = gamePort.unproject(screenTouch.cpy());
            if(replay.getBoundingRectangle().contains(point.x,point.y))
                game.setScreen(new GeneratingScreen(game));
            else if(next_level.getBoundingRectangle().contains(point.x,point.y)) {
                if(!Assets.getAssetLoader().isMaxLevel() && state == State.WIN)
                    Assets.getAssetLoader().nextLevel();
                game.setScreen(new GeneratingScreen(game));
            }

        }
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
