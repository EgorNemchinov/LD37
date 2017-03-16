package com.jacktheogre.lightswitch.tools.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;
import com.jacktheogre.lightswitch.screens.GeneratingScreen;
import com.jacktheogre.lightswitch.screens.MainMenuScreen;

/**
 * Created by luna on 14.12.16.
 */
public class MainMenuInputHandler implements InputProcessor {


    private final MainMenuScreen screen;

    public MainMenuInputHandler(MainMenuScreen mainMenuScreen) {
        this.screen = mainMenuScreen;
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.ENTER)
            screen.getPlayButton().press();
        if(keycode == Input.Keys.BACK) {
            screen.returnBack();
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if(keycode == Input.Keys.ENTER) {
            screen.getPlayButton().unpress();
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 screenTouch = new Vector3(screenX, screenY, 0);
        Vector3 point = screen.getGamePort().unproject(screenTouch.cpy());
        screen.touchDownButtons(point.x, point.y, pointer);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Vector3 screenTouch = new Vector3(screenX, screenY, 0);
        Vector3 point = screen.getGamePort().unproject(screenTouch.cpy());
        screen.touchUpButtons(point.x, point.y, pointer);
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Vector3 screenTouch = new Vector3(screenX, screenY, 0);
        Vector3 point = screen.getGamePort().unproject(screenTouch.cpy());
        screen.touchDraggedButtons(point.x, point.y, pointer);
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        Vector3 screenTouch = new Vector3(screenX, screenY, 0);
        Vector3 point = screen.getGamePort().unproject(screenTouch.cpy());
        screen.mouseMovedButtons(point.x, point.y);
        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
