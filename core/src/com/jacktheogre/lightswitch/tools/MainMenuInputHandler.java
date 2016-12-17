package com.jacktheogre.lightswitch.tools;

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
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if(keycode == Input.Keys.ENTER) {
            screen.getPlayButton().touchUp();
            screen.getGame().setScreen(new GeneratingScreen(screen.getGame()));
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 screenTouch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        Vector3 point = screen.getGamePort().unproject(screenTouch.cpy());
        if(screen.getPlayButton().getBoundingRectangle().contains(point.x, point.y))
            screen.getPlayButton().press();
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Vector3 screenTouch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        Vector3 point = screen.getGamePort().unproject(screenTouch.cpy());
        if(screen.getPlayButton().getBoundingRectangle().contains(point.x,point.y)) {
            screen.getPlayButton().touchUp();
            screen.getGame().setScreen(new GeneratingScreen(screen.getGame()));
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        Vector3 screenTouch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        Vector3 point = screen.getGamePort().unproject(screenTouch.cpy());
        if(screen.getPlayButton().getBoundingRectangle().contains(point.x,point.y)) {
            screen.getPlayButton().focused();
        } else {
            screen.getPlayButton().unfocused();
        }
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
