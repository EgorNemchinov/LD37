package com.jacktheogre.lightswitch.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.jacktheogre.lightswitch.LightSwitch;
import com.jacktheogre.lightswitch.ai.LevelManager;
import com.jacktheogre.lightswitch.sprites.MapActor;
import com.jacktheogre.lightswitch.tools.Assets;
import com.jacktheogre.lightswitch.tools.ColorLoader;
import com.jacktheogre.lightswitch.tools.DrawingAssistant;


/**
 * Created by luna on 30.03.17.
 */

public class LevelChoosingScreen extends GameScreen {
    private final Color BACKGROUND = ColorLoader.colorMap.get("LEVEL_CHOOSING_SCREEN_BACKGROUND");
    private final int PREVIOUS_INDEX = 0;
    private final int CURRENT_INDEX = 1;
    private final int NEXT_INDEX = 2;

    public Stage stage;
    private ScreenActor screenActor;

    private Frame[] framesArray = new Frame[3];
    private Array<Frame> framesToBeDeleted = new Array<Frame>();;
    private Vector2 prevFramePosition, currentFramePosition, nextFramePosition;
    private float sideFramesScale = 0.5f;
    private int middleLevel = LevelManager.maxUnlockedLevel();

    private TextureRegion teleport, trap;

    private FPSLogger fpsLogger = new FPSLogger();

    public LevelChoosingScreen(LightSwitch game) {
        this.game = game;
        initializeStage();

        LevelManager.recountShards();
        initializeGraphicElements();
    }

    public LevelChoosingScreen(LightSwitch game, int middleLevel) {
        this.game = game;
        this.middleLevel = middleLevel;
        initializeStage();
        LevelManager.recountShards();
        initializeGraphicElements();
    }

    private void initializeStage() {
        stage = new Stage(gamePort, game.batch);
        Gdx.input.setInputProcessor(stage);
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
                } else if(keycode == Input.Keys.BACK || keycode == Input.Keys.BACKSPACE) {
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

    }

    class Frame extends Group {
        private static final int PRESSED_OFFSET_Y = 10;
        public static final float WIDTH = 200, HEIGHT = 200;

        private Table main;
        private Label levelLabel;
        public MapActor mapActor;

        private Image teleportImage, trapImage;
        private Label teleportLabel, trapLabel;
        private Button playButton;
        private int levelNumber;

        private ActorSettings targetSettings, currentSettings;
        private float time = 0.2f;
        private Vector2 deltaPerSec;
        private float scalePerSec;

        private boolean toBeDeleted;
        private boolean firstFrame;

        private boolean[] shardsCollected;
        private Image[] shards;
        private Group shardsImages;

        private Label shardsToUnlock;
        private Group shardsCounter;

        private boolean open; //determines whether level is playable
        private boolean pressed = false;
        private boolean drawing;

        public Frame(final int levelNumber) {
            firstFrame = true;
            drawing = true;
            open = LevelManager.isOpenLevel(levelNumber);
            this.setSize(Frame.WIDTH, Frame.HEIGHT);
            this.levelNumber = levelNumber;

            initializeTable();
            initializeShardsCount();
            this.addActor(main);
            main.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    if(!open)
                        return false;
                    if(0 <= x && x <= getWidth() && 0 <= y && y <= getHeight())
                        pressed = true;
                    else
                        return false;
                    return true;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    pressed = false;
                    if(0 <= x && x <= getWidth() && 0 <= y && y <= getHeight()) {
                        LevelManager.setLevelNum(levelNumber);
                        game.setScreen(new GeneratingScreen(game));
                    }
//                        Gdx.app.log("touchUp", x + ", " + y + ", size is "+getWidth() + "x" + getHeight());
                }
            });
            main.setTouchable(Touchable.enabled);
//            main.debug();

            shardsCollected = new boolean[LevelManager.countAmountOfShards(levelNumber)];
            shards = new Image[LevelManager.countAmountOfShards(levelNumber)];
            initializeCollectedShardsArray();
            initializeMoonShards();

//            main.setDebug(true);
            targetSettings = currentSettings = new ActorSettings(1f, getX(), getY());
        }

        private void initializeTable() {
            main = new Table();
            main.setSize(getWidth(), getHeight());
            mapActor = new MapActor(0.8f, Assets.getAssetLoader().maps[levelNumber], stage.getViewport());
            levelLabel = new Label("Level "+levelNumber, new Label.LabelStyle(Assets.getAssetLoader().font, ColorLoader.colorMap.get("LEVEL_LABEL_COLOR")));
            levelLabel.setFontScale(0.8f);
            levelLabel.setAlignment(Align.center);

            teleportImage = new Image(teleport);
            trapImage = new Image(trap);
            teleportLabel = new Label(LevelManager.getAmountOfTeleports(levelNumber)+"",
                    new Label.LabelStyle(Assets.getAssetLoader().font, ColorLoader.colorMap.get("RESOURSES_LABELS_COLOR")));
            trapLabel = new Label(LevelManager.getAmountOfTraps(levelNumber)+"",
                    new Label.LabelStyle(Assets.getAssetLoader().font, ColorLoader.colorMap.get("RESOURSES_LABELS_COLOR")));
            teleportLabel.setAlignment(Align.center);
//            teleportLabel.setFontScale(0.8f);
            trapLabel.setAlignment(Align.center);
//            trapLabel.setFontScale(0.8f);

            /*final TextureRegion playButtonTexture = Assets.getAssetLoader().start_button;
            TextureRegion usualButton, pressedButton;
            usualButton = new TextureRegion(playButtonTexture, open ? playButtonTexture.getRegionWidth() / 2 : 0, 0,
                    playButtonTexture.getRegionWidth() / 4, playButtonTexture.getRegionHeight());
            pressedButton = open ? new TextureRegion(playButtonTexture, playButtonTexture.getRegionWidth()*3 / 4, 0,
                    playButtonTexture.getRegionWidth() / 4, playButtonTexture.getRegionHeight()) : usualButton;
            playButton = new Button(new TextureRegionDrawable(usualButton), new TextureRegionDrawable(pressedButton));
            playButton.setBounds(playButton.getX(), playButton.getY(), playButton.getWidth(), playButton.getHeight());
            playButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
//                    Gdx.app.log("playButton", "clicked");
                    super.clicked(event, x, y);
                }

                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
//                    Gdx.app.log("playButton", "touchDown");
                    return true;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    LevelManager.setLevelNum(levelNumber);
                    game.setScreen(new GeneratingScreen(game));
                }
            });
            playButton.setTouchable(open? Touchable.enabled: Touchable.disabled);
*/
            main.align(Align.top | Align.center);
            main.add(levelLabel).expandX().colspan(4).row();
            main.padLeft(0);
            Cell<MapActor> cell = main.add(mapActor).expandX().colspan(4).padBottom(-5);
            cell.row();

            float cellScale = 0.5f;
            int cellHeight = (int) ((mapActor.getWidth() / 4)*cellScale);
            main.add(teleportImage).minHeight(cellHeight).fill(cellScale, cellScale);
            main.add(teleportLabel).maxHeight(cellHeight);
            main.add(trapImage).minHeight(cellHeight).fill(cellScale, cellScale);
            main.add(trapLabel).maxHeight(cellHeight).row();
//            main.padBottom(0);
//            main.add(playButton).colspan(4);
            main.moveBy((main.getWidth() - mapActor.getWidth() )/ 2, 0);
            main.setWidth(mapActor.getWidth());
        }

        public void setToBeDeleted(boolean toBeDeleted) {
            this.toBeDeleted = toBeDeleted;
            if(toBeDeleted)
                framesToBeDeleted.add(this);
        }

        public void setDrawing(boolean drawing) {
            this.drawing = drawing;
        }

        public void setTargetSettings(ActorSettings targetSettings) {
            this.targetSettings = targetSettings;
            deltaPerSec = new Vector2(targetSettings.position.cpy().sub(currentSettings.position).scl(1/time));
            scalePerSec = (targetSettings.scale - currentSettings.scale) / time;
        }

        @Override
        public void setPosition(float x, float y) {
            super.setPosition(x, y + (pressed && open ? -PRESSED_OFFSET_Y : 0));
            currentSettings.position = new Vector2(x, y);
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            if(drawing)
                super.draw(batch, parentAlpha);
        }

        @Override
        public void act(float delta) {
            if(deltaPerSec.hasSameDirection(currentSettings.position.cpy().sub(targetSettings.position))) {
                currentSettings.position = targetSettings.position;
                deltaPerSec = Vector2.Zero;
            }
            if(scalePerSec*(targetSettings.scale - currentSettings.scale) < 0) {
                currentSettings.scale = targetSettings.scale;
                scalePerSec = 0f;
            }
            if((currentSettings.position.epsilonEquals(targetSettings.position, 5) )
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
            shardsImages.setPosition(getX() + (main.getX() + main.getWidth())*getScaleY() - shardsImages.getScaleX()*shardsImages.getWidth(),
                    getY()+  getScaleY()*(main.getY() + main.getHeight() - levelLabel.getHeight()) - shardsImages.getHeight()*shardsImages.getScaleY());
            shardsCounter.setPosition(getX()+(main.getX() + main.getWidth() / 2)*getScaleX() - shardsCounter.getScaleX()*shardsCounter.getWidth() / 2,
                    getY() + getScaleY()*(main.getY() + main.getHeight() / 2) - shardsCounter.getScaleY()*shardsCounter.getHeight()/2 + 10);
//            mapActor.centerAt(mapActor.getX()+mapActor.getWidth() / 2, mapActor.getY()+mapActor.getHeight() / 2);
            super.act(delta);
        }

        private void initializeMoonShards() {
            shardsImages = new Group();
            shardsImages.setSize(14, 14);
            float scale = 1f;
            Vector2 leftBottomCorner = new Vector2(getX()+main.getX() + main.getWidth() - 40,
                    getY() + main.getY() + mapActor.getY() + mapActor.getHeight());
            // FIXME: 05.04.17 mb *scale
            if(shards.length == 3) {
                for (int i = 0; i < 3; i++) {
                    shards[i] = new Image(Assets.getAssetLoader().threeShards[i]);
                    if(shardsCollected[i])
                        shardsImages.addActor(shards[i]);
                }
                shardsImages.setPosition(leftBottomCorner.x ,leftBottomCorner.y);
                shardsImages.setScale(scale);
                shards[0].setPosition(- 1 , + 1 );
                shards[1].setPosition(+ 3 , - 3 );
                shards[2].setPosition(+ 5 , + 4 );
//                shards[0].setPosition(leftBottomCorner.x - 1*scale, leftBottomCorner.y + 1*scale);
//                shards[1].setPosition(leftBottomCorner.x + 3*scale, leftBottomCorner.y - 3*scale);
//                shards[2].setPosition(leftBottomCorner.x + 5*scale, leftBottomCorner.y + 4*scale);
            } else if(shards.length == 2) {
                for (int i = 0; i < 2; i++) {
                    shards[i] = new Image(Assets.getAssetLoader().twoShards[i]);
                    if(shardsCollected[i])
                        shardsImages.addActor(shards[i]);
                }
                shardsImages.setPosition(leftBottomCorner.x ,leftBottomCorner.y);
                shardsImages.setScale(scale);
                shards[0].setPosition(0 , 0 );
                shards[1].setPosition(+ 0 , - 1 );
//                shards[0].setPosition(leftBottomCorner.x + 0*scale, leftBottomCorner.y + 0*scale);
//                shards[1].setPosition(leftBottomCorner.x + 0.5f*scale, leftBottomCorner.y - 1f*scale);
            }
            for (int i = 0; i < shards.length; i++) {
                Image shard = shards[i];
                shard.setOrigin(shard.getX() - leftBottomCorner.x, shard.getY() - leftBottomCorner.y);
            }
        }

        public void initializeCollectedShardsArray() {
            String shardsStr = LevelManager.getCollectedShardsString(levelNumber);
            for (int i = 0; i < shardsCollected.length; i++) {
                shardsCollected[i] = false;
            }
            if(shardsStr.length() != 0) {
//                Gdx.app.log("LevelChoosingScreen", levelNumber+" - "+shardsStr);
                if(shardsStr.length() > shardsCollected.length)
                    Gdx.app.error("LevelChoosingScreen", shardsStr +", but shardsCollected length is "+shardsCollected.length );
                for (int i = 0; i < shardsStr.length(); i++) {
                    if(shardsStr.charAt(i) == '1')
                        shardsCollected[i] = true;
                    else {
                        shardsCollected[i] = false;
                    }
                }
            }
        }

        private void initializeShardsCount() {
            if(open) {
                shardsCounter = new Group();
                return;
            }
            shardsToUnlock = new Label(LevelManager.getTotalShardsCollected() +"/"+LevelManager.getAmountOfShardsToUnlock(levelNumber),
                    new Label.LabelStyle(Assets.getAssetLoader().font, ColorLoader.colorMap.get("LEVEL_LOCKED_SHARDS_TO_UNLOCK_LABEL")));
            shardsToUnlock.setSize(main.getWidth(), mapActor.getHeight() / 2); // FIXME: 09.04.17 set correct height
            shardsToUnlock.setAlignment(Align.center);
            shardsToUnlock.setFontScale(1.7f);
            shardsCounter = new Group();
            shardsCounter.setSize(shardsToUnlock.getWidth(), shardsToUnlock.getHeight());
            shardsCounter.addActor(shardsToUnlock);
            shardsCounter.setPosition(getX()+main.getX() + main.getWidth() / 2 - shardsToUnlock.getWidth() / 2,
                    getY() + main.getY() + mapActor.getY() + mapActor.getHeight() / 2);
        }

        @Override
        public void setScale(float scaleXY) {
            super.setScale(scaleXY);
            mapActor.setScale(scaleXY);
            shardsImages.setScale(2*scaleXY);
            shardsCounter.setScale(scaleXY);
            currentSettings.scale = scaleXY;
        }

        public int getLevelNumber() {
            return levelNumber;
        }

        public void drawBefore() {
            setDrawing(true);
            drawBackground();
        }

        public void drawAfter() {
            drawMap();
            boolean wasActive = stage.getBatch().isDrawing();
            if(!wasActive)
                stage.getBatch().begin();
            drawShards();
            drawShardsCounter();
            setDrawing(false);
            if(!wasActive)
                stage.getBatch().end();
        }

        private void drawBackground() {
            int borderSize = 2;
            shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
            shapeRenderer.setAutoShapeType(true);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(ColorLoader.colorMap.get("LEVEL_FRAME_STROKE"));
            DrawingAssistant.roundedRect(shapeRenderer, getX() + main.getX()*getScaleX(),
                    getY() + getScaleY()*(main.getY() + (pressed ? 0 : -PRESSED_OFFSET_Y)),
                    main.getWidth()*getScaleX(), getScaleY()*(main.getHeight() + (pressed ? 0 : PRESSED_OFFSET_Y)), 8);
            shapeRenderer.setColor(ColorLoader.colorMap.get("LEVEL_FRAME_BUTTON_SHADOW_COLOR"));
            if(!pressed)
                DrawingAssistant.roundedRect(shapeRenderer, getX() + main.getX()*getScaleX() + borderSize,
                    getY() + main.getY()*getScaleY() + borderSize - PRESSED_OFFSET_Y*getScaleY(),
                    main.getWidth()*getScaleX() - 2*borderSize, main.getHeight()*getScaleY() - 2*borderSize + PRESSED_OFFSET_Y*getScaleY(), 6);
            shapeRenderer.setColor(open ? ColorLoader.colorMap.get("LEVEL_FRAME_UNLOCKED_BACKGROUND")
                    : ColorLoader.colorMap.get("LEVEL_FRAME_LOCKED_BACKGROUND"));
            DrawingAssistant.roundedRect(shapeRenderer, getX() + main.getX()*getScaleX() + borderSize, getY() + main.getY()*getScaleY() + borderSize,
                    main.getWidth()*getScaleX() - 2*borderSize, main.getHeight()*getScaleY() - 2*borderSize, 6);
            shapeRenderer.end();
        }

        private void drawMap() {
            mapActor.act(0);
            if(!firstFrame) {
                mapActor.getMapRenderer().render();
            } else firstFrame = false;
        }

        private void drawShards() {
            shardsImages.draw(stage.getBatch(), 1f);
        }

        private void drawShardsCounter() {
            if(!open)
                shardsCounter.draw(game.batch, 1f);
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

        currentFramePosition = new Vector2(stage.getWidth() / 2 - Frame.WIDTH / 2, 25);
        prevFramePosition = new Vector2(currentFramePosition.x - Frame.WIDTH*sideFramesScale/ 2 - 20, stage.getHeight() / 2 - Frame.HEIGHT*sideFramesScale / 2);
        nextFramePosition = new Vector2(currentFramePosition.x + Frame.WIDTH - 20 , stage.getHeight() / 2 - Frame.HEIGHT*sideFramesScale / 2);

        if(middleLevel > 1)
            createFrameAtPosition(middleLevel - 1, PREVIOUS_INDEX);
        createFrameAtPosition(middleLevel, CURRENT_INDEX);
        if(middleLevel < LevelManager.LEVEL_AMOUNT)
            createFrameAtPosition(middleLevel + 1, NEXT_INDEX);
        screenActor = new ScreenActor(this);
        screenActor.setVisible(false);
//        stage.setDebugAll(true);
    }

    private void createFrameAtPosition(int levelNum, int screenPosition) {
        Frame frame = new Frame(levelNum);
        switch(screenPosition) {
            case PREVIOUS_INDEX:
                frame.setPosition(prevFramePosition.x, prevFramePosition.y);
                frame.setScale(sideFramesScale);
                frame.toBack();
                framesArray[0] = frame;
                break;
            case CURRENT_INDEX:
                frame.setPosition(currentFramePosition.x, currentFramePosition.y);
                frame.setScale(1);
                framesArray[1] = frame;
                break;
            case NEXT_INDEX:
                frame.setPosition(nextFramePosition.x, nextFramePosition.y);
                frame.setScale(sideFramesScale);
                frame.toBack();
                framesArray[2] = frame;
                break;
        }
        frame.setTargetSettings(frame.currentSettings);
        stage.addActor(frame);
    }

    private Frame nextFrame() {
        if(LevelManager.isMaxLevel(framesArray[1].getLevelNumber()))
            return null;
        Frame frame = new Frame(framesArray[1].getLevelNumber() + 1);
        frame.setPosition(nextFramePosition.x + 50, nextFramePosition.y + 30);
        frame.setScale(sideFramesScale / 2);
        frame.setTargetSettings(new ActorSettings(sideFramesScale, nextFramePosition.x, nextFramePosition.y));
        frame.toBack();
        return frame;
    }

    public  void nextLevel() {
        if(framesArray[framesArray.length - 1] == null) //already the last one
            return;
        if(!framesArray[framesArray.length - 1].open) //next level not avialable
            return;
        if(framesArray[0] != null) {
            framesArray[0].setTargetSettings(new ActorSettings(sideFramesScale / 2, prevFramePosition.x - 100, prevFramePosition.y + 30));
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
        frame.setPosition(prevFramePosition.x - 50, prevFramePosition.y + 30);
        frame.setScale(sideFramesScale / 2);
        frame.setTargetSettings(new ActorSettings(sideFramesScale, prevFramePosition.x, prevFramePosition.y));
        frame.toBack();
        return frame;
    }

    public void previousLevel() {
        if(framesArray[0] == null) //already first level in the center
            return;
        if(framesArray[2] != null) {
            framesArray[2].setTargetSettings(new ActorSettings(sideFramesScale / 2, nextFramePosition.x + 100, nextFramePosition.y + 30));
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
        Gdx.gl.glClearColor(BACKGROUND.r, BACKGROUND.g, BACKGROUND.b, BACKGROUND.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

//        fpsLogger.log();
        drawFrames(delta);
    }

    private void drawFrames(float delta) {
        stage.act(delta);
        for (Frame frame :framesArray) {
            if(frame != null)
                frame.setDrawing(false);
        }

        //drawing disappearing frames
        for(Frame frame: framesToBeDeleted) {
            frame.drawBefore();
        }
        stage.draw();
        for(Frame frame: framesToBeDeleted) {
            frame.drawAfter();
        }

        //drawing side frames
        Frame frameLeft = framesArray[0];
        if(frameLeft != null) {
            frameLeft.drawBefore();
        }
        Frame frameRight = framesArray[2];
        if(frameRight != null) {
            frameRight.drawBefore();
        }
        stage.draw();
        if(frameLeft != null) {
            frameLeft.drawAfter();
        }
        if(frameRight != null) {
            frameRight.drawAfter();
        }

        //middle one
        Frame frameMiddle = framesArray[1];
        if(frameMiddle != null) {
            frameMiddle.drawBefore();
        }
        stage.draw();
        if(frameMiddle != null) {
            frameMiddle.drawAfter();
        }
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
