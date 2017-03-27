package com.jacktheogre.lightswitch.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jacktheogre.lightswitch.LightSwitch;
import com.jacktheogre.lightswitch.ai.LevelManager;
import com.jacktheogre.lightswitch.objects.InteractiveObject;
import com.jacktheogre.lightswitch.sprites.Button;
import com.jacktheogre.lightswitch.tools.AssetLoader;
import com.jacktheogre.lightswitch.tools.Assets;
import com.jacktheogre.lightswitch.tools.input.GameOverInputHandler;

/**
 * Created by luna on 10.12.16.
 */
public class GameOverScreen extends GameScreen{

    private static final float INTERVAL = 10;
    private static final float SCALE = 1.5f;

    public State getState() {
        return state;
    }



    public enum State {WIN, LOSE;}
    private State state;
    private Button next_level, replay, home;
    private Array<InteractiveObject> interactiveObjects;

    public GameOverScreen(LightSwitch game, State state) {
        super();
        this.game = game;
        this.state = state;
        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(LightSwitch.WIDTH, LightSwitch.HEIGHT, gameCam);
        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);
        initializeButtons();

        Gdx.input.setInputProcessor(new GameOverInputHandler(this));
    }

    public GameOverScreen(LightSwitch game, State state, Array<InteractiveObject> objects) {
        this(game, state);
        this.interactiveObjects = objects;
    }

    public void setInteractiveObjects(Array<InteractiveObject> interactiveObjects) {
        this.interactiveObjects = interactiveObjects;
    }

    @Override
    public void show() {

    }

    public Array<InteractiveObject> getObjects() {
        return interactiveObjects;
    }

    public Viewport getGamePort() {
        return gamePort;
    }

    public LightSwitch getGame() {
        return game;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        switch (state) {
            case WIN:
                if(LevelManager.isMaxLevel()) {
                    Assets.getAssetLoader().font.getData().setScale(1f);
                    Assets.getAssetLoader().font.draw(game.batch, "CONGRATULATIONS!",
                            gamePort.getWorldWidth() / 2 - 7.5f* Assets.getAssetLoader().getLetterWidth(),
                            gamePort.getWorldHeight() / 2);
                    Assets.getAssetLoader().font.getData().setScale(Assets.getAssetLoader().FONT_SCALE);
                } else {

                    Assets.getAssetLoader().font.draw(game.batch, "YOU",
                            gamePort.getWorldWidth() / 2 - 2f* AssetLoader.LETTER_WIDTH,
                            gamePort.getWorldHeight() / 2 + 1.2f * AssetLoader.LETTER_HEIGHT);
                    Assets.getAssetLoader().font.draw(game.batch, "WIN!",
                            gamePort.getWorldWidth() / 2 - 2f* AssetLoader.LETTER_WIDTH,
                            gamePort.getWorldHeight() / 2);
                }
                break;
            case LOSE:
                Assets.getAssetLoader().font.draw(game.batch, "GAME",
                                gamePort.getWorldWidth() / 2 - 2f* AssetLoader.LETTER_WIDTH,
                                gamePort.getWorldHeight() / 2 + 1.3f * AssetLoader.LETTER_HEIGHT);
                Assets.getAssetLoader().font.draw(game.batch, "OVER",
                        gamePort.getWorldWidth() / 2 - 2f* AssetLoader.LETTER_WIDTH,
                        gamePort.getWorldHeight() / 2);
                break;
        }
        renderButtons(gameCam);
        if(game.batch.isDrawing())
            game.batch.end();
        /*ShapeRenderer shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(gameCam.combined);
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.begin();
        shapeRenderer.rect(replay.getBoundingRectangle().getX(), replay.getBoundingRectangle().getY(),
                replay.getBoundingRectangle().getWidth(), replay.getBoundingRectangle().getHeight());
        shapeRenderer.end();*/
    }

    @Override
    protected void initializeButtons() {
        replay = new Button(Assets.getAssetLoader().replay_button, Button.State.ACTIVE, this) {
          @Override
          protected void actUnpress() {
              screen.getGame().setScreen(new GeneratingScreen(screen.getGame(), interactiveObjects));
          }
        };
        replay.setPosition(gamePort.getWorldWidth() / 2 - 2f*replay.getWidth()-INTERVAL, gamePort.getWorldHeight() / 2 - replay.getHeight() / 2 - 80);
        replay.setScale(SCALE);

        home = new Button(Assets.getAssetLoader().home_button, Button.State.ACTIVE, this) {
            @Override
            protected void actUnpress() {
                screen.getGame().setScreen(new MainMenuScreen(screen.getGame()));
            }
        };
        home.setPosition(replay.getBoundingRectangle().getX()+replay.getBoundingRectangle().getWidth() + INTERVAL, replay.getY());
        home.setScale(SCALE);

        boolean nextLevelActive = !LevelManager.isMaxLevel() && state == State.WIN;
        next_level = new Button(Assets.getAssetLoader().next_level_button, nextLevelActive ? Button.State.ACTIVE : Button.State.DISABLED, this) {
            @Override
            protected void actUnpress() {
                LevelManager.nextLevel();
                screen.getGame().setScreen(new GeneratingScreen(screen.getGame()));
            }
        };
        next_level.setPosition(home.getBoundingRectangle().getX()+home.getBoundingRectangle().getWidth() + INTERVAL, home.getY());
        next_level.setScale(SCALE);

        buttons.add(replay);
        buttons.add(next_level);
        buttons.add(home);
    }

    public Button getNext_level() {
        return next_level;
    }

    public Button getReplay() {
        return replay;
    }

    public Button getHome() {
        return home;
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
