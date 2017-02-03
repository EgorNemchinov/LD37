package com.jacktheogre.lightswitch.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jacktheogre.lightswitch.LightSwitch;
import com.jacktheogre.lightswitch.sprites.Button;

/**
 * Created by luna on 01.02.17.
 */

public abstract class GameScreen implements Screen {

    protected LightSwitch game;
    protected OrthographicCamera gameCam;
    protected Viewport gamePort;
    protected ShapeRenderer shapeRenderer;
    protected Array<Button> buttons;

    public GameScreen() {
        buttons = new Array<Button>();
    }

    protected abstract void initializeButtons();

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
        if(!game.batch.isDrawing())
            game.batch.begin();
        for (int i = 0; i < getButtons().size; i++) {
            getButtons().get(i).draw(game.batch);
        }
        game.batch.end();
    }

    public LightSwitch getGame() {
        return game;
    }

    public Array<Button> getButtons() {
        return buttons;
    }
}
