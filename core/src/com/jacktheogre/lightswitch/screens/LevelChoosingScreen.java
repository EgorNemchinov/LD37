package com.jacktheogre.lightswitch.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.jacktheogre.lightswitch.LightSwitch;
import com.jacktheogre.lightswitch.ai.LevelManager;
import com.jacktheogre.lightswitch.tools.Assets;

import java.util.*;


/**
 * Created by luna on 30.03.17.
 */

public class LevelChoosingScreen extends GameScreen {

    public Stage stage;
    private ScreenActor screenActor;

    private Skin touchpadSkin;
    private Touchpad.TouchpadStyle touchpadStyle;
    private Touchpad touchpad;

    private Frame[] framesArray;
    private Array<Frame> framesToBeDeleted;
    private Vector2 prevFramePosition, currentFramePosition, nextFramePosition;
    private float sideFramesScale = 0.6f;
    private int frameWidth, frameHeight;

    private TextureRegion teleport, trap;


    public LevelChoosingScreen(LightSwitch game) {
        this.game = game;
        gamePort = new FitViewport(LightSwitch.WIDTH, LightSwitch.HEIGHT);
        gameCam = new OrthographicCamera(gamePort.getWorldWidth(), gamePort.getWorldHeight());
        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);
        gameCam.zoom = 1f;
        stage = new Stage(gamePort, game.batch);
        stage.addListener(new InputListener() {
            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                if(keycode == Input.Keys.RIGHT) {
                    screenActor.screen.nextLevel();
                }
                else if(keycode == Input.Keys.LEFT) {
                    screenActor.screen.previousLevel();
                } else if(keycode == Input.Keys.ENTER) {
                    LevelManager.setLevelNum(screenActor.screen.framesArray[1].getLevelNumber());
                    screenActor.screen.getGame().setScreen(new GeneratingScreen(screenActor.screen.getGame()));
                } else if(keycode == Input.Keys.BACK) {
                    screenActor.screen.getGame().setScreen(new MainMenuScreen(screenActor.screen.getGame()));
                }
                return true;
            }
        });
        stage.addListener(new ActorGestureListener() {
            @Override
            public void fling(InputEvent event, float velocityX, float velocityY, int button) {
//                Gdx.app.log("fling", velocityX+" - "+velocityY);
                if(Math.abs(velocityX) > 100) {
                    if(velocityX < 0)
                        screenActor.screen.nextLevel();
                    else if(velocityX > 0)
                        screenActor.screen.previousLevel();
                }
            }
        });
        Gdx.input.setCatchBackKey(true);
        framesArray = new Frame[3];
        framesToBeDeleted = new Array<Frame>();
        frameWidth = (int)stage.getWidth() / 2;
        frameHeight = (int) stage.getHeight() - 30;

        initializeGraphicElements();
        Gdx.input.setInputProcessor(stage);
    }

    class Frame extends Group {
        private Table main;
        private Label levelLabel;
        public MapActor mapActor;

        private Image teleportImage, trapImage;
        private Label teleportLabel, trapLabel;
        private Button playButton;
        private int levelNumber;

        private ActorSettings targetSettings, currentSettings;
        private float time = 0.3f;
        private Vector2 deltaPerSec;
        private float scalePerSec;

        private boolean toBeDeleted;

        public Frame(final int levelNumber) {
            this.setSize(frameWidth, frameHeight);
            this.levelNumber = levelNumber;
            main = new Table();
            main.setSize(getWidth(), getHeight());
//            main.setPosition(50, 0);
            mapActor = new MapActor(0.8f, Assets.getAssetLoader().maps[levelNumber]);
            levelLabel = new Label("Level "+levelNumber, new Label.LabelStyle(Assets.getAssetLoader().font, Color.WHITE));
            levelLabel.getStyle().font.getData().setScale(1f);
            levelLabel.setAlignment(Align.center);

            teleportImage = new Image(teleport);
            trapImage = new Image(trap);
            teleportLabel = new Label(LevelManager.getAmountOfTeleports(levelNumber)+"", new Label.LabelStyle(Assets.getAssetLoader().font, Color.WHITE));
            trapLabel = new Label(LevelManager.getAmountOfTraps(levelNumber)+"", new Label.LabelStyle(Assets.getAssetLoader().font, Color.WHITE));
            teleportLabel.setAlignment(Align.center);
            teleportLabel.setFontScale(1.3f);
            trapLabel.setAlignment(Align.center);
            trapLabel.setFontScale(1.3f);

            final TextureRegion playButtonTexture = Assets.getAssetLoader().start_button;
            playButton = new Button(new TextureRegionDrawable(new TextureRegion(playButtonTexture, playButtonTexture.getRegionWidth() / 2, 0,
                    playButtonTexture.getRegionWidth() / 4, playButtonTexture.getRegionHeight())),
                    new TextureRegionDrawable(new TextureRegion(playButtonTexture, playButtonTexture.getRegionWidth()*3 / 4, 0,
                    playButtonTexture.getRegionWidth() / 4, playButtonTexture.getRegionHeight())));
            playButton.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    LevelManager.setLevelNum(levelNumber);
                    game.setScreen(new GeneratingScreen(game));
                }
            });

            main.align(Align.top | Align.center);
            main.add(levelLabel).expandX().colspan(4).row();
            main.padLeft(0);
            Cell<MapActor> cell = main.add(mapActor).expandX().colspan(4);
            cell.row();
//            main.padLeft(0);

            int cellHeight = (int) ((mapActor.getWidth() / 4)*0.7f);
            main.add(teleportImage).minHeight(cellHeight).fill(0.7f, 1f);
            main.add(teleportLabel);
            main.add(trapImage).fill(0.7f, 1f);
            main.add(trapLabel).row();
            main.padBottom(10);
            main.add(playButton).colspan(4);
            main.moveBy((main.getWidth() - mapActor.getWidth() )/ 2, 0);
            main.setWidth(mapActor.getWidth());
            this.addActor(main);

//            main.setDebug(true);
            targetSettings = currentSettings = new ActorSettings(1f, getX(), getY());
        }

        public void setToBeDeleted(boolean toBeDeleted) {
            this.toBeDeleted = toBeDeleted;
            if(toBeDeleted)
                framesToBeDeleted.add(this);

        }

        public void setTargetSettings(ActorSettings targetSettings) {
            this.targetSettings = targetSettings;
            deltaPerSec = new Vector2(targetSettings.position.cpy().sub(currentSettings.position).scl(1/time));
            scalePerSec = (targetSettings.scale - currentSettings.scale) / time;
        }

        @Override
        public void setPosition(float x, float y) {
            super.setPosition(x, y);
            currentSettings.position = new Vector2(x, y);
        }

        @Override
        public void act(float delta) {
            if((currentSettings.position.epsilonEquals(targetSettings.position, 5) || deltaPerSec.hasSameDirection(currentSettings.position.cpy().sub(targetSettings.position)) )
                    && Math.abs(targetSettings.scale - currentSettings.scale) < 0.05f) {
                if(currentSettings != targetSettings) {
                    currentSettings = targetSettings;
                    setScale(currentSettings.scale);
                    deltaPerSec = Vector2.Zero;
                    if(toBeDeleted) {
                        stage.getActors().removeValue(this, true);
                        framesToBeDeleted.removeValue(this, true);
                    }
                }
            } else {
                currentSettings.position.add(deltaPerSec.cpy().scl(delta)); //or .nor().scl(coef);
                currentSettings.scale += scalePerSec*delta;
                setScale(currentSettings.scale);
            }
            setPosition(currentSettings.position.x, currentSettings.position.y);
            mapActor.centerAt(getX()+main.getX()*getScaleX()+(main.getWidth() / 2),
                    getY() +main.getY() - mapActor.getHeight()*(getScaleY() - 0.5f) +main.getHeight()*getScaleY()- (levelLabel.getHeight() + 5)*getScaleY());
//            mapActor.centerAt(mapActor.getX()+mapActor.getWidth() / 2, mapActor.getY()+mapActor.getHeight() / 2);
            super.act(delta);
        }

        @Override
        public void setScale(float scaleXY) {
            super.setScale(scaleXY);
            mapActor.setScale(scaleXY);
            currentSettings.scale = scaleXY;
        }

        public int getLevelNumber() {
            return levelNumber;
        }

        public void drawMap() {
            mapActor.act(0);
            mapActor.getMapRenderer().render();
        }
    }

    class MapActor extends Actor {

        private OrthogonalTiledMapRenderer mapRenderer;
        private OrthographicCamera camera;
        private TiledMap map;

        float initialScale;

        public MapActor(float scale, TiledMap map) {
            initialScale = scale;
            this.map = map;
            LevelManager.loadLevel(map);
            mapRenderer = new OrthogonalTiledMapRenderer(map, initialScale);
            this.setSize(LevelManager.lvlPixelWidth*scale, LevelManager.lvlPixelHeight*scale);
            camera = new OrthographicCamera(gamePort.getWorldWidth(), gamePort.getWorldHeight());
            camera.position.set(stage.getCamera().position.x - getX(), stage.getCamera().position.y - getY(), 0);
        }

        public void centerAt(float x, float y) {
            setPosition(x - getWidth() / 2, y - getHeight() / 2);
        }

        @Override
        public void setScale(float scaleXY) {
            LevelManager.loadLevel(map);
            mapRenderer = new OrthogonalTiledMapRenderer(map, initialScale*scaleXY);
        }

        @Override
        public void act(float delta) {
            syncCamera((int)getX(), (int)getY());
            mapRenderer.setView(camera);
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {

//            mapRenderer.render();
//            super.draw(batch, parentAlpha);
        }

        public OrthogonalTiledMapRenderer getMapRenderer() {
            return mapRenderer;
        }

        private void syncCamera(int x, int y) {
            camera.position.x = stage.getCamera().position.x - x;
            camera.position.y = stage.getCamera().position.y - y;
            camera.update();
        }
    }

    class ActorSettings {
        private float scale;
        private Vector2 position;

        public ActorSettings(float scale, float x, float y) {
            this.scale = scale;
            this.position = new Vector2(x, y);
        }
    }

    class ScreenActor extends Actor {
        private LevelChoosingScreen screen;
        public ScreenActor(LevelChoosingScreen screen) {
            this.screen = screen;
        }
    }

    private void initializeGraphicElements() {
        teleport = new TextureRegion(Assets.getAssetLoader().teleport);
        teleport = new TextureRegion(teleport, teleport.getRegionWidth()*3 / 4, 0, teleport.getRegionWidth() / 4, teleport.getRegionHeight());
        trap = new TextureRegion(Assets.getAssetLoader().trap);
        trap = new TextureRegion(trap, trap.getRegionWidth()*3 / 4, 0, trap.getRegionWidth() / 4, trap.getRegionHeight());
/*        Actor backgroundFill = new Actor() {
            @Override
            public void draw(Batch batch, float parentAlpha) {
                batch.end();
                shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(Color.BLUE);
                shapeRenderer.rect(this.getX(), this.getY(), getWidth(), getHeight());
                shapeRenderer.end();
                batch.begin();
            }
        };*/
//        Gdx.app.log("Actors", frame.getChildren().size +"");
        prevFramePosition = new Vector2(stage.getWidth() / 2 - frameWidth, stage.getHeight() / 2 - frameHeight*0.3f);
        currentFramePosition = new Vector2(stage.getWidth() / 4, 15);
        nextFramePosition = new Vector2(stage.getWidth() / 2 + frameWidth/ 2, stage.getHeight() / 2 - frameHeight*0.3f);
        Frame frame1 = new Frame(1);
        frame1.setPosition(currentFramePosition.x, currentFramePosition.y);
        frame1.setTargetSettings(frame1.currentSettings);
        frame1.setScale(1f);
        Frame frame2 = new Frame(2);
        frame2.setPosition(nextFramePosition.x, nextFramePosition.y);
        frame2.setTargetSettings(frame2.currentSettings);
        frame2.setScale(sideFramesScale);
        frame2.toBack();
        stage.addActor(frame1);
        stage.addActor(frame2);
        screenActor = new ScreenActor(this);
        screenActor.setVisible(false);

        framesArray[1] = frame1;
        framesArray[2] = frame2;
    }

    private Frame nextFrame() {
        if(LevelManager.isMaxLevel(framesArray[1].getLevelNumber()))
            return null;
        Frame frame = new Frame(framesArray[1].getLevelNumber() + 1);
        frame.setPosition(nextFramePosition.x + 100, nextFramePosition.y + 30);
        frame.setScale(sideFramesScale / 2);
        frame.setTargetSettings(new ActorSettings(sideFramesScale, nextFramePosition.x, nextFramePosition.y));
        frame.toBack();
        return frame;
    }

    public  void nextLevel() {
        if(framesArray[framesArray.length - 1] == null) //already the last one
            return;
        if(framesArray[0] != null) {
            framesArray[0].setTargetSettings(new ActorSettings(sideFramesScale / 2, framesArray[0].getX() - 100, framesArray[0].getY() + 30));
            framesArray[0].setToBeDeleted(true);
        }
        framesArray[0] = null;
        for (int i = 1; i < framesArray.length; i++) {
            framesArray[i - 1] = framesArray[i];
        }
        framesArray[0].setTargetSettings(new ActorSettings(sideFramesScale, prevFramePosition.x, prevFramePosition.y));
        framesArray[1].setTargetSettings(new ActorSettings(1f, currentFramePosition.x, currentFramePosition.y));
        Frame next = nextFrame();
        if(next == null) {
            framesArray[2] = null;
            return;
        }
        framesArray[2] = next;
        stage.addActor(next);
    }

    private Frame previousFrame() {
        if(1 == framesArray[1].getLevelNumber())
            return null;
        Frame frame = new Frame(framesArray[1].getLevelNumber() - 1);
        frame.setPosition(prevFramePosition.x - 100, prevFramePosition.y + 30);
        frame.setScale(sideFramesScale / 2);
        frame.setTargetSettings(new ActorSettings(sideFramesScale, prevFramePosition.x, prevFramePosition.y));
        frame.toBack();
        return frame;
    }

    public void previousLevel() {
        if(framesArray[0] == null) //already first level in the center
            return;
        if(framesArray[2] != null) {
            framesArray[2].setTargetSettings(new ActorSettings(sideFramesScale / 2, framesArray[2].getX() + 100, framesArray[2].getY() + 30));
            framesArray[2].setToBeDeleted(true);
        }
        framesArray[2] = null;
        for (int i = framesArray.length- 2; i >= 0; i--) {
            framesArray[i + 1] = framesArray[i];
        }
        framesArray[2].setTargetSettings(new ActorSettings(sideFramesScale, nextFramePosition.x, nextFramePosition.y));
        framesArray[1].setTargetSettings(new ActorSettings(1f, currentFramePosition.x, currentFramePosition.y));
        Frame prev = previousFrame();
        if(prev == null) {
            framesArray[0] = null;
            return;
        }
        framesArray[0] = prev;
        stage.addActor(prev);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
        renderMaps();
    }

    private void renderMaps() {
        for(Frame frame: framesToBeDeleted) {
            frame.drawMap();
        }
        Frame frame = framesArray[0];
        if(frame != null)
            frame.drawMap();
        frame = framesArray[2];
        if(frame != null)
            frame.drawMap();
        frame = framesArray[1];
        if(frame != null)
            frame.drawMap();
    }

    @Override
    protected void initializeButtons() {

    }

    @Override
    public void dispose() {

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
}
