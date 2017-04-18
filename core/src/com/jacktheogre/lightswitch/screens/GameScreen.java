package com.jacktheogre.lightswitch.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jacktheogre.lightswitch.LightSwitch;
import com.jacktheogre.lightswitch.sprites.Button;
import com.jacktheogre.lightswitch.tools.AutoScreenViewport;
import com.jacktheogre.lightswitch.tutorials.Highlighter;
import com.jacktheogre.lightswitch.tutorials.TutorialTelegraph;

/**
 * Created by luna on 01.02.17.
 */

public abstract class GameScreen implements Screen {

    protected LightSwitch game;
    protected OrthographicCamera gameCam;
    protected Viewport gamePort, fillGamePort;
    protected ShapeRenderer shapeRenderer;
    protected Array<Button> buttons;

    protected Highlighter highlighter;

    public GameScreen() {
        buttons = new Array<Button>();
        shapeRenderer = new ShapeRenderer();
        gameCam = new OrthographicCamera();

//        gamePort = new MyFitViewport(LightSwitch.WIDTH, LightSwitch.HEIGHT, gameCam);
        AutoScreenViewport viewport = new AutoScreenViewport(LightSwitch.WIDTH, LightSwitch.HEIGHT, gameCam);
        gamePort = viewport;
//        gamePort.setWorldSize(LightSwitch.WIDTH, LightSwitch.HEIGHT);
        fillGamePort = new StretchViewport(LightSwitch.WIDTH, LightSwitch.HEIGHT, gameCam);

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(gameCam.combined);
        shapeRenderer.setAutoShapeType(true);

        highlighter = new Highlighter(this);
        TutorialTelegraph.getInstance().setScreen(this);
        MessageManager.getInstance().clearQueue();
    }

    protected void notifyTutorialTelegraph(){
        getGamePort().apply();
    }

    protected abstract void initializeButtons();

    public void update(float dt) {
        for (int i = 0; i < getButtons().size; i++) {
            getButtons().get(i).update(dt);
        }
        GdxAI.getTimepiece().update(dt);
        MessageManager.getInstance().update();
//        gamePort.apply();
    }

    public void touchDownButtons(float x, float y, int pointer) {
        for (int i = 0; i < getButtons().size; i++) {
            Button button = getButtons().get(i);
            if(button.getBoundingRectangle().contains(x, y)) {
                if(button.press())
                    button.setPointer(pointer);
            }
        }
    }

    public void touchUpButtons(float x, float y, int pointer) {
        for (int i = 0; i < getButtons().size; i++) {
            Button button = getButtons().get(i);
            if(button.getBoundingRectangle().contains(x, y) && button.getPointer() == pointer && button.isAutoUnpress()) {
                button.unpress();
            }
        }
    }

    public void touchDraggedButtons(float x, float y, int pointer) {
        for (int i = 0; i < getButtons().size; i++) {
            Button button = getButtons().get(i);
            if(button.getPointer() == pointer) {
                if(!button.getBoundingRectangle().contains(x, y) && button.isAutoUnpress() && button.isPressed())
                    button.undoPressing();
                else if(button.getBoundingRectangle().contains(x, y) && button.isAutoUnpress() && !button.isPressed())
                    button.redoPressing();
            }
        }
    }

    public void mouseMovedButtons(float x, float y) {
        for (int i = 0; i < getButtons().size; i++) {
            Button button = getButtons().get(i);
            if(button.getBoundingRectangle().contains(x, y)) {
                button.focused();
            } else {
                button.unfocused();
            }
        }
    }

    public void renderButtons(Camera camera) {
        game.batch.setProjectionMatrix(camera.combined);
        boolean wasDrawing = game.batch.isDrawing();
        if(!wasDrawing)
            game.batch.begin();
        for (int i = 0; i < getButtons().size; i++) {
            getButtons().get(i).draw(game.batch);
        }
        if(!wasDrawing)
            game.batch.end();
    }

    public Viewport getFillGamePort() {
        return fillGamePort;
    }

    public Highlighter getHighlighter() {
        return highlighter;
    }

    public LightSwitch getGame() {
        return game;
    }

    public Viewport getGamePort() {
        return gamePort;
    }

    public OrthographicCamera getGameCam() {
        return gameCam;
    }

    public ShapeRenderer getShapeRenderer() {
        return shapeRenderer;
    }

    public SpriteBatch getSpriteBatch() {
        return game.batch;
    }

    public Array<Button> getButtons() {
        return buttons;
    }

    @Override
    public void resize(int width, int height) {
        fillGamePort.update(width, height);
        gamePort.update(width, height);
    }

    public void setHighlighter(Highlighter highlighter) {
        this.highlighter = highlighter;
    }
}
