package com.jacktheogre.lightswitch.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jacktheogre.lightswitch.LightSwitch;
import com.jacktheogre.lightswitch.ai.LevelManager;
import com.jacktheogre.lightswitch.objects.InteractiveObject;
import com.jacktheogre.lightswitch.sprites.Button;
import com.jacktheogre.lightswitch.tools.AssetLoader;
import com.jacktheogre.lightswitch.tools.Assets;
import com.jacktheogre.lightswitch.tools.ColorLoader;
import com.jacktheogre.lightswitch.tools.input.GameOverInputHandler;

import static com.jacktheogre.lightswitch.screens.GameOverScreen.State.WIN;

/**
 * Created by luna on 10.12.16.
 */
public class GameOverScreen extends GameScreen{

    private final Color BACKGROUND_COLOR = ColorLoader.colorMap.get("GAMEOVER_SCREEN_BACKGROUND");

    private static final float INTERVAL = 10;
    private static final float SCALE = 1.5f;

    public State getState() {
        return state;
    }

    public enum State {WIN, LOSE;}
    private State state;
    private Button next_level, replay, home;
    private Array<InteractiveObject> interactiveObjects;
    private Label result;

    public GameOverScreen(LightSwitch game, State state) {
        super();
        this.game = game;
        this.state = state;
        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(LightSwitch.WIDTH, LightSwitch.HEIGHT, gameCam);
        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);
        initializeButtons();
        result = new Label(state == WIN ? "YOU WIN!" : "GAME OVER",
                new Label.LabelStyle(Assets.getAssetLoader().font, ColorLoader.colorMap.get("GAMEOVER_LABELS_COLOR")));
        if(LevelManager.isMaxLevel() && state == WIN)
            result.setText("CONGRATULATIONS!");
        result.setAlignment(Align.center);
        result.setFontScale(1.2f);
        result.setSize(gamePort.getWorldWidth() * 0.6f, gamePort.getWorldHeight() / 2);
        result.setPosition(gamePort.getWorldWidth() / 2 - result.getWidth() / 2,
                gamePort.getWorldHeight() / 2 - result.getHeight() / 2);

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
        Gdx.gl.glClearColor(BACKGROUND_COLOR.r, BACKGROUND_COLOR.g, BACKGROUND_COLOR.b, BACKGROUND_COLOR.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
//        Assets.getAssetLoader().font.getData().setScale(AssetLoader.FONT_SCALE);
        result.draw(game.batch, 1f);
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

        boolean nextLevelActive = !LevelManager.isMaxLevel() && state == WIN && LevelManager.isOpenLevel(LevelManager.getLevelNum() + 1);
        next_level = new Button(Assets.getAssetLoader().next_level_button, nextLevelActive ? Button.State.ACTIVE : Button.State.DISABLED, this) {
            //fixme: actUnpress called even when buton is disabled
            @Override
            protected void actUnpress() {
                if(!LevelManager.isMaxLevel() && !disabled)
                {
                    LevelManager.setLevel(LevelManager.getLevelNum() + 1);
                    screen.getGame().setScreen(new GeneratingScreen(screen.getGame()));
                }
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
