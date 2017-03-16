package com.jacktheogre.lightswitch.tools.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;
import com.jacktheogre.lightswitch.ai.LevelManager;
import com.jacktheogre.lightswitch.screens.ConnectingScreen;
import com.jacktheogre.lightswitch.screens.MainMenuScreen;

/**
 * Created by luna on 13.02.17.
 */

public class ConnectInputHandler implements InputProcessor {
    private ConnectingScreen screen;

    public ConnectInputHandler(ConnectingScreen screen) {
        this.screen = screen;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.BACK:
                MainMenuScreen mainMenuScreen = new MainMenuScreen(screen.getGame());
                mainMenuScreen.setState(MainMenuScreen.State.CHOOSING_CHARACTER);
                screen.getGame().setScreen(mainMenuScreen);
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 screenTouch = new Vector3(screenX, screenY, 0);
        Vector3 point = screen.getGamePort().unproject(screenTouch.cpy());
        screenTouch.y = screen.getGamePort().getScreenHeight() - screenTouch.y;
        screen.touchDownButtons(point.x, point.y, pointer);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Vector3 screenTouch = new Vector3(screenX, screenY, 0);
        Vector3 point = screen.getGamePort().unproject(screenTouch.cpy());
        screenTouch.y = screen.getGamePort().getScreenHeight() - screenTouch.y;
        screen.touchUpButtons(point.x, point.y, pointer);
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Vector3 screenTouch = new Vector3(screenX, screenY, 0);
        Vector3 point = screen.getGamePort().unproject(screenTouch.cpy());
        screenTouch.y = screen.getGamePort().getScreenHeight() - screenTouch.y;
        screen.touchDraggedButtons(point.x, point.y, pointer);
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        Vector3 screenTouch = new Vector3(screenX, screenY, 0);
        Vector3 point = screen.getGamePort().unproject(screenTouch.cpy());
        screenTouch.y = screen.getGamePort().getScreenHeight() - screenTouch.y;
        screen.mouseMovedButtons(point.x, point.y);
        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
